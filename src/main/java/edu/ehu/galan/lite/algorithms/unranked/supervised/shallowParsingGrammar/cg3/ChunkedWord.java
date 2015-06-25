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
package edu.ehu.galan.lite.algorithms.unranked.supervised.shallowParsingGrammar.cg3;

/**
 * Helper class for shallowParsing grammar algorithm representing a single word
 *
 * @author Angel Conde Manjon
 */
//TODO: Should use the Corpus Token Class!!!
class ChunkedWord {

    public ChunkedWord(String pWord, String pChunker, String pPos) {
        word = pWord;
        chunker = pChunker;
        pos = pPos;
    }
    private String word;
    private String chunker;
    private String pos;

    /**
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the word to set
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * @return the chunker
     */
    public String getChunker() {
        return chunker;
    }

    /**
     * @param chunker the chunker to set
     */
    public void setChunker(String chunker) {
        this.chunker = chunker;
    }

    /**
     * @return the pos
     */
    public String getPos() {
        return pos;
    }

    /**
     * @param pos the pos to set
     */
    public void setPos(String pos) {
        this.pos = pos;
    }

}
