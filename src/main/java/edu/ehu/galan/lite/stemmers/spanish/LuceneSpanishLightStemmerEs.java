package edu.ehu.galan.lite.stemmers.spanish;
/*
 *    Copyright (C) 2013 Angel Conde Manjon, neuw84 at gmail dot com
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */


import edu.ehu.galan.lite.stemmers.IStemmer;
import org.apache.lucene.analysis.es.SpanishLightStemmer;


/**
 * Wrapper for Spanish light stemmer from Apache Lucene
 * @author Angel Conde Manjon 
 */
public class LuceneSpanishLightStemmerEs implements IStemmer {
    private final SpanishLightStemmer stem;

    /**
     *
     */
    public LuceneSpanishLightStemmerEs(){
        stem = new SpanishLightStemmer();
    }

    @Override
    public String stem(String pString) {
      char[] word= pString.toCharArray();
      int lenght=stem.stem(word, pString.length());
      return new String(word).substring(0,lenght);
    }
//    public static void main(String[] args) {
//        System.out.println(new LuceneSpanishLightStemmerEs().stem("casas"));
//    }
    
    
}
