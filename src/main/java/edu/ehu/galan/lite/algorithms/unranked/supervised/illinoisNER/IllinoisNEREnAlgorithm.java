package edu.ehu.galan.lite.algorithms.unranked.supervised.illinoisNER;

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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Algorithms that runs Illinois NER for english via external call, see
 * http://cogcomp.cs.illinois.edu/page/software_view/NETagger for more
 * documentation (tested on 2.3 extended types version)
 *
 *
 * @author Angel Conde Manjon
 */
public class IllinoisNEREnAlgorithm extends AbstractAlgorithm {

    private Document doc;
    private Properties props;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Properties pen;
    private final List<Term> termList;

    /**
     * Choosing a different config files, different languages could be processed
     *
     * @param pPropsDir
     *
     */
    public IllinoisNEREnAlgorithm(String pPropsDir) {
        super(true, "IllinoisNEREnAlgorithm");
        termList = super.getTermList();

    }

    @Override
    public void init(Document pDoc, String pPropsDir) {
        doc = pDoc;
        pen = new Properties();
        props = new Properties();
        try {
            pen.load(new FileInputStream(new File(pPropsDir + "configs/pennTree.conf")));
            props.load(new FileInputStream(new File(pPropsDir + "configs/general.conf")));

        }
        catch (IOException ex) {
            logger.error("Error loading properties file located in: resources/config/general.conf", ex);
        }
    }

    @Override
    public void runAlgorithm() {
        SystemCommandExecutor executor = new SystemCommandExecutor();
        executor.setWorkingDirectory(props.getProperty("IlliniNERDir"));

        List<String> command = new ArrayList<>();
        command.add("/bin/sh");
        command.add("-c");
        command.add("java -classpath \"\\\"./dist/LbjNerTagger-2.3.jar:./lib/commons-cli-1.2.jar:./lib/coreUtilities-0.1.1.jar:./lib/LBJ-2.8.2.jar:./lib/log4j-1.2.13.jar:./lib/lucene-core-2.4.1.jar:./lib/stanford-ner.src.jar:./lib/commons-configuration-1.6.jar:./lib/curator-client-0.6.jar:./lib/LBJLibrary-2.8.2.jar:./lib/logback-classic-0.9.17.jar:./lib/slf4j-api-1.6.1.jar:./lib/commons-lang-2.5.jar:./lib/curator-interfaces.jar:./lib/libthrift-0.4.jar:./lib/logback-core-0.9.17.jar:./lib/stanford-ner.jar:bin:\\\"\"" + " " + "-Xmx6g edu.illinois.cs.cogcomp.LbjNer.LbjTagger.NerTagger -annotate " + doc.getPath() + " " + props.get("tmpDir") + "IllinoisNERResults" + " " + props.getProperty("IlliniNERConfigFile"));
        logger.info("Will Process Illinois NER, 6GB of ram will be required at least and will take a while.....");
        executor.addCommand(command);

        try {
            executor.executeCommand();
        }
        catch (IOException | InterruptedException ex) {
            logger.error("Error executing Illinois NER command", ex);
        }

        logger.info("Illinois NER finished");
        try {
            List<Term> termss = new ArrayList<>();
            List<String> list = Files.readAllLines(Paths.get(props.get("tmpDir") + "IllinoisNERResults"), StandardCharsets.UTF_8);
            for (String string : list) {
                Pattern p = Pattern.compile("\\[(.*?)\\]");
                Matcher m = p.matcher(string);
                while (m.find()) {
                    String[] term = m.group(1).split("\\s");
                    String terms = "";
                    for (int i = 1; i < term.length; i++) {
                        terms = terms + term[i] + " ";

                    }
                    termss.add(new Term(terms.trim()));
//                    System.out.println(terms);
                }
            }
            List<Term> ters = list.stream().sorted().map(s -> new Term(s)).collect(Collectors.toList());
            doc.addListTerm(new ListTerm(this.getName(), ters));

        }
        catch (IOException ex) {
            logger.info("Error getting Illinois NER results", ex);
        }
//        saveToTmp();
    }

}
