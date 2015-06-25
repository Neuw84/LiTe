package edu.ehu.galan.lite.parsers;
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

import edu.ehu.galan.lite.model.Token;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that represent a document reader for LiTe, all readers must extend this class
 * @author Angel Conde Majon
 */

public abstract class AbstractDocumentReader {

    /** 
     * The list of tokenized (tagged words) sentences
     */
    protected List<LinkedList<Token>> tokenizedSentenceList;
    /** 
     * The plain list of sentences 
     */
    protected List<String> sentenceList;

    public AbstractDocumentReader() {
        tokenizedSentenceList = new ArrayList<>();
        sentenceList = new ArrayList<>();
    }

    /**
     * Reads a defined source and fills the tokenized sentence list and the text
     * sentence lists using the desired parser plain text file, using CogComp
     * Splitter and adds POS tags. In the Tokenized list, each sentece will be a
     * linked list of tokens, each token will have the his POS tag For javadoc
     * of Token see Javadoc link of
     * http://cogcomp.cs.illinois.edu/page/software_view/LBJ Text sentence list
     * = plain text
     *
     * @param pSource - the source to process
     */
    public abstract void readSource(String pSource);

    /**
     * Returns the SentenceList of tokens
     *
     * @return the sentenceList
     */
    public List<LinkedList<Token>> getTokenizedSentenceList() {
        return tokenizedSentenceList;
    }

    /**
     * This list must contain a List of senteces, each sentece will be a linked
     * list of tokens, each token will have the his POS tag For javadoc of Token
     * see Javadoc link of http://cogcomp.cs.illinois.edu/page/software_view/LBJ
     *
     * @param sentenceList the sentenceList to set
     */
    public void setTokenizedSentenceList(List<LinkedList<Token>> sentenceList) {
        this.tokenizedSentenceList = sentenceList;
    }

    /**
     * List that contains a list of sentences (String) representing the text
     *
     * @return the sentenList
     */
    public List<String> getSentenceList() {
        return sentenceList;
    }

    /**
     * this list must contain Strings representing each line of the text to be
     * processed
     *
     * @param sentenList the sentenList to set
     */
    public void setSentenceList(List<String> sentenList) {
        this.sentenceList = sentenList;
    }

    /**
     * print the text to the standard output
     */
    public void printText() {
        sentenceList.stream().forEach(System.out::println);
    }

    /**
     * print the text and the POS tags and syntactic information if available
     * to the standard output
     */
    public void printPosTagsAndChunked() {
        for (LinkedList<Token> linkedList : tokenizedSentenceList) {
            for (Token token : linkedList) {
                System.out.println(token.getWordForm() + "\t" + token.getPosTag() + " \t" + token.getChunkerTag());
            }
        }
    }

    /**
     * Reset the parser (clear the lists containing tokens and sentences
     *
     */
    public void reset() {
        sentenceList=new ArrayList<>();
        tokenizedSentenceList=new ArrayList<>();
    }
}
