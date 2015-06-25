package edu.ehu.galan.lite.algorithms.ranked.supervised.tfidf.corpus.analyzers;
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

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.KeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

/**
 * {@link Analyzer} for English.
 */
public final class AnalyzerEnglish extends StopwordAnalyzerBase {

    private final CharArraySet stemExclusionSet;

    /**
     * Returns an unmodifiable instance of the default stop words set.
     *
     * @return default stop words set.
     */
    public static CharArraySet getDefaultStopSet() {
        return DefaultSetHolder.DEFAULT_STOP_SET;
    }

    /**
     * Atomically loads the DEFAULT_STOP_SET in a lazy fashion once the outer
     * class accesses the static final set the first time.;
     */
    private static class DefaultSetHolder {

        static final CharArraySet DEFAULT_STOP_SET = StandardAnalyzer.STOP_WORDS_SET;
    }

    /**
     * Builds an analyzer with the default stop words:
     * {@link #getDefaultStopSet}.
     */
    public AnalyzerEnglish(Version matchVersion) {
        this(matchVersion, DefaultSetHolder.DEFAULT_STOP_SET);
    }

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param matchVersion lucene compatibility version
     * @param stopwords a stopword set
     */
    public AnalyzerEnglish(Version matchVersion, CharArraySet stopwords) {
        this(matchVersion, stopwords, CharArraySet.EMPTY_SET);
    }

    /**
     * Builds an analyzer with the given stop words. If a non-empty stem
     * exclusion set is provided this analyzer will add a
     * {@link KeywordMarkerFilter} before stemming.
     *
     * @param matchVersion lucene compatibility version
     * @param stopwords a stopword set
     * @param stemExclusionSet a set of terms not to be stemmed
     */
    public AnalyzerEnglish(Version matchVersion, CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(matchVersion, stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(
                matchVersion, stemExclusionSet));
    }

    /**
     * Creates a
     * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents} which
     * tokenizes all the text in the provided {@link Reader}.
     *
     * @return A
     * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents} built
     * from an {@link StandardTokenizer} filtered with      {@link StandardFilter}, {@link EnglishPossessiveFilter}, 
   *         {@link LowerCaseFilter}, {@link StopFilter}
     *         , {@link KeywordMarkerFilter} if a stem exclusion set is provided and
     * {@link PorterStemFilter}.
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName,
            Reader reader) {
        final Tokenizer source = new StandardTokenizer(matchVersion, reader);
        TokenStream result = new StandardFilter(matchVersion, source);
        // prior to this we get the classic behavior, standardfilter does it for us.
        result = new EnglishPossessiveFilter(matchVersion, result);
        result = new LowerCaseFilter(matchVersion, result);
        result = new StopFilter(matchVersion, result, stopwords);
        return new TokenStreamComponents(source, result);
    }

//    public static void main(String[] args) throws IOException {
//        // text to tokenize
//        final String text = "This is a demo using of the TokenStream API classes, classes angel's house doesn't houses abstraction";
//
//        Version matchVersion = Version.LUCENE_40; // Substitute desired Lucene version for XY
//        AnalyzerTest analyzer = new AnalyzerTest(matchVersion);
//        TokenStream stream = analyzer.tokenStream("field", new StringReader(text));
//
//        // get the CharTermAttribute from the TokenStream
//        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
//
//        try {
//            stream.reset();
//
//            // print all tokens until stream is exhausted
//            while (stream.incrementToken()) {
//                System.out.println(termAtt.toString());
//            }
//
//            stream.end();
//        } finally {
//            stream.close();
//        }
//    }
}