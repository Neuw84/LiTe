package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.lucene;

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
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FieldsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Class extracts the top n most frequent terms (by document frequency ) from an existing Lucene
 * index and reports their document frequency.
 *
 * @author Angel Conde Manjon
 *
 */
public class CorpusHighFreqTerms {

    // The top numTerms will be displayed

    /**
     *
     */
        public static final int DEFAULTnumTerms = 1000;

    /**
     *
     */
    public static int numTerms = DEFAULTnumTerms;
    private static final Logger logger=LoggerFactory.getLogger(CorpusHighFreqTerms.class);

    /**
     *
     * Extracts the top n most frequent terms (by document frequency ) from an existing Lucene index
     * (the dir must be specified via args or via tfidfTester on
     * /resources/lite/configs/general.conf) in this case the Wikipedia corpus) and reports their
     * document frequency.
     *
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        try{
        Properties prop = new Properties();
        InputStream is = new FileInputStream("resources/lite/configs/general.conf");
            FSDirectory dir;
            if(args.length==1){
                if(Paths.get(args[0]).toFile().isDirectory()){
                    dir = FSDirectory.open(new File(args[0]));
                }else{
                    System.out.println("The specified directory does not exist\n"
                            + " backing to load the lucene index specified in the config files");
                    dir = FSDirectory.open(new File(prop.getProperty("tfidfTester")));
                }
            }else if(args.length>1){
                System.out.println("The args only need one parameter, the directory of the Lucene Index\n "
                        + "backing to load the lucene index specified in the config files");
                dir = FSDirectory.open(new File(prop.getProperty("tfidfTester")));
            }else{
                dir = FSDirectory.open(new File(prop.getProperty("tfidfTester")));
            }
            IndexReader reader = null;           
            String field = null;
            boolean IncludeTermFreqs = false;
            prop.load(is);           
            IncludeTermFreqs = true;
            reader = DirectoryReader.open(dir);
            System.out.println("num Docs " + reader.numDocs());
            TermStats[] terms = getHighFreqTerms(reader, numTerms, field);
            if (!IncludeTermFreqs) {
                //default HighFreqTerms behavior
                for (int i = 0; i < terms.length; i++) {
                    System.out.printf("%s:%s %,d \n",
                            terms[i].field, terms[i].termtext.utf8ToString(), terms[i].docFreq);
                }
            } else {
                TermStats[] termsWithTF = sortByTotalTermFreq(reader, terms);
                for (int i = 0; i < termsWithTF.length; i++) {
                    System.out.printf("%s:%s \t totalTF = %,d \t doc freq = %,d \n",
                            termsWithTF[i].field, termsWithTF[i].termtext.utf8ToString(),
                            termsWithTF[i].totalTermFreq, termsWithTF[i].docFreq);
                }
            }
            reader.close();
        }catch(Exception ex){
            logger.error("The directory specified contains a Lucene index?",ex);
        }
    }

    /**
     *
     * @param reader
     * @param numTerms
     * @param field
     * @return TermStats[] ordered by terms with highest docFreq first.
     * @throws Exception
     */
    public static TermStats[] getHighFreqTerms(IndexReader reader, int numTerms, String field) throws Exception {
        TermStatsQueue tiq = null;

        if (field != null) {
            Fields fields = MultiFields.getFields(reader);
            if (fields == null) {
                throw new RuntimeException("field " + field + " not found");
            }
            Terms terms = fields.terms(field);
            if (terms != null) {
                TermsEnum termsEnum = terms.iterator(null);
                tiq = new TermStatsQueue(numTerms);
                tiq.fill(field, termsEnum);
            }
        } else {
            Fields fields = MultiFields.getFields(reader);
            if (fields == null) {
                throw new RuntimeException("no fields found for this index");
            }
            tiq = new TermStatsQueue(numTerms);
            FieldsEnum fieldsEnum = fields.iterator();
            while (true) {
                field = fieldsEnum.next();
                if (field != null) {
                    Terms terms = fieldsEnum.terms();
                    if (terms != null) {
                        tiq.fill(field, terms.iterator(null));
                    }
                } else {
                    break;
                }
            }
        }

        TermStats[] result = new TermStats[tiq.size()];
        // we want highest first so we read the queue and populate the array
        // starting at the end and work backwards
        int count = tiq.size() - 1;
        while (tiq.size() != 0) {
            result[count] = tiq.pop();
            count--;
        }
        return result;
    }

    /**
     * Takes array of TermStats. For each term looks up the tf for each doc containing the term and
     * stores the total in the output array of TermStats. Output array is sorted by highest total
     * tf.
     *
     * @param reader
     * @param terms TermStats[]
     * @return TermStats[]
     * @throws Exception
     */
    public static TermStats[] sortByTotalTermFreq(IndexReader reader, TermStats[] terms) throws Exception {
        TermStats[] ts = new TermStats[terms.length]; // array for sorting
        long totalTF;
        for (int i = 0; i < terms.length; i++) {
            totalTF = getTotalTermFreq(reader, terms[i].field, terms[i].termtext);
            ts[i] = new TermStats(terms[i].field, terms[i].termtext, terms[i].docFreq, totalTF);
        }

        Comparator<TermStats> c = new TotalTermFreqComparatorSortDescending();
        Arrays.sort(ts, c);

        return ts;
    }

    /**
     *
     * @param reader
     * @param field
     * @param termText
     * @return
     * @throws Exception
     */
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
}

/**
 * Comparator
 *
 * Reverse of normal Comparator. i.e. returns 1 if a.totalTermFreq is less than b.totalTermFreq So
 * we can sort in descending order of totalTermFreq
 */
final class TotalTermFreqComparatorSortDescending implements Comparator<TermStats> {

    @Override
    public int compare(TermStats a, TermStats b) {
        if (a.totalTermFreq < b.totalTermFreq) {
            return 1;
        } else if (a.totalTermFreq > b.totalTermFreq) {
            return -1;
        } else {
            return 0;
        }
    }
}

/**
 * Priority queue for TermStats objects ordered by docFreq
 *
 */
final class TermStatsQueue extends PriorityQueue<TermStats> {

    TermStatsQueue(int size) {
        super(size);
    }

    @Override
    protected boolean lessThan(TermStats termInfoA, TermStats termInfoB) {
        return termInfoA.docFreq < termInfoB.docFreq;
    }

    protected void fill(String field, TermsEnum termsEnum) throws IOException {
        while (true) {
            BytesRef term = termsEnum.next();
            if (term != null) {
                insertWithOverflow(new TermStats(field, term, termsEnum.docFreq()));
            } else {
                break;
            }
        }
    }
}
