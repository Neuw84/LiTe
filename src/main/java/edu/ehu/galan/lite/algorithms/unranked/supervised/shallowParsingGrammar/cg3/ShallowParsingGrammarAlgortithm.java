package edu.ehu.galan.lite.algorithms.unranked.supervised.shallowParsingGrammar.cg3;
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

import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Token;
import edu.ehu.galan.lite.parsers.english.PlainTextDocumentReaderFreeLingEn;
import edu.ehu.galan.lite.utils.freeLingUtils.FreeLing2LidomFormatter;
import edu.ehu.galan.lite.utils.systemUtils.SystemCommandExecutor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that processes the Shallow Parsing Grammar algorithm for english using VISL CG-3 parser,
 * CogComp POS tagger:http://cogcomp.cs.illinois.edu/page/software_view/POS and CogComp Chunker:
 * http://cogcomp.cs.illinois.edu/page/software_view/Chunker
 *
 * The CG-3 must be installed in the system (the command cg3 must be available from the command line)
 * and both the CogComp POS tagger and CogComp Chunker are in the classpathz
 * 
 * @author Angel Conde Manjon
 */
public class ShallowParsingGrammarAlgortithm extends AbstractAlgorithm {

    private SystemCommandExecutor commande;
    private Properties props;
    private final List<Term> termList;
    private final String grammar;
    private Document doc;
    private Properties pen;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String propsDir = null;
    private final transient String dir;

    /**
     * Initializes the algorithm using the grammar file, and using as temp dirs the desired one
     * @param pGrammarFile
     * @param pProcDir
     */
    public ShallowParsingGrammarAlgortithm(String pGrammarFile,String pProcDir) {
        super(true, "ShallowParsingGrammar");
        grammar = pGrammarFile;
        dir=pProcDir;
        termList = super.getTermList();

    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        doc = pDoc;
        pen = new Properties();
        props = new Properties();
        propsDir = pPropsDir;
        try {
            pen.load(new FileInputStream(pPropsDir + "lite/configs/pennTree.conf"));
            props.load(new FileInputStream(pPropsDir + "lite/configs/general.conf"));
        } catch (IOException ex) {
            logger.error("Error reading properties files", ex);
        }
    }

    @Override
    public void runAlgorithm() {
        generateTempFolders();
        List<String> candidateList;
//        StringBuilder sb = new StringBuilder();
        PlainTextDocumentReaderFreeLingEn en = new PlainTextDocumentReaderFreeLingEn(propsDir);
//        for (Iterator<Document> it = corpus.getIterator(); it.hasNext();) {
//            Document doc = it.next();
        en.readSource(doc.getPath());
        StringBuilder docr = new StringBuilder();
        //int i = 0;
        List<LinkedList<Token>> tokenList = en.getTokenizedSentenceList();
        //TODO, cheap identifier to process all the grammar in one CG3 service call
        docr.append("@@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@").append(System.getProperty("line.separator"));
        // long start = System.currentTimeMillis();

        for (LinkedList<Token> token : tokenList) {
            for (ListIterator<Token> itr = token.listIterator(); itr.hasNext();) {
                Token tok = itr.next();
//                sb.append(tok.getWordForm()).append(" ").append(tok.getLemma()).append(" ").append(tok.getPosTag()).append(System.getProperty("line.separator"));
                docr.append(tok.getWordForm()).append(" ").append(tok.getLemma()).append(" ").append(tok.getPosTag()).append(System.getProperty("line.separator"));
            }
//            String result = processSentence(sb);
//            if (result != null) {
////                System.out.println(result);
//                candidateList.add(result + i);
//            }
            docr.append("@@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@ @@@@@new_sentence@@@@@").append(System.getProperty("line.separator"));
//            sb = new StringBuilder();
            // i++;
        }
      //  long end = System.currentTimeMillis();

        //System.out.println("Execution time was " + (end - start) + " ms.");
        //start = System.currentTimeMillis();
        candidateList = processDocument(docr);
        //end = System.currentTimeMillis();
        //System.out.println("Execution time was " + (end - start) + " ms.");
//        }
//        for (String string : candidateList) {
//            System.out.println(string);
//        }

        TopicExtractorEnglish topic = new TopicExtractorEnglish(doc, propsDir);
        topic.loadCandidates(candidateList);
        topic.extractTopics();
        topic.cleanTopics();
        List<String> list = topic.getTopics();
        List<Term> terms = list.stream().map(s -> new Term(s)).collect(Collectors.toList());
        doc.addListTerm(new ListTerm(this.getName(), terms));
        super.setTermList(terms);
//        super.saveToTmp();
//        termList.clear();
        if (commande != null) {
            commande.closeStreams();
        }

//        saveToTmp();
    }

