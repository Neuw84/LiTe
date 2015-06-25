package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.plain;

/*
 *    PlainCorpusBuilder.java
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
import edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.analyzers.AnalyzerSpanish;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * PlainCorpusBuilder class builds a Lucene index given a set of plain text files extracted with
 * WIkiExtractor, 1GB ram required for index creation (check the xmx and xms parameters of your jvm
 *
 * @author Angel Conde Manjon
 */
public class PlainCorpusBuilder {

    private long numWords;
    private static int numDocs;
    List<Document> docList;
    IndexWriter writer;
    private static long limit;

    /**
     *
     */
    private PlainCorpusBuilder() {
        docList = new ArrayList<>();
        numWords = 0;
        limit = 0;
    }

    /**
     * Builds a lucene index containing the wikipedia data using a wikiExtractor folder with
     * wikipedia articles (no compression), wikiExtractor, the directories can be specified via
     * args, first argument will be the lucene index file
     *
     * @param args
     */
    public static void main(String[] args) {
        PlainCorpusBuilder doc = new PlainCorpusBuilder();
        limit = 700;
        doc.initializeIndex("/home/angel/wikipedia/indexEs700");
        doc.extract(new File("/home/angel/wikiextractor/extractedES"));
        System.gc();

    }

    /**
     *
     * @param file
     * @return
     */
    public List<Document> extract(File file) {
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
            Logger.getLogger(PlainCorpusBuilder.class.getName()).log(Level.SEVERE, null, ex);
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
                    //TODO see the wikipedia corpus builder for an example
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                Logger.getLogger(PlainCorpusBuilder.class.getName()).log(Level.SEVERE, null, ex1);
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
            Analyzer analyzer = new AnalyzerSpanish(Version.LUCENE_40);
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
