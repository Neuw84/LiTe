
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

package edu.ehu.galan.lite.utils.ixatools;

import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.utils.ixatools.pos.pos.Morpheme;
import ixa.kaflib.Chunk;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import ixa.kaflib.WF;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utils for convert from KAF format to Lite Format and viceversa
 *
 * @author Angel Conde Manjon
 */
public class KafConverter {

    /**
     * Given a KAF document returns a Lite Document containing the sentence list and the tokenized
     * sentence list
     *
     * @param kaf
     * @return - a lite document
     */
    public static Document kaf2Lite(KAFDocument kaf) {
        Document doc = new Document("", "");
        List<String> sentenceList = doc.getSentenceList();
        List<LinkedList<Token>> tokenizedSentenceList = doc.getTokenList();
        for (int i = 0; i < kaf.getSentences().size(); i++) {
            List<WF> sentence = kaf.getSentences().get(i);
            StringBuilder sb = new StringBuilder();
            for (WF wfSent : sentence) {
                sb.append(wfSent.getForm()).append(" ");
            }
            sentenceList.add(sb.toString().trim());
            LinkedList<Token> tokenList = new LinkedList<>();
            List<Term> terms = kaf.getSentenceTerms(i + 1);
            List<Chunk> chunksBySent = kaf.getChunksBySent(i + 1);
            boolean empty = false;
            int aux = 0;
            int numChunk = 0;
            int prev = 0;
            for (int j = 0; j < terms.size(); j++) {
                Term term = terms.get(j);
                if (j == aux) {
                    if (!chunksBySent.isEmpty()) {
                        if (numChunk < chunksBySent.size()) {
                            Chunk chunk = chunksBySent.get(numChunk);
                            if (term.getId() == null ? chunk.getTerms().get(0).getId() == null : term.getId().equals(chunk.getTerms().get(0).getId())) {
                                aux += chunk.getTerms().size();
                                empty = false;
                                Token tok = new Token(term.getForm(), term.getMorphofeat(), term.getLemma(), "B-" + chunk.getPhrase());
                                tokenList.add(tok);
                                prev = numChunk;
                                numChunk++;
                            } else {
                                Token tok = new Token(term.getForm(), term.getMorphofeat(), term.getLemma(), "O");
                                tokenList.add(tok);
                            }
                        } else {
                            Token tok = new Token(term.getForm(), term.getMorphofeat(), term.getLemma(), "O");
                            tokenList.add(tok);
                        }
                    } else {
                        empty = true;
                    }
                } else {
                    if (empty) {
                        Token tok = new Token(term.getForm(), term.getMorphofeat(), term.getLemma(), "O");
                        tokenList.add(tok);
                    } else {
                        Chunk chunk = chunksBySent.get(prev);
                        Token tok = new Token(term.getForm(), term.getMorphofeat(), term.getLemma(), "I-" + chunk.getPhrase());
                        tokenList.add(tok);
                    }
                }
            }
            tokenizedSentenceList.add(tokenList);
        }

        return doc;
    }

    /**
     * Given a Lite Document and it's language returns a KAF representation of it
     *
     * @param liteDoc
     * @param lang
     * @return
     */
    public static KAFDocument lite2KAF(Document liteDoc, String lang) {
        KAFDocument kaf = new KAFDocument(lang, "angel");
        KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("text", "ixa-pipe-tok-" + lang, "angel");
        KAFDocument.LinguisticProcessor newLp2 = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + lang, "angel");
        List<String> sentenceList = liteDoc.getSentenceList();
        List<LinkedList<Token>> tokenizedSentenceList = liteDoc.getTokenList();
        int sentences = 0;
        int offSet = 0;
        for (LinkedList<Token> tokenizedSentenceList1 : tokenizedSentenceList) {
            List<Morpheme> morphemes = new ArrayList<>();
            for (Token token : tokenizedSentenceList1) {
                WF wf = kaf.newWF(token.getWordForm(), offSet, sentences);
                Morpheme morpheme = new Morpheme(token.getWordForm(), token.getPosTag(), token.getLemma());
                morphemes.add(morpheme);
                offSet++;
                List<WF> wfs = new ArrayList<>();
                wfs.add(wf);
                String posId = morpheme.getTag();
                String type = setTermType(posId);
                kaf.createTermOptions(type, morpheme.getLemma(), posId, morpheme.getTag(), wfs);
            }
            sentences++;

        }
        System.out.println(kaf);
        return kaf;
    }

    /**
     * Set the term type attribute based on the pos value.
     *
     * @param postag the postag
     * @return the type
     */
    private static String setTermType(final String postag) {
        if (postag.startsWith("N") || postag.startsWith("V")
                || postag.startsWith("G") || postag.startsWith("A")) {
            return "open";
        } else {
            return "close";
        }
    }
}
