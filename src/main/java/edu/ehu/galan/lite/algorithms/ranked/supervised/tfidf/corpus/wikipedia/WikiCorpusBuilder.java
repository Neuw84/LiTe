package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.wikipedia;

/*
 *    WikiCorpusBuilder.java
 *    Copyright (C) 2013 Angel Conde, neuw84 at gmail dot com
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
import edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.analyzers.AnalyzerEnglish;
import edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.lucene.VecTextField;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * WikiCorpusBuilder Class builds a Lucene index of a entire wikipedia dump processed with
 * wikiExtractor(http://medialab.di.unipi.it/wiki/Wikipedia_Extractor) tool. (no compression) 1GB
 * ram, required for index creation
 *
 * @author Angel Conde Manjon
 */
public class WikiCorpusBuilder {

    private long numWords;
    private static int numDocs;
    List<Document> docList;
    IndexWriter writer;
    private static long limit;

    /**
     *
     */
    public WikiCorpusBuilder() {
        docList = new ArrayList<>();
        numWords = 0;
        limit = 0;
    }

    /**
     * Builds a Lucene index containing the Wikipedia data using a wikiExtractor folder with
     * wikipedia articles (no compression), wikiExtractor, the parameters are, the number of words
     * that a document needs to be taken into account (integer), the directory where the Lucene
     * index will be built, the directory where the WikiExtractor files are.
     *
     * @param args
     */
    public static void main(String[] args) {
        WikiCorpusBuilder doc = new WikiCorpusBuilder();
        limit = 700;
        doc.initializeIndex("/home/angel/wikipedia/indexEU700");
        doc.extract(new File("/home/angel/wikipedia/extractedEU"));
        System.gc();

    }

    public void buildIndex(String pIndexDir, String wikiExtractorDir, int wordsLimit) {
        limit = wordsLimit;
        initializeIndex(pIndexDir);
        extract(new File(wikiExtractorDir));
        System.gc();
    }

    private List<Document> extract(File file) {
        // do not try to index files that cannot be read
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    File[] listFiles = file.listFiles();
                    for (int i = 0; i < file.listFiles().length; i++) {
                        if (listFiles[i] != null) {
                            extractDirectory2(listFiles[i]);
                        }
                    }
                }
            }

        }
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(WikiCorpusBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docList;
    }

    private void print() {
        for (Document document : docList) {
            System.out.println(document.getField("term").stringValue());
            System.out.println(document.getField("contents").stringValue());
        }
    }

    private void extractDirectory2(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (int i = 0; i < file.listFiles().length; i++) {
                File fil = listFiles[i];
                if (fil != null) {
                    try {
                        String sb = new String();
                        String line;
                        Document doc = null;
                        List<String> readLines;
                        readLines = FileUtils.readLines(fil, StandardCharsets.UTF_8.name());
                        for (int o = 0; o < readLines.size(); o++) {
                            line = readLines.get(o);
                            if (line.matches("<doc .*?>.*?")) {
                                String[] split = line.split("<.*>");
                                doc = new Document();
                                // Add the path of the file as a field named "path".  Use a
                                // field that is indexed (i.e. searchable), but don't tokenize 
                                // the field into separate words and don't index term frequency
                                // or positional information:
                                Field pathField = null;
                                if (split.length == 2) {
                                    pathField = new StringField("term", split[1], Field.Store.YES);
                                } else {
                                    //TODO: check new version of wikiextractor that put the title in the next line
                                    pathField = new StringField("term", readLines.get(o + 1).trim(), Field.Store.YES);
                                    System.out.println(line);
                                }

                                doc.add(pathField);
                                if (split.length == 2) {
                                    sb += split[1];
                                }

                            } else if (line.matches("</doc>")) {
                                // Add the contents of the file to a field named "contents".  Specify a Reader,
//                                doc.add(new VecTextField("contents", sb.toString(), Field.Store.YES));

                                numDocs++;
                                int l = sb.split("\\s+").length;
                                numWords = l;
                                if (numWords >= limit) {
                                    System.out.println(numDocs + " lines of doc " + l);

                                    doc.add(new VecTextField("contents", sb, Field.Store.YES));
                                    indexDocs(writer, doc);
                                }
//                        docList.add(doc);
                                sb = new String();
//                        System.out.println(doc.getField("term").stringValue());
//                            System.out.println(file.getName());

                            } else {
                                sb += (line);
                            }

                        }
                    } catch (IOException ex) {
                        Logger.getLogger(WikiCorpusBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Indexes the given file using the given writer, or if a directory is given, recurses over
     * files and directories found under the given directory.
     *
     *
     * @param writer Writer to the index where the given file/dir info will be stored
     * @param file The file to index, or the directory to recurse into to find files to index
     * @throws IOException
     */
    static void indexDocs(IndexWriter writer, Document doc) {

        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            try {

                writer.addDocument(doc);
            } catch (IOException ex1) {
                Logger.getLogger(WikiCorpusBuilder.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } else {
        }
    }

    private void initializeIndex(String index) {
        final File docDir = new File(index);

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + docDir.getAbsolutePath() + "'...");

            Directory dir = FSDirectory.open(new File(index));
            Analyzer analyzer = new AnalyzerEnglish(Version.LUCENE_40);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40, analyzer);
////      in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            iwc.setRAMBufferSizeMB(1024.0);
            writer = new IndexWriter(dir, iwc);

            // NOTE: if you want to maximize search performance,
            // you can optionally call forceMerge here.  This can be
            // a terribly costly operation, so generally it's only
            // worth it when your index is relatively static (ie
            // you're done adding documents to it):
            //
//             writer.forceMerge(1);
            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }
    }

    private int countLines(String str) {
        String[] lines = str.split("\r\n|\r|\n");
        return lines.length;
    }
}
