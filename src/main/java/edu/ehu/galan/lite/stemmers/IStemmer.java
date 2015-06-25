package edu.ehu.galan.lite.stemmers;
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
 * An functional interface that every stemmer must implement, as Java 8 is being
 * used, this interface is also functional
 *
 * @author Angel Conde Manjon
 */

@FunctionalInterface
public interface IStemmer {

    /**
     * Given a string returns its stemmed representation
     *
     * @param pString
     * @return
     */
    public String stem(String pString);

}
