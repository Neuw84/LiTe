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
 * A helper class that represents a sentence that may contain a Topic
 *
 * @author Angel Conde Manjon
 */
class CandidateSentence {

    private String sentence;
    private int indxLine;
    private String rule;
    public List<ChunkedWord> cSentence;
    private String topic;

    @SuppressWarnings("unchecked")
    public CandidateSentence(String pSentence, int pIndxLine, String pRule) {
        sentence = pSentence;
        indxLine = pIndxLine;
        rule = pRule;
        cSentence = new ArrayList<>();
    }

    /**
     * @return the sentence
     */
    public String getSentence() {
        return sentence;
    }

    /**
     * @param sentence the sentence to set
     */
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    /**
     * @return the indxLine
     */
    public int getIndxLine() {
        return indxLine;
    }

    /**
     * @param indxLine the indxLine to set
     */
    public void setIndxLine(int indxLine) {
        this.indxLine = indxLine;
    }

    /**
     * @return the trigger
     */
    public String getRule() {
        return rule;
    }

    /**
     * @param rule the trigger to set
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @param topic the topic to set
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

}
