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
import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.stemmers.IStemmer;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The tfidf Algorithms needs a corpus to be created, see the package corpus for examples,
 *
 * @author Angel Conde Manjon
 */
public class TFIDFAlgorithm extends AbstractAlgorithm {
    
    private List<Term> termList;
    private Document doc;
    private IStemmer stemmer;
    private int numTotalWords = 0;
    private Properties props = null;
    private List<Word> wordList;
    private List<TFIDFTerm> tfTermList;
    private String lang;
    private String propsDir;

    /**
     * The TFIDF algorithm takes a corpus where the terms will be extracted and a stemmer that will
     * be used to process that corpus, if the stemmer is "null" no stemmer will be used to process
     * the corpus
     *
     * @param pStemmer
     * @param pLang
     */
    public TFIDFAlgorithm(IStemmer pStemmer, String pLang) {
        super(true, "TFIDF");
        termList = super.getTermList();
        stemmer = pStemmer;
        wordList = new ArrayList<>();
        tfTermList = new ArrayList<>();
        lang = pLang;
    }
    
    @Override
    public void init(Document pDoc, String pPropsDir) {
        setDoc(pDoc);
        propsDir = pPropsDir;        
        props = new Properties();
        try {
            props.load(new FileInputStream(pPropsDir + "lite/configs/general.conf"));
        } catch (IOException ex) {
            Logger.getLogger(TFIDFAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.setProperties(props);
        
    }
    
    @Override
    public void runAlgorithm() {
        tfTermList.clear();
        extractCorpusData();
        TFIDF method = new TFIDF(lang, propsDir);
        for (Word w : wordList) {
            tfTermList.add(new TFIDFTerm(w.word, w.count));
        }
        method.setCorpusData(numTotalWords, tfTermList);
        method.computeTFIDF(tfTermList, numTotalWords);        
        List<Term> terms = tfTermList.parallelStream().filter(t -> t.tfidf != -1).map(t -> new Term(t.word.trim(), (float) t.tfidf)).sorted((t1, t2) -> t1.getScore() > t2.getScore() ? -1 : t1.getScore() == t2.getScore() ? 0 : 1).collect(Collectors.toList());
        tfTermList.clear(); //memory leak if we do not clean here
        getDoc().addListTerm(new ListTerm(this.getName(), terms));
        termList.clear();
    }

    /**
     * @return the stemmer
     */
    public IStemmer getStemmer() {
        return stemmer;
    }

    /**
     * @param stemmer the stemmer to set
     */
    public void setStemmer(IStemmer stemmer) {
        this.stemmer = stemmer;
    }
    
    public void printCorpusData() {
        Collections.sort(wordList);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(props.getProperty("tmpDir") + "/tfCorpusData"), Charset.defaultCharset())) {
            writer.append(Integer.toString(numTotalWords));
            writer.newLine();
            for (Word wor : wordList) {
                if (wor.count > 2) {
                    writer.append(wor.word + "\t" + wor.count);
                    writer.newLine();
                }
            }
            writer.flush();
        } catch (IOException exception) {
            System.out.println("Error writing to file");
        }
    }
    
    private void extractCorpusData() {
        List<LinkedList<Token>> tokensLists = getDoc().getTokenList();
        for (LinkedList<Token> linkedList : tokensLists) {
            for (Token token : linkedList) {
                String words = token.getWordForm();
                if (stemmer == null) {
                } else {
                    words = stemmer.stem(words);
                }
                numTotalWords++;
                boolean aux = false;
                for (Word w : wordList) {
                    if (w.word.equalsIgnoreCase(words)) {
                        w.count++;
                        aux = true;
                        break;
                    }
                }
                if (!aux) {
                    wordList.add(new Word(words, 1));
                }
                
            }
        }        
        printCorpusData();

    }

    /**
     * @return the doc
     */
    public Document getDoc() {
        return doc;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(Document doc) {
        this.doc = doc;
    }
    
    private class Word implements Comparable<Word> {
        
        public int count;
        public String word;
        public int indx;
        
        public Word() {
        }
        
        private Word(String form, int i) {
            word = form;
            count = i;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof String) {
                String x = (String) obj;
                if (x.equalsIgnoreCase(word)) {
                    return true;
                }
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Word other = (Word) obj;
            return this.word.equalsIgnoreCase(other.word);
        }
        
        @Override
        public int hashCode() {
            return word.hashCode();
        }
        
        @Override
        public int compareTo(Word o) {
            return (this.count > o.count ? -1 : (this.count == o.count ? 0 : 1));
        }
    }
}
