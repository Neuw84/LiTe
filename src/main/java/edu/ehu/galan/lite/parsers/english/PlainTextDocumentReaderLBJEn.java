package edu.ehu.galan.lite.parsers.english;
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

import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.parse.Parser;
import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.parsers.AbstractDocumentReader;
import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;
import edu.illinois.cs.cogcomp.lbj.pos.POSTagger;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Class that reads the text to be processed using LBJ and CogComp Part of
 * Speech tagger, the libraries MUST in the classpath
 *
 * * @author Angel Conde Manjon
 */
public class PlainTextDocumentReaderLBJEn extends AbstractDocumentReader {

    private Parser parser = null;

    /**
     *
     */
    public Properties pen = null;

    /**
     *
     */
    public PlainTextDocumentReaderLBJEn() {
        super();

    }

    /**
     * Reads a text plain text file, using CogComp Splitter, adds POS tags using
     * CogComp POS Tagger, and add chunk information using Illinois Chunker See
     * http://cogcomp.cs.illinois.edu for more info
     *
     * @param pFile - The Location of the file
     */
    @Override
    public void readSource(String pFile) {
        POSTagger tagger = new POSTagger();
        Chunker chunker = new Chunker();
        boolean first = true;
        parser = new PlainToTokenParser(new WordSplitter(new SentenceSplitter(pFile)));
        String sentence = "";
        LinkedList<Token> tokenList = null;
        for (LBJ2.nlp.seg.Token word = (LBJ2.nlp.seg.Token) parser.next(); word != null;
                word = (LBJ2.nlp.seg.Token) parser.next()) {
            String chunked = chunker.discreteValue(word);
            tagger.discreteValue(word);
            if (first) {
                tokenList = new LinkedList<>();
                tokenizedSentenceList.add(tokenList);
                first = false;
            }
            tokenList.add(new Token(word.form, word.partOfSpeech, null, chunked));
            sentence = sentence + " " + (word.form);
            if (word.next == null) {
                sentenceList.add(sentence);
                first = true;
                sentence = "";
            }
        }
        parser.reset();
    }

    /**
     * // * Reads the file called "text" from the jre current running directory
     * using // * parser from CogComp group // * // * @param args //
     */
//    public static void main(String[] args) {
//        PlainTextDocumentReaderLBJEn reader = new PlainTextDocumentReaderLBJEn();
//        reader.readSource("testCorpus/textAstronomy");
//        reader.printText();
//        reader.printPosTagsAndChunked();
//    }
}
