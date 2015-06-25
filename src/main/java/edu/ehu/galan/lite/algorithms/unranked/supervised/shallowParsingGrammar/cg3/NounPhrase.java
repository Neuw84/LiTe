package edu.ehu.galan.lite.algorithms.unranked.supervised.shallowParsingGrammar.cg3;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that represents a Noun phrase inside a sentence
 *
 * @author Angel Conde Manjon
 */
class NounPhrase {

    private List<ChunkedWord> phrase;
    private int iniIdx;
    private int lastIdx;

    public NounPhrase() {
        phrase = new ArrayList<>();
    }

    public void addWord(ChunkedWord word) {
        phrase.add(word);
    }

    /**
     * @return the phrase
     */
    public List<ChunkedWord> getPhrase() {
        return phrase;
    }

    /**
     * @param phrase the phrase to set
     */
    public void setPhrase(List<ChunkedWord> phrase) {
        this.phrase = phrase;
    }

    /**
     * @return the iniIdx
     */
    public int getIniIdx() {
        return iniIdx;
    }

    /**
     * @param iniIdx the iniIdx to set
     */
    public void setIniIdx(int iniIdx) {
        this.iniIdx = iniIdx;
    }

    /**
     * @return the lastIdx
     */
    public int getLastIdx() {
        return lastIdx;
    }

    /**
     * @param lastIdx the lastIdx to set
     */
    public void setLastIdx(int lastIdx) {
        this.lastIdx = lastIdx;
    }
}
