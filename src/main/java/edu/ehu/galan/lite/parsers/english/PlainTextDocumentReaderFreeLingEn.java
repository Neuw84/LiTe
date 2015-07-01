package edu.ehu.galan.lite.parsers.english;
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
 * Class that reads plain text and asing POS tags using FreeLing via external
 * call
 * http://nlp.lsi.upc.edu/freeling/
 *
 * @author Angel Conde Manjon
 */
public class PlainTextDocumentReaderFreeLingEn extends AbstractDocumentReader {

    private final Properties props;
    private final Properties pen;
    private transient final Logger logger = LoggerFactory.getLogger(PlainTextDocumentReaderFreeLingEn.class);

    /**
     *
     * @param pProps
     */
    public PlainTextDocumentReaderFreeLingEn(String pProps) {
        super();
        props = new Properties();
        pen = new Properties();
        try {
            pen.load(new FileInputStream(new File(pProps + "lite/configs/pennTree.conf")));
            props.load
        (new FileInputStream(new File(pProps + "lite/configs/general.conf")));
        }
        catch (IOException ex) {
            logger.error("Error while loading properties files, check the dirs? ", ex);
        }
    }

    /**
     * Reads a text plain text file, using FreeLing and adds POS tags using PENN
     * treebank, expects a freeling conf file in
     * resources/configs/freeling/enPOS.cfg See http://nlp.lsi.upc.edu/freeling/
     * for more info. The splitter.dat option  AllowBetweenMarkers should be set to 1
     *
     * @param pFile
     */
    @Override
    public void readSource(String pFile) {
        List<String> command = new ArrayList<>();
        command.add("/bin/sh");
        command.add("-c");
        command.add(props.getProperty("freelingDir") + "analyze  -f resources/lite/configs/freeling/enPOS.cfg < " + pFile);
        SystemCommandExecutor executor = new SystemCommandExecutor(command);
        try {
            //we allow for the command 20 segs of processor time if not we assume that the file should be cleanded as the parser have been into trouble
            int result = executor.executeCommand(20);
            if(result==-1){
                logger.error("Please clean your text file, seems that FreeLing is not able to deal with your file, please 'clean' it");
            }
        }
        catch (IOException | InterruptedException ex) {
            logger.error("Error while executing freeLing command, check the FreeLing dir and the configs resources for FreeLing ", ex);
        }
        String st = executor.getStandardOutputFromCommand().toString();
        String result = freelingToLidom(st);
        List<String> list = Arrays.asList(result.split(System.getProperty("line.separator")));
        boolean aux = false;
        String wordForm = null;
        String wordPos = null;
        String wordLemma = null;
        List<String> lisWords = new ArrayList<>();
        List<String> lisPos = new ArrayList<>();
        List<String> listLemmas = new ArrayList<>();
        for (String string : list) {
            if (string.contains("\"<")) {
                wordForm = string.substring(2, string.length() - 3);
                aux = false;
            }
            if (!aux) {
                if (string.startsWith("\t")) {
                    wordPos = string.split("\t")[1].split("\\s")[1];
                    wordLemma = string.split("\t")[1].split("\\s")[0];
                    wordLemma = wordLemma.substring(1, wordLemma.length() - 1);
                    aux = true;
                    lisWords.add(wordForm);
                    lisPos.add(wordPos);
                    listLemmas.add(wordLemma);

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
                tokenizedSentence.add(new Token(lisWords.get(i), lisPos.get(i), listLemmas.get(i)));
            } else {
                if (lisWords.get(i).equalsIgnoreCase(".") && lisPos.get(i).equalsIgnoreCase("Fp")) {
                    first = true;
                    sentence = sentence + " " + lisWords.get(i);
//                    System.out.println(sentence);
                    sentenceList.add(sentence);
                    tokenizedSentence.add(new Token(lisWords.get(i), lisPos.get(i), listLemmas.get(i)));
                    tokenizedSentenceList.add(tokenizedSentence);
                } else {
                    tokenizedSentence.add(new Token(lisWords.get(i), lisPos.get(i), listLemmas.get(i)));
                    sentence = sentence + " " + lisWords.get(i);
                }
            }

        }
        executor.closeStreams(); //close the streams, if not we may get too much file open Error!

    }

////    public static void main(String[] args) {
////        PlainTextDocumentReaderFreeLingEn reader = new PlainTextDocumentReaderFreeLingEn(System.getProperty("user.dir") + "/resources/");
////        reader.readSource("testCorpus/textAstronomy");
////        reader.printPosTagsAndChunked();
////    }
////    
    private String freelingToLidom(String sTream) {
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
                    formater.addWord(word + " " + pen.getProperty(word));
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
