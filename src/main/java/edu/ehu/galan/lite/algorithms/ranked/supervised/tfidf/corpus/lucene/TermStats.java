package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.lucene;

/*
 *    TermStats.java
 *    Copyright (C) 2013 Angel Conde, neuw84 at gmail dot com
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


import org.apache.lucene.util.BytesRef;

/**
 * Holder for a term along with its statistics
 * ({@link #docFreq} and {@link #totalTermFreq}).
 */

public final class TermStats {

    /**
     *
     */
    public BytesRef termtext;

    /**
     *
     */
    public String field;

    /**
     *
     */
    public int docFreq;

    /**
     *
     */
    public long totalTermFreq;
  
  TermStats(String field, BytesRef termtext, int df) {
    this.termtext = BytesRef.deepCopyOf(termtext);
    this.field = field;
    this.docFreq = df;
  }
  
  TermStats(String field, BytesRef termtext, int df, long tf) {
    this.termtext = BytesRef.deepCopyOf(termtext);
    this.field = field;
    this.docFreq = df;
    this.totalTermFreq = tf;
  }
  
  String getTermText() {
    return termtext.utf8ToString();
  }

  @Override
  public String toString() {
    return("TermStats: term=" + termtext.utf8ToString() + " docFreq=" + docFreq + " totalTermFreq=" + totalTermFreq);
  }
}