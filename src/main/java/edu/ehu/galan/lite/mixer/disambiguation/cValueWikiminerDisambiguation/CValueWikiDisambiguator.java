package edu.ehu.galan.lite.mixer.disambiguation.cValueWikiminerDisambiguation;

/*
 *    CValueWikiDisambiguator.java
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
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.mixer.disambiguation.AbstractDisambiguation;
import edu.ehu.galan.lite.model.Topic;
import edu.ehu.galan.lite.utils.wikiminer.WikiminnerHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 * Class that disambiguates topics using Wikiminner (global disamgiguation method 
 * by Milne&Witten)
 * and the Terms that have
 * highest CValue score with only one meaning/sense in Wikipedia
 *
 * @author Angel Conde Manjon
 */
public class CValueWikiDisambiguator extends AbstractDisambiguation {

    //default values
    private int goldTerms = 8;
    private final List<String> cGold;
    private final transient Properties props;
    private final transient WikiminnerHelper helper;
    private transient final org.slf4j.Logger logger = LoggerFactory.getLogger(CValueWikiDisambiguator.class);

    /**
     * Default constructor
     *
     * @param pPropsDir
     * @param pHelper
     */
    public CValueWikiDisambiguator(String pPropsDir,WikiminnerHelper pHelper) {
        super("CValueWikiDisambiguator", pPropsDir);
        props = new Properties();
        helper=pHelper;
        try {
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            System.out.println("general.conf not found");
            logger.error(CValueWikiDisambiguator.class.getName(), ex);
        } catch (IOException ex) {
            logger.error(CValueWikiDisambiguator.class.getName(), ex);
        }
        goldTerms = Integer.parseInt(props.getProperty("goldTerms"));
        cGold = new ArrayList<>();
    }

//    public static void main(String[] args) {
//        WikiMapReader reader = new WikiMapReader();
//        List<Topic> topics = reader.readFile("tmp/wikiMap");
//        Corpus c = new Corpus("en");
//        c.setTopicList(topics);
//        new CValueWikiDisambiguator().disambiguateTopics(c); //new Disambiguation().filter("AstronomyFinalList");
//        System.exit(0);
//    }
//    private void disambiguateTopic(Topic top) {
//        Compare comp;
//        int count = 0;
//        int lenght = cGold.size();
////        System.out.println("disambiguating: " + top.getTopic());
//        for (String gold : cGold) {
//            comp = wiki.compareTopics(top.getTopic(), gold);
//            if (comp != null) {
//                if (comp.getTerm1() != null) {
//                    top.addProbableSense(comp.getTerm1(), comp.getTerm1Id());
//                } else {
//                    count++;
//                }
//            }
//        }
//        if (count == lenght) {
//            top.setDisambiguationFail(true);
//        }
////        System.out.println(top.getGoodSense() + "\t" + top.getWikiId());
//    }

    private List<Topic> disambiguate(List<Topic> topicsToDisam) {
        return helper.disambiguate(topicsToDisam, cGold);
    }

    @Override
    public void disambiguateTopics(Document pDoc) {
        if(!pDoc.getTopicList().isEmpty()){
        //   for (Document doc : pCorpus.getDocQueue()) {
        topicList.clear();
        cGold.clear();
        topicList = pDoc.getTopicList();
        obtainGoldTerms(pDoc);
        logger.info("The obtained gold terms are: " + cGold.toString());
        List<Topic> topicsToDisam = new ArrayList<>();
        List<Topic> topicsClean = new ArrayList<>();
        //TODO check here stream use (nb implementation doesn't work)
          for (Topic top : topicList) {
            top.initializeSenseCount();
            if (top.getId() == -1) {
                topicsToDisam.add(top);
//                disambiguateTopic(top);
            } else {
                topicsClean.add(top);
            }
        }
        List<Topic> disam = disambiguate(topicsToDisam);
        if(disam!=null){
        disam.stream().forEach((topic) -> {
            topicsClean.add(topic);
        });
        }
        pDoc.setTopicList(topicsClean);
        //saveToTmp();
        //     }
    }else{
      logger.info("The topic list appears to be empty");

        }}

    private void obtainGoldTerms(Document pDoc) {
        if(pDoc.getDomainTopics().isEmpty()){ //if we want to trick the method
        cGold.clear();
        int cvalue = -1;
        for (int i = 0; i < pDoc.getTermList().size(); i++) {
            ListTerm terms = pDoc.getTermList().get(i);
            if (terms.getName().equalsIgnoreCase("CValue")) {
                cvalue = i;
                break;
            }
        }
        if (cvalue != -1) {

            ListTerm cval = pDoc.getTermList().get(cvalue);
            List<Term> cList = cval.getTermList();
            int numTerms = 0;
            Topic top = null;
            for (Term string : cList) {
                boolean aux = false;
                for (Topic pTopic : topicList) {
                    if (pTopic.getTopic().equalsIgnoreCase(string.getTerm())) {
                        aux = true;
                        top = pTopic;
                        break;
                    }
                }
                if (aux != false) {
                    if(top != null){
//                        System.out.println(top);
                    if (top.getSenseList().size() == 1) {
                        pDoc.addDomainTopic(top);
                        cGold.add(top.getSenseList().get(0));
//                System.out.println(top.getSourceTitle());
                        numTerms++;
                        if (numTerms == getGoldTerms()) {
                            break;
                        }
                    }}
                }
            }
        } else {
            logger.error("This disambiguation method requires that the CValue is processed");
            System.exit(-1);
        }
        }else{
            pDoc.getDomainTopics().entrySet().stream().forEach((entry) -> {
                cGold.add(entry.getValue());
            });
        }
    }

    private void saveToTmp() {
        FileWriter outFile = null;
        PrintWriter out = null;
        try {
            outFile = new FileWriter(props.getProperty("tmpDir") + File.separator + "wikiCValueDisambiguation");
            out = new PrintWriter(outFile);
            boolean first = false;
            for (Topic top : topicList) {
                if (!first) {
                    out.printf("%s\t%d\t%s", top.getTopic(), top.getId(), top.getGoodSense());
                    first = true;
                } else {
                    out.printf("\n%s\t%d\t%s", top.getTopic(), top.getId(), top.getGoodSense());
                }
            }
        } catch (IOException ex) {
            logger.error(CValueWikiDisambiguator.class.getName(), "Error while saving disambiguation results", ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Return the number of required gold terms
     *
     * @return the goldTerms
     */
    public int getGoldTerms() {
        return goldTerms;
    }

    /**
     * Set the number of gold terms that will be extracted for disambiguation
     * purposes
     *
     * @param goldTerms the goldTerms to set
     */
    public void setGoldTerms(int goldTerms) {
        this.goldTerms = goldTerms;
    }

}
