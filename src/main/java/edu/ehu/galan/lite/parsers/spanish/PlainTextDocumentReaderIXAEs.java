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

package edu.ehu.galan.lite.parsers.spanish;

import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.parsers.AbstractDocumentReader;
import es.ehu.si.ixa.ixa.pipe.tok.Annotate;
import ixa.kaflib.Chunk;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.Term;
import ixa.kaflib.WF;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 * Class that parse a Spanish plain text  using the IXA NLP toolkit (Tokenizer and POS Tagger)
 * http://ixa2.si.ehu.es/ixa-pipes/  
 * 
 * Tokenize + POS TAGS
 *
 * @author Angel Conde
 */

public class PlainTextDocumentReaderIXAEs extends AbstractDocumentReader {

    private transient final org.slf4j.Logger logger = LoggerFactory.getLogger(PlainTextDocumentReaderIXAEs.class);

    
    @Override
    public void readSource(String pSource) {
        KAFDocument kaf;
        try {
            BufferedReader breader = null;
            BufferedWriter bwriter = null;
            Properties properties = setAnnotateProperties("en", "IxaPipeTokenizer", "default", "false");
            breader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(pSource)), "UTF-8"));
            kaf = new KAFDocument("es", "angel");
            KAFDocument.LinguisticProcessor newLp = kaf.addLinguisticProcessor("text", "ixa-pipe-tok-" + "es", "angel");
            newLp.setBeginTimestamp();
            Annotate TOKannotator = new Annotate(new BufferedReader(breader), properties);
            TOKannotator.tokenizedToKAF(kaf);
            newLp.setEndTimestamp();
            Properties posProperties = setAnnotatePropertiesPos("resources/lite/pos-models/es/es-pos-perceptron-c0-b3.bin", "es", "3", "false", "true","resources/lite/");
            eus.ixa.ixa.pipe.pos.Annotate POSannotator = new eus.ixa.ixa.pipe.pos.Annotate(posProperties);
            KAFDocument.LinguisticProcessor newLp2 = kaf.addLinguisticProcessor("terms", "ixa-pipe-pos-" + "es", "angel");
            newLp2.setBeginTimestamp();
            POSannotator.annotatePOSToKAF(kaf);
            newLp2.setEndTimestamp();
//            System.out.println((kaf.toString()));
                      for (int i = 0; i < kaf.getSentences().size(); i++) {
                List<WF> sentence = kaf.getSentences().get(i);
                StringBuilder sb = new StringBuilder();
                for (WF wfSent : sentence) {
                    sb.append(wfSent.getForm()).append(" ");
                }
                sentenceList.add(sb.toString().trim());
//                System.out.println(sb.toString());
//                if (sb.toString().equals("It also models the motion of Mercury and Venus . ")) {
//                    System.out.println("sad");
//                }
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

        } catch (FileNotFoundException ex) {
            logger.error("File not found ", ex);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Encoding error ", ex);
        } catch (IOException ex) {
            logger.error("IO Exceptio n", ex);
        }

    }

    private Properties setAnnotateProperties(String lang, String tokenizer, String normalize, String paragraphs) {
        Properties annotateProperties = new Properties();
        annotateProperties.setProperty("language", lang);
        annotateProperties.setProperty("tokenizer", tokenizer);
        annotateProperties.setProperty("normalize", normalize);
        annotateProperties.setProperty("paragraphs", paragraphs);
        return annotateProperties;
    }

    private Properties setAnnotatePropertiesPos(final String model,
            final String language, final String beamSize, final String multiwords,
            final String dictag, final String directory) {
        final Properties annotateProperties = new Properties();
        annotateProperties.setProperty("model", model);
        annotateProperties.setProperty("language", language);
        annotateProperties.setProperty("beamSize", beamSize);
        annotateProperties.setProperty("multiwords", multiwords);
        annotateProperties.setProperty("dictag", dictag);
        annotateProperties.setProperty("directory", directory);

        return annotateProperties;
    }

}
