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
 * Class that convert all the characters from a string to lower/upper case whether needed this stemmer is language
 * "neutral"
 *
 * @author Angel Conde Manjon
 */
public class CaseStemmer implements IStemmer {

    private final CaseType typeOfCase;

    public enum CaseType {

        lowercase, uppercase
    }

    public CaseStemmer(CaseType pCase) {
        typeOfCase = pCase;
    }

    @Override
    public String stem(String pString) {
        return typeOfCase == CaseType.lowercase ? pString.toLowerCase() : pString.toUpperCase();
    }

////    public static void main(String[] args) {
////        System.out.println(new CaseStemmer(CaseType.lowercase).stem("HOUSE MOUSE"));
////    }
}
