package edu.ehu.galan.lite.stemmers.spanish;

import edu.ehu.galan.lite.stemmers.IStemmer;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.SpanishStemmer;

/*
   Copyright (C) 2013 Angel Conde Manjon, neuw84 at gmail dot com
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
 * Wrapper for Basque stemmer from Snowball using  SnowBall Analyzer
 *
 * @author Angel Conde Manjon
 */

public class SpanishSnowballStemmer implements IStemmer{

    private final SnowballProgram stem;

    public SpanishSnowballStemmer() {
        stem = new SpanishStemmer();
    }

    @Override
    public String stem(String pString) {
        stem.setCurrent(pString);
        stem.stem();
        return stem.getCurrent();
    }
//
//    public static void main(String[] args) {
//        System.out.println(new SpanishSnowballStemmer().stem("casas"));
//    }
}