package edu.ehu.galan.lite.parsers.spanish;

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

import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.parsers.AbstractDocumentReader;
import edu.ehu.galan.lite.utils.freeLingUtils.FreeLing2LidomFormatter;
import edu.ehu.galan.lite.utils.systemUtils.SystemCommandExecutor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that reads plain text and assign POS tags using FreeLing via external call
 * http://nlp.lsi.upc.edu/freeling/
 * @author Angel Conde Manjon
 */
public class PlainTextDocumentReaderFreeLingEs extends AbstractDocumentReader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Properties props;
    private String loc;

    /**
     *
     * @param pProps
     */
    public PlainTextDocumentReaderFreeLingEs(String pProps) {
        super();
        props = new Properties();
        try {
            props.load(new FileInputStream(new File(pProps + "lite/configs/general.conf")));
            loc = pProps + "lite/";
        } catch (IOException ex) {
            logger.error("Error loading lite/confings/general.conf", ex);
        }
    }

    /**
     * Reads a text plain text file, using FreeLing and adds POS tags See
     * http://nlp.lsi.upc.edu/freeling/ for more info. The splitter.dat option AllowBetweenMarkers
     * should be set to 1
     *
     * @param pFile - String
     */
    @Override
    public void readSource(String pFile) {
        List<String> command = new ArrayList<>();
        command.add("/bin/sh");
        command.add("-c");
        command.add(props.getProperty("freelingDir") + "analyze " + "--outf morfo " + "-f " + loc + "configs/freeling/esPOS.cfg" + " < " + pFile);
        SystemCommandExecutor executor = new SystemCommandExecutor(command);
        try {
            executor.executeCommand();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error executing command for FreeLing parser", ex);
        }
        String st = executor.getStandardOutputFromCommand().toString();
        String result = freelingToErauntz(st);
//        System.out.println(result);
        List<String> list = Arrays.asList(result.split(System.getProperty("line.separator")));
        boolean aux = false;
        String wordForm = null;
        String wordPos = null;
        String wordLemma = null;
        List<String> lisWords = new ArrayList<>();
        List<String> lisPos = new ArrayList<>();
        List<String> lisLemma = new ArrayList<>();
        for (String string : list) {
            if (string.contains("\"<")) {
                wordForm = string.substring(2, string.length() - 3);
                aux = false;
            }
            if (!aux) {
                if (string.startsWith("\t")) {
                    String[] lemmaFormwordLema = string.split("\t")[1].split("\\s");
                    //TODO in spanish all words are formed from one component but in another languages\
                    //will we better to deal in a different way this part
                    wordPos = lemmaFormwordLema[1];

                    wordLemma = lemmaFormwordLema[0].substring(1, lemmaFormwordLema[0].length() - 1);
                    aux = true;
                    lisWords.add(wordForm);
                    lisPos.add(wordPos);
                    lisLemma.add(wordLemma);
                }
            }
        }
        boolean first = true;
        String sentence = "";
        LinkedList<Token> tokenizedSentence = null;
        for (int i = 0; i < lisPos.size(); i++) {
            if (first) {
                sentence = "";
                tokenizedSentence = new LinkedList<>();
                first = false;
                sentence += lisWords.get(i);
                tokenizedSentence.add(new Token(lisWords.get(i), lisPos.get(i), lisLemma.get(i)));
            } else {
                if (lisWords.get(i).equalsIgnoreCase(".") && lisPos.get(i).equalsIgnoreCase("Fp")) {
                    first = true;
                    sentence = sentence + " " + lisWords.get(i);
//                    System.out.println(sentence);
                    sentenceList.add(sentence);
                    tokenizedSentence.add(new Token(lisWords.get(i), lisPos.get(i), lisLemma.get(i)));
                    tokenizedSentenceList.add(tokenizedSentence);
                } else {
                    tokenizedSentence.add(new Token(lisWords.get(i), lisPos.get(i), lisLemma.get(i)));
                    sentence = sentence + " " + lisWords.get(i);
                }
            }

        }
        executor.closeStreams();
    }

//    public static void main(String[] args) {
//        PlainTextDocumentReaderFreeLingEs reader = new PlainTextDocumentReaderFreeLingEs(System.getProperty("user.dir") + "/resources/lite/");
//        reader.readSource("spanishCorpus/text");
//    }
    private static String freelingToErauntz(String sTream) {
//        System.out.println(sTream);
        String newLine = System.getProperty("line.separator");
//        System.out.println(sTream);
        String[] lines = sTream.split(newLine);
        int numLines = lines.length;
        String[] line;
        String word;
        FreeLing2LidomFormatter formater = new FreeLing2LidomFormatter();
        for (int i = 0; i < numLines; i++) {
            line = lines[i].split(" ");
            for (int j = 0; j < line.length; j++) {
                word = line[j];
                if (j == 0) {
                    formater.addWord("\"<" + word + ">\"");
                    formater.addLine();
                }
                if ((j - 1) % 3 == 0) {
                    formater.addWord("\"" + word + "\"");
                }
                if ((j - 2) % 3 == 0) {
                    formater.addWord(word);
                    formater.addLine();
                }
            }
        }
        return formater.getContent();
    }

//    private void parseFreeLingOutput(String pOutput) {
//        String[] freeLing = pOutput.split("\\n");
////        System.out.println("guau");
//        LinkedList<Token> sentence = new LinkedList<>();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < freeLing.length; i++) {
//            String string = freeLing[i];
//            if (string.equals("")) {
//                tokenizedSentenceList.add(sentence);
//                sentence = new LinkedList<>();
//                sentenceList.add(sb.toString());
//                sb = new StringBuilder();
//            } else {
//                String[] free = string.split("\\s");
//                sb.append(free[0]).append(" ");
//                sentence.add(new Token(free[0], free[2]));
//            }
//        }
//    }
}