    private String freelingToCG(String sTream) {
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
                    formater.addWord(word + " " + pen.getProperty(word));
                    formater.addLine();
                }
            }
        }
        return formater.getContent();
    }

    /**
     * deletes and create temporals folders needed for analysis delete first be carefoul about
     * this!!
     */
    private void generateTempFolders() {
        String tempFold = props.getProperty("tmpDir");
        try {
            FileUtils.deleteQuietly(new File(props.getProperty("tmpDir") + "/cg3"));
            Files.createDirectory(Paths.get(props.getProperty("tmpDir") + "/cg3"));

        } catch (java.nio.file.FileAlreadyExistsException ex1) {
            logger.debug("directory already exist");
        } catch (IOException ex) {
            logger.error("Error while creating temporal folders", ex);
        }
        logger.debug("Temporals folder created");
    }

    private void stringToTextFile(String sTream) {
        PrintWriter pw = null;
        try {
            Files.deleteIfExists(Paths.get(props.getProperty("tmpDir") + "cg3/text.txt"));
        } catch (IOException ex) {
            logger.warn("Error deleting temporal file for CG3 analysis", ex);
        }

        try (FileWriter fichero = new FileWriter(props.getProperty("tmpDir") + "cg3/text.txt")) {
            pw = new PrintWriter(fichero);
            pw.println(sTream);
        } catch (IOException e) {
            logger.error("Error while creating temporal text file for analysis", e);
        } finally {
            if (null != pw) {
                pw.close();
            }
        }

    }

    private List<String> processDocument(StringBuilder sb) {
        String cgStream = freelingToCG(sb.toString());
//        System.out.println(cgStream);
        stringToTextFile(cgStream);
        List<String> command = new ArrayList<>();
        command.add("/bin/sh");
        command.add("-c");
        command.add("cg3 --trace -g " + grammar + " -I " + props.getProperty("tmpDir") +dir +"text.txt -O " + props.getProperty("tmpDir") +dir+ "cg3");

        List<String> command2 = new ArrayList<>();
        command2.add("/bin/sh");
        command2.add("-c");
        command2.add("cg3 --trace -g " + grammar + " -I " + props.getProperty("tmpDir") + "cg3/text.txt");
//        commande = new SystemCommandExecutor(command);
        commande = new SystemCommandExecutor(command2);
        List<String> result = null;
        List<String> candidates = new ArrayList<>();

        try {
//            commande.executeCommand();
            commande.executeCommand();
            StringBuilder results = commande.getStandardOutputFromCommand();
            stringToTextFile(results.toString());

            result = Arrays.asList(results.toString().split(System.getProperty("line.separator")));
        } catch (IOException | InterruptedException ex) {
            logger.error("Error while processing the sentence", ex);
        } finally {
//            commande.closeStreams();
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

    private String processSentence(StringBuilder sb) {
        String cgStream = freelingToCG(sb.toString());
//        System.out.println(cgStream);
        stringToTextFile(cgStream);
        List<String> command = new ArrayList<>();
        command.add("/bin/sh");
        command.add("-c");
        command.add("cg3 --trace -g " + grammar + " -I " + props.getProperty("tmpDir") + "/cg3/text.txt -O " + props.getProperty("tmpDir") + "/cg3/cg3");

        List<String> command2 = new ArrayList<>();
        command2.add("/bin/sh");
        command2.add("-c");
        command2.add("cg3 --trace -g " + grammar + " -I " + props.getProperty("tmpDir") + "/cg3/text.txt");
//        commande = new SystemCommandExecutor(command);
        commande = new SystemCommandExecutor(command2);
        List<String> result = null;
        try {
//            commande.executeCommand();
            commande.executeCommand();
            StringBuilder results = commande.getStandardOutputFromCommand();
            result = Arrays.asList(results.toString().split(System.getProperty("line.separator")));
        } catch (IOException | InterruptedException ex) {
            logger.error("Error while processing the sentence", ex);
        } finally {
//            commande.closeStreams();

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

}
