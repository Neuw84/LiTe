package edu.ehu.galan.lite.algorithms.unranked.supervised.freeLingNerEn;


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
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.utils.freeLingUtils.FreeLing2LidomFormatter;
import edu.ehu.galan.lite.utils.systemUtils.SystemCommandExecutor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Algorithms that runs FreeLing NER via external call, see http://nlp.lsi.upc.edu/freeling/ for
 * more documentation (tested on FreeLing 3.1)
 *
 * For using this method FreeLing must be installed in the system, and the freeLing dir must be
 * configured in resources/lite/configs/general.conf
 *
 * @author Angel Conde Manjon
 */

public class FreeLingNerAlgorithm extends AbstractAlgorithm {

    private Document doc;
    private Properties props;
    private final String configFile;
    private Properties pen;
    private final List<Term> termList;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String file="";

    /**
     * Choosing a different config files, different languages could be processed
     *
     * @param pConfigFile - the name of the config file of freeling with the NER option active, the
     * config files are in resources/configs/freeling dir (for example enPOSMW.cfg for NER MW
     * detection in english)
     *
     */
    public FreeLingNerAlgorithm(String pConfigFile) {
        super(true, "FreeLingNer");
        configFile = pConfigFile;
        termList = super.getTermList();
    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        doc = pDoc;
        pen = new Properties();
        props = new Properties();
        file=pDoc.getPath();
        try {
            pen.load(new FileInputStream(new File(pPropsDir + "lite/configs/pennTree.conf")));
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (IOException ex) {
            logger.error("Error loading properties file located in: resources/config/general.conf", ex);
        }
    }

    @Override
    public void runAlgorithm() {
        List<String> command = new ArrayList<>();
        command.add("/bin/sh");
        command.add("-c");
        command.add(props.getProperty("freelingDir") + "analyze " + "--outf morfo " + "-f " +  configFile + " < " + file);
        SystemCommandExecutor executor = new SystemCommandExecutor(command);
        try {
            executor.executeCommand();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error executing FreeLingNer", ex);
        }
        String st = executor.getStandardOutputFromCommand().toString();
        String result = freelingToErauntz(st);
        List<String> list = Arrays.asList(result.split(System.getProperty("line.separator")));
        String wordForm = null;
        boolean aux = true;
        List<String> topics = new ArrayList<>();
        for (String string : list) {
            if (string.contains("\"<")) {
                wordForm = string.substring(2, string.length() - 3);
                aux = false;
            }
            if (!aux) {
                if (string.startsWith("\t")) {
                    String pavo = (string.split("\t")[1].split("\\s")[1]);
                    String temp;
                    if (pavo.contains("_")) {
                        pavo = pavo.replaceAll("_", "\\s");
                    }
                    if (pavo.equals("NP")) {
                        if (wordForm.contains("_")) {
                            wordForm = wordForm.replaceAll("_", " ");
                        }
                        topics.add(wordForm);

                    }
                    aux = true;

                }
            }
        }
        List<Term> terms = topics.stream().sorted().map(s -> new Term(s)).collect(Collectors.toList());
        terms= terms.stream().distinct().collect(Collectors.toList());
        doc.addListTerm(new ListTerm(this.getName(), terms));

       termList.clear();

    }

    private String freelingToErauntz(String sTream) {
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

}
