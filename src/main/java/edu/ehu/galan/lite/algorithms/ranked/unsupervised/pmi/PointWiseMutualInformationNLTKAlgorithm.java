package edu.ehu.galan.lite.algorithms.ranked.unsupervised.pmi;

/*
 *    PointWiseMutualInformationNLTKAlgorithm.java
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
import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Corpus;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.utils.systemUtils.SystemCommandExecutor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

/**
 * Class that processes the LikeHoodRatio algorithm using the NLTK python toolkit, via externall
 * call
 *
 * NLTK 3.0 requires Python versions 2.6-2.7. You must have installed in your system python and NLTK
 * in order to run this algorithm
 *
 * @author Angel Conde Manjon
 */
public class PointWiseMutualInformationNLTKAlgorithm extends AbstractAlgorithm {

    private Document doc;
    private final String BRIGRAM = "bigram_pmi";
    private final String TRIGRAM = "trigram_pmi";
    private SystemCommandExecutor command;
    private Properties props;
    private Corpus corpus;

    private int frequencyRequired = 0;
    private final List<Term> termList;
    final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractAlgorithm.class);
    private String pPropsDir;

    /**
     *
     * @param pFrequencyRequired
     */
    public PointWiseMutualInformationNLTKAlgorithm(int pFrequencyRequired) {
        super(true, "PMI");
        props = new Properties();
        frequencyRequired = pFrequencyRequired;
        termList = super.getTermList();
    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        doc = pDoc;
        this.pPropsDir = pPropsDir;
        props = new Properties();
        this.pPropsDir = pPropsDir;
        try {
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (IOException ex) {
            logger.error(this.getClass().getName(), "Error loading properties file located in: resources/config/general.conf", ex);
        }
    }

    @Override
    public void runAlgorithm() {
        //python chi_square.py /home/angel/NetBeansProjects/WikiTerm/testCorpus/ 1 trigram_pmi 3 textAstronomy
        command = new SystemCommandExecutor(buildCommand(BRIGRAM));
        try {
            command.executeCommand();
            StringBuilder results = command.getStandardOutputFromCommand();
            command = new SystemCommandExecutor(buildCommand(TRIGRAM));
            command.executeCommand();
            results.append(command.getStandardOutputFromCommand());
            List<String> terms = Arrays.asList(results.toString().split("\n"));
            List<Term> termL = new ArrayList<>();
            if(terms.size()!=1 && !terms.get(0).isEmpty())
            for (String string : terms) {
                String[] term = string.split("\t");
                if (term.length == 3) {
                    termL.add(new Term(term[0].concat(" ").concat(term[1]), Float.parseFloat(term[2])));
                } else {
                    termL.add(new Term(term[0].concat(" ").concat(term[1]).concat(" ").concat(term[2]), Float.parseFloat(term[3])));
                }
            }
            List<Term> finalList = termL.stream().sorted(Comparator.comparing(Term::getScore)).collect(Collectors.toList());
            doc.addListTerm(new ListTerm(this.getName(), finalList));
        } catch (ArrayIndexOutOfBoundsException | IOException | InterruptedException ex) {
            logger.warn(PointWiseMutualInformationNLTKAlgorithm.class.getName(), "Error while running the algorithm,check your dirs and NLTK installaiton", ex);
        }

    }

    private List<String> buildCommand(String pType) {
        List<String> commandList = new ArrayList<>();
        commandList.add("python");
        commandList.add(pPropsDir + "lite/python/wikiterm.py");
        commandList.add(doc.getPath().substring(0, doc.getPath().length() - doc.getName().length()));
        commandList.add("1");
        commandList.add(pType);
        commandList.add(Integer.toString(frequencyRequired));
        commandList.add(doc.getName());
        return commandList;
    }

//    public static void main(String[] args) {
//        Corpus c = new Corpus("en");
//        c.loadCorpus("testCorpus", Document.SourceType.wikipedia);
//        PointWiseMutualInformationNLTKAlgorithm alg = new PointWiseMutualInformationNLTKAlgorithm(3);
//        alg.init(c.getDocQueue().getFirst(), System.getProperty("user.dir") + "/resources/");
//        alg.runAlgorithm();
//        List<Term> termList = new ArrayList<>();
//        List<String> terms = null;
//        List<String> stopWords = null;
//        try {
//            terms = Files.readAllLines(Paths.get("resources/lite/python/results_chi"), Charset.defaultCharset());
//            stopWords = Files.readAllLines(Paths.get("resources/lite/stopWordLists/ShallowCleaner/english"), Charset.defaultCharset());
//        } catch (IOException ex) {
//            Logger.getLogger(PointWiseMutualInformationNLTKAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        for (String string : terms) {
//            String[] term = string.split("\t");
//            if (term.length == 3) {
//                termList.add(new Term(term[0].concat(" ").concat(term[1]), Float.parseFloat(term[2])));
//            } else {
//                termList.add(new Term(term[0].concat(" ").concat(term[1]).concat(" ").concat(term[2]), Float.parseFloat(term[3])));
//            }
//
//        }
//        List<Term> clean=new ArrayList<>();
//        Collections.sort(termList, (t1, t2) -> t1.getScore() > t2.getScore() ? -1 : t1.getScore() == t2.getScore() ? 0 : 1);
//        for (Term t : termList) {
//            String[] pol = t.getTerm().split(" ");
//            boolean aux = true;
//            for (int i = 0; i < pol.length; i++) {
//                String string = pol[i];
//                if (stopWords.contains(string.trim().toLowerCase())) {
//                    aux = false;
//                    break;
//                }
//
//            }
//            if (aux) {
//                System.out.println(t);
//                clean.add(t);
//            }
//
//        }
//        List<String> cGoldIndex = null;
//        try {
//            PlingStemmerEn pling = new PlingStemmerEn();
//            cGoldIndex = Files.readAllLines(Paths.get("GoldTopicsAstronomy"), StandardCharsets.UTF_8);
//            cGoldIndex = cGoldIndex.stream().map(s -> pling.stem(s.trim())).collect(Collectors.toList());
//        } catch (IOException ex1) {
//        }
//
//        float recall;
//        int positives = 0;
//        PlingStemmerEn pling = new PlingStemmerEn();
//
//        int total = clean.size();
//        List<String> finals = new ArrayList<>();
//        for (String gold : cGoldIndex) {
//
//            for (Term term : clean) {
//                boolean found = false;
//                if (term.getTerm().equalsIgnoreCase("RNA polymerase") && gold.equalsIgnoreCase("RNA polymerase")) {
////                    System.out.println("dadsads");
//                }
//                if (pling.stem(term.getTerm()).equalsIgnoreCase(pling.stem(gold))) {
//                    found = true;
//                    finals.add(gold);
//                    positives++;
//                    break;
//                }
//                if (found) {
//                    positives++;
//                    break;
//                }
//            }
//        }
//        System.out.println(positives+ "    "+total);
   // }
}
