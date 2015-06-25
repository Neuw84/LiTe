package edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.filters.english;

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

import edu.ehu.galan.lite.algorithms.ranked.unsupervised.cvalue.filters.ILinguisticFilter;
import edu.ehu.galan.lite.model.Token;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Filter that searchs for Noun+Noun
 *
 * @author Angel Conde Manjon
 */
public class NounFilter implements ILinguisticFilter {

    private List<String> list;

    @Override
    public List<String> getCandidates(LinkedList<Token> pSentence) {
        list = new ArrayList<>();
        if (pSentence != null) {
            for (Token token : pSentence) {
                if (token.getPosTag().matches("NN|NNS|NNP|NNPS")) {
                    String candidate = null;
                    int pos = pSentence.indexOf(token);
                    String result = findCandidate(pSentence, pos, candidate);
                    if (result != null) {
                        list.add(result);
                    }
                }
            }
        }
        return list;
    }

    private String findCandidate(LinkedList<Token> pSentence, int pPos, String candidate) {
        int sentenceSize = pSentence.size();
        Token word = null;
        if (sentenceSize - 1 > pPos) {
            word = pSentence.get(pPos + 1);
        }
        if (word != null) {
            if (word.getPosTag().matches("NN|NNS|NNP|NNPS")) {
                candidate = pSentence.get(pPos).getWordForm() + " " + word.getWordForm();
                list.add(candidate);
                pPos++;
                if (sentenceSize - 1 == pPos) {
                    candidate = null;
                } else {
                    if (!(pSentence.get(pPos).getPosTag().matches("NN|NNS|NNP|NNPS"))) {
                        return null;
                    }
                }
                return findCandidate2(pSentence, pPos, candidate);
            } else {
                return candidate;
            }
        } else {
            return candidate;
        }

    }

    private String findCandidate2(LinkedList<Token> pSentence, int pPos, String candidate) {

        int sentenceSize = pSentence.size();
        Token word = null;
        if (sentenceSize - 1 > pPos) {
            word = pSentence.get(pPos + 1);
        }
        if (word != null) {
            if (word.getPosTag().matches("NN|NNS|NNP|NNPS")) {
                candidate = candidate + " " + word.getWordForm();
                list.add(candidate);
                pPos++;

                if (sentenceSize - 1 == pPos) {
                    candidate = null;
                } else {
                    if (!(pSentence.get(pPos).getPosTag().matches("NN|NNS|NNP|NNPS"))) {
                        return null;
                    }
                }
                return findCandidate2(pSentence, pPos, candidate);
            } else {
                return candidate;
            }
        } else {
            return candidate;
        }

    }

//    public static void main(String[] args) {
//        LinkedList<Token> tokenList = new LinkedList<>();
//        tokenList.add(new Token("hello", "NN"));
//        tokenList.add(new Token("hello", "NN"));
//        new NounFilter().getCandidates(tokenList);
//
//    }
}
