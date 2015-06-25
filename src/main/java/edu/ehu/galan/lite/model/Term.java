package edu.ehu.galan.lite.model;
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

import java.util.Objects;

/**
 * A term represents a candidate of the term extraction methods, it's need that
 * it will pass a validation with a knowledge base before knowing if it is a
 * topic A term contains an String with the term's text and a score if the
 * algorithm used for extracting the term has one. (if not the score must be -1)
 *
 * @author Angel Conde Manjon
 */
public class Term {

    private String term;
    private float score;

    /**
     *
     * @param pTerm
     */
    public Term(String pTerm) {
        term = pTerm;
        score = -1;

    }

    /**
     *
     * @param pTerm
     * @param pScore
     */
    public Term(String pTerm, float pScore) {
        term = pTerm;
        score = pScore;
    }

    /**
     * @return the extracted term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @param term the term to set
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(float score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return term + "\t" + score;
    }

    @Override
    public boolean equals(Object pObject) {
        if (pObject instanceof Term) {
            return this.term.equalsIgnoreCase(((Term) pObject).getTerm());
        } else {
            return false;

        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.term);
        return hash;
    }
}
