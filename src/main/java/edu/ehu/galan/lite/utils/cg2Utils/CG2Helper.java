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

package edu.ehu.galan.lite.utils.cg2Utils;

import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.utils.freeLingUtils.FreeLing2LidomFormatter;
import edu.ehu.galan.lite.utils.systemUtils.SystemCommandExecutor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 * Helper class for processing CG2 constraint grammars
 *
 * @author Angel Conde Manjon
 */
public class CG2Helper {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CG2Helper.class);

    /**
     * Given a document returns a StringBuilder containing a representation of the document that can
     * be used to process the document with a Constraint grammar using CG2, in this case the aim 
     * is to apply a grammar to all the sentences in order to discover topics, after this, see processDocument
     * method
     *
     * @param pTokenList - the tokenized representation of the document
     *
     * @return
     */
    public static StringBuilder formatDocument(List<LinkedList<Token>> pTokenList) {

        StringBuilder docr = new StringBuilder();

        //TODO, cheap identifier to process all the grammar in one CG3 service call
        docr.append("@@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@").append(System.getProperty("line.separator"));
        for (LinkedList<Token> token : pTokenList) {
            for (ListIterator<Token> itr = token.listIterator(); itr.hasNext();) {
                Token tok = itr.next();
                docr.append(tok.getWordForm()).append(" ").append(tok.getLemma()).append(" ").append(tok.getPosTag()).append(System.getProperty("line.separator"));
            }
            docr.append("@@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@").append(System.getProperty("line.separator"));
        }
        return docr;
    }

    
    
    /**
     * Given a tokenized sentence returns a StringBuilder containing a representation of the
     * document that can be used to process the document with a Constraint grammar using CG2 (see
     * processDocument) method
     *
     * @param pTokenizedSent
     * @return
     */
    public static StringBuilder formatTokenizedSentence(LinkedList<Token> pTokenizedSent) {

        StringBuilder sb = new StringBuilder();

        for (ListIterator<Token> itr = pTokenizedSent.listIterator(); itr.hasNext();) {
            Token tok = itr.next();
            sb.append(tok.getWordForm()).append(" ").append(tok.getLemma()).append(" ").append(tok.getPosTag()).append(System.getProperty("line.separator"));
        }
        return sb;
    }

    /**
     * Process a Document and return a list of candidates formed by the
     * sentence##rule_fired##numberofSentence
     *
     * @param sb - the CG2 compatible internal representation of the document (use formatDocument
     * method)
     * @param pProps - The general properties of Lite containing dir information
     * @param pGrammar - The grammar to apply
     * @param pPenProperties - The properties that contain a map of the PenTree Bank tag set
     * @return
     */
    public static List<String> processDocument(StringBuilder sb, Properties pProps, String pGrammar, Properties pPenProperties) {
        String cgStream = parserToCG(sb.toString(), pPenProperties);
        try {
            //TODO: not compatible with paralelization!
            Files.deleteIfExists(Paths.get(pProps.getProperty("tmpDir") + "cg3/text.txt"));
            Files.createDirectories(Paths.get(pProps.getProperty("tmpDir") + "cg3/"));
            Files.write(Paths.get(pProps.getProperty("tmpDir") + "cg3/text.txt"), cgStream.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        } catch (IOException ex) {
            logger.error("Error while deleting and creating the CG compatible version of the document", ex);
        }
        List<String> command2 = new ArrayList<>();
        command2.add("/bin/sh");
        command2.add("-c");
        command2.add("cg3 --trace -g " + pGrammar + " -I " + pProps.getProperty("tmpDir") + "/cg3/text.txt");
        SystemCommandExecutor commande = new SystemCommandExecutor(command2);
        List<String> result = null;
        List<String> candidates = new ArrayList<>();

        try {
//            commande.executeCommand();
            commande.executeCommand();
            StringBuilder results = commande.getStandardOutputFromCommand();
              if(commande.getStandardErrorFromCommand().toString().length()>0){
                logger.error("Error while processing the CG grammar: "+commande.getStandardErrorFromCommand().toString());
            }
            Files.deleteIfExists(Paths.get(pProps.getProperty("tmpDir") + "cg3/text.txt"));
            Files.write(Paths.get(pProps.getProperty("tmpDir") + "cg3/text.txt"), cgStream.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
            result = Arrays.asList(results.toString().split(System.getProperty("line.separator")));
        } catch (IOException | InterruptedException ex) {
            logger.error("Error processing Constraint Grammar parser check the expection", ex);
        } finally {
            commande.closeStreams();
            StringBuilder sentence = new StringBuilder();
            String rule = null;
            boolean candidate = false;
            int i = 0;
            for (String string : result) {
                if (string.startsWith("\"<") && string.endsWith(">\"") && string.contains("@@@@@new_sentence@@@@@")) {
                    if (candidate) {
                        candidates.add(sentence.toString().trim() + "##" + rule + "##" + i);
                        i++;
                    }
                    candidate = false;
                    sentence = new StringBuilder();

                } else {
                    if (string.startsWith("\"<") && string.endsWith(">\"")) {
                        sentence.append(string.trim().substring(2, string.trim().length() - 2)).append(" ");
                    }
                    if (string.startsWith("\t") && string.contains("MAP:")) {
                        String[] lis = string.split("MAP:.*:");
                        String paco[] = lis[1].split("\\n");
                        rule = paco[0];
                        candidate = true;
                    }
                }

            }
        }
        return candidates;
    }

    /**
     * Process a sentence of the Document and return a candidates formed by the
     * sentence##rule_fired## (you should take care of the number of the sentence)
     *
     * @param sb - the CG2 compatible internal representation of the sentence (use
     * formatTokenizedSentence method)
     * @param pProps - The general properties of Lite containing dir information
     * @param pGrammar - The grammar to apply
     * @param pPenProperties - The properties that contain a map of the PenTree Bank tag set
     * @return
     */
    public String processSentence(StringBuilder sb, Properties pProps, String pGrammar, Properties pPenProperties) {
        String cgStream = parserToCG(sb.toString(), pPenProperties);
//        System.out.println(cgStream);
        try {
            Files.deleteIfExists(Paths.get(pProps.getProperty("tmpDir") + "cg3/text.txt"));
            Files.write(Paths.get(pProps.getProperty("tmpDir") + "cg3/text.txt"), cgStream.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
        } catch (IOException ex) {
            logger.error("Error while deleting and creating the CG compatible version of the sententce", ex);
        }
        List<String> command2 = new ArrayList<>();
        command2.add("/bin/sh");
        command2.add("-c");
        command2.add("cg3 --trace -g " + pGrammar + " -I " + pProps.getProperty("tmpDir") + "/cg3/text.txt");
//        commande = new SystemCommandExecutor(command);
        SystemCommandExecutor commande = new SystemCommandExecutor(command2);
        List<String> result = null;
        try {
//            commande.executeCommand();
            commande.executeCommand();
            StringBuilder results = commande.getStandardOutputFromCommand();
            result = Arrays.asList(results.toString().split(System.getProperty("line.separator")));
        } catch (IOException | InterruptedException ex) {
            logger.error("Error processing Constraint Grammar parser check the expection", ex);
        } finally {
            commande.closeStreams();
        }
        StringBuilder sentence = new StringBuilder();
        String rule = null;
        boolean candidate = false;
        for (String string : result) {
            if (string.startsWith("\"<") && string.endsWith(">\"")) {
                sentence.append(string.trim().substring(2, string.trim().length() - 2)).append(" ");
            }
            if (string.startsWith("\t") && string.contains("MAP:")) {
                String[] lis = string.split("MAP:.*:");
                String paco[] = lis[1].split("\\n");
                rule = paco[0];
                candidate = true;
            }
        }
        if (candidate) {
            return sentence.toString().trim() + "##" + rule + "##";
        } else {
            return null;
        }
    }

    public  static String parserToCG(String pString, Properties pPenProperties) {
//        System.out.println(pString);
        String newLine = System.getProperty("line.separator");
//        System.out.println(sTream);
        String[] lines = pString.split(newLine);
        int numLines = lines.length;
        String[] line;
        String word;
        FreeLing2LidomFormatter formater = new FreeLing2LidomFormatter();
        for (int i = 0; i < numLines; i++) {
            line = lines[i].split(" ");
            String aux = "";
            for (int j = 0; j < line.length; j++) {
                word = line[j];
                if (j == 0) {
                    formater.addWord("\"<" + word + ">\"");
                    formater.addLine();
                    aux = word.toLowerCase();
                }
                if ((j - 1) % 3 == 0) {
                    formater.addWord("\"" + aux + "\"");
                }
                if ((j - 2) % 3 == 0) {
                    formater.addWord(word + " " + pPenProperties.getProperty(word));
                    formater.addLine();
                }
            }
        }
        return formater.getContent();
    }
}
