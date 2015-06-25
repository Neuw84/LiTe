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

/**
 * Class that implement the tfidf data of a term, this class is not implemented using getters
 * setters for efficiency reasons
 *
 * @author Angel Conde Manjon
 */
class TFIDFTerm {

    public int count;
    public String word;
    public int indx;
    public double tfidf;

    /**
     *
     * @param form
     * @param i
     */
    public TFIDFTerm(String form, int i) {
        word = form;
        count = i;
        tfidf = -1;
    }

    /**
     *
     * @param obj
     * @return
     */
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
        TFIDFTerm other = (TFIDFTerm) obj;
        if (this.word.equalsIgnoreCase(other.word)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return word.hashCode();
    }

}
