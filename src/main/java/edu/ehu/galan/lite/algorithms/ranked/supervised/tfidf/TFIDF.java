package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf;

/*
 * Copyright (C) 2014 Angel Conde Manjon neuw84 at gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class for processing the TDIDF algorithm using an Apache Lucene index to calculate the 
 * inverse document frequency
 */

class TFIDF {

    private IndexReader reader;
    private IndexSearcher searcher;
    private static List<TFIDFTerm> wordL;
    private static int numTotalWords = 0;
    private final Properties props;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
       
    
    public TFIDF(String pLang,String pProps) {
        
        props = new Properties();
        try {
            props.load(new FileInputStream(new File(pProps+"lite/configs/general.conf")));
        } catch (IOException ex) {
            logger.error("lite/configs/general.conf not found", ex);
        }
        initialize(pLang);
    }


    public static long getTotalTermFreq(IndexReader reader, final String field, final BytesRef termText) throws Exception {
        long totalTF = 0L;
        for (final AtomicReaderContext ctx : reader.getTopReaderContext().leaves()) {
            AtomicReader r = ctx.reader();
            Bits liveDocs = r.getLiveDocs();
            if (liveDocs == null) {
                // TODO: we could do this up front, during the scan
                // (next()), instead of after-the-fact here w/ seek,
                // if the codec supports it and there are no del
                // docs...
                final long totTF = r.totalTermFreq(field, termText);
                if (totTF != -1) {
                    totalTF += totTF;
                    continue;
                } // otherwise we fall-through
            }
            // note: what should we do if field omits freqs? currently it counts as 1...
            DocsEnum de = r.termDocsEnum(liveDocs, field, termText);
            if (de != null) {
                while (de.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                    totalTF += de.freq();
                }
            }
        }
        return totalTF;
    }

    private void initialize(String pLang) {
        try {
            reader = DirectoryReader.open(FSDirectory.open(new File(props.getProperty("luceneDir"+pLang.toUpperCase()))));
            searcher = new IndexSearcher(reader);
        } catch (IOException ex) {
            logger.error("Error loading Lucene Index file: check luceneDir property under lite conf.",ex);
            reader = null;
            searcher = null;
        }
    }

    protected void computeTFIDF(List<TFIDFTerm> wordList, int totalWordsDoc) {
        if(reader!=null && searcher != null ){
        double tf;
        double idf;
        double tfidf;
        EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_40);
        TokenStream stream = null;
        CharTermAttribute termAtt;
        String term;
        double totalWikiDocs = (double) reader.numDocs();
        for (TFIDFTerm word : wordList) {
            try {
                term = "";
                stream = analyzer.tokenStream("field", new StringReader(word.word));
                termAtt = stream.addAttribute(CharTermAttribute.class);
                stream.reset();
                // print all tokens until stream is exhausted
                while (stream.incrementToken()) {
                    term += (termAtt.toString());
                }
//                System.out.println(term);
                stream.end();
                tf = (double) word.count / (double) totalWordsDoc;
                double wikiTermFrec = reader.docFreq(new Term("contents", term));
                if (wikiTermFrec != 0) {
                    idf = Math.log(totalWikiDocs / wikiTermFrec);
                    tfidf = tf * idf;
                } else {
                    tfidf = 0;
                }
                word.tfidf = tfidf;
            } catch (IOException ex) {
                    logger.error("Error processing the TFIDF",ex);
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException ex) {
                    logger.error("Error processing the TFIDF",ex);
                }

            }

        }
        try {
            reader.close();
        } catch (IOException ex) {
            logger.warn("Error closing lucene reader",ex);
        }
        }
    }

    protected void setCorpusData(int pNumTotalWords, List<TFIDFTerm> pẀordList) {
        numTotalWords = pNumTotalWords;
        wordL = pẀordList;
    }

    protected void readCorpusDataFromText(String pFile) {
        Path path1 = Paths.get("words");
        List<String> wordList = null;
        try {
            wordList = Files.readAllLines(path1, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.error("Error getting Document data for TFIDF",ex);
        }
        wordL = new ArrayList<>();
        numTotalWords = Integer.parseInt(wordList.get(0));
        for (int i = 1; i < wordList.size(); i++) {
//            if(wordL.get(i)!=null){
            String[] num = wordList.get(i).split(" ");
            wordL.add(new TFIDFTerm(num[0], Integer.parseInt(num[1])));
//        }
        }
    }
}