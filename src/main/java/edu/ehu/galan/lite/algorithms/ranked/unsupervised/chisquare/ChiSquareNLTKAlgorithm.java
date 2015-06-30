package edu.ehu.galan.lite.algorithms.ranked.unsupervised.chisquare;

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
 * Class that processes the Chi Square algorithm using the NLTK python toolkit
 *
 * @author Angel Conde Manjon
 */
public class ChiSquareNLTKAlgorithm extends AbstractAlgorithm {

    private final String BRIGRAM = "brigram_chi_sq";
    private final String TRIGRAM = "trigram_chi_sq";
    private SystemCommandExecutor command;
    private Properties props;
    private Document doc;
    private int frequencyRequired = 0;
    private final List<Term> termList;
    private String pPropsDir;
    final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractAlgorithm.class);

    /**
     *
     * @param pFrequencyRequired
     */
    public ChiSquareNLTKAlgorithm(int pFrequencyRequired) {
        super(true, "ChiSquare");
        frequencyRequired = pFrequencyRequired;
        termList = super.getTermList();

    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        doc = pDoc;
        props = new Properties();
        this.pPropsDir = pPropsDir;
        try {
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (IOException ex) {
            logger.error("Error loading properties file located in: resources/config/general.conf", ex);
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
            List<Term> termL = new ArrayList<>();
            List<String> terms = Arrays.asList(results.toString().split("\n"));
            if(terms.size()!=1 && !terms.get(0).isEmpty())
            for (String string : terms) {
                String[] term = string.split("\t");
                if (term.length == 3 || term.length == 4) {
                    if (term.length == 3) {
                        termL.add(new Term(term[0].concat(" ").concat(term[1]), Float.parseFloat(term[2])));
                    } else {
                        termL.add(new Term(term[0].concat(" ").concat(term[1]).concat(" ").concat(term[2]), Float.parseFloat(term[3])));
                    }
                }
            }
            List<Term> finalList = termL.stream().sorted(Comparator.comparing(Term::getScore)).collect(Collectors.toList());
            doc.addListTerm(new ListTerm(this.getName(), finalList));
        } catch (ArrayIndexOutOfBoundsException | IOException | InterruptedException ex) {
            logger.warn(ChiSquareNLTKAlgorithm.class.getName(), "Error while running the algorithm, check your bin dirs and NLTK", ex);
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
//      Corpus c = new Corpus("en");
//        c.loadCorpus("testCorpus",Document.SourceType.wikipedia);
//        ChiSquareNLTKAlgorithm alg = new ChiSquareNLTKAlgorithm(3);
//        alg.init(c.getDocQueue().getFirst(),System.getProperty("user.dir") + "/resources/");
//        alg.runAlgorithm();
//
//    }
}
