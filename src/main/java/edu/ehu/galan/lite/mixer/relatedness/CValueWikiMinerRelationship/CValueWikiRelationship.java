package edu.ehu.galan.lite.mixer.relatedness.CValueWikiMinerRelationship;

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
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Topic;
import edu.ehu.galan.lite.mixer.relatedness.AbstractRelatedness;
import edu.ehu.galan.lite.utils.wikiminer.WikiminnerHelper;
import edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate.Comparison;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that relates two topics using Wikiminer and the desired thresholds, uses the The topics
 * that have greater score from CValue and that have only possible sense in Wikipedia
 *
 * This class delete the topics below the configured thresholds in
 * /resources/lite/configs/general.conf
 *
 * goldTerms == > number of topics for comparison (highest CValue ones with only one mapping to
 * Wikipedia) relatedness ==> the minimun relatedness of each topic with the gold ones to be taken
 * into account minRelationship ==> the number of topics that need to be above the relatedness
 * threshold (to not to be deleted)
 *
 *
 * @author Angel Conde Manjon
 */
public class CValueWikiRelationship extends AbstractRelatedness {

    private List<Integer> cGold;
    private transient int goldTerms = 8;
    private int minRelationship = 1;
    private final transient Properties props;
    private float relatedness = 0.40f;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private transient final WikiminnerHelper helper;

    /**
     *
     * @param pPropsDir
     * @param pHelper
     */
    public CValueWikiRelationship(String pPropsDir, WikiminnerHelper pHelper) {
        super("CValueWikiRelationship", pPropsDir);
        cGold = new ArrayList<>();
        props = new Properties();
        helper=pHelper;
        try {
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            System.out.println("general.conf not found");
            logger.error("general.conf not found", ex);
        } catch (IOException ex) {
            logger.error("general.conf not found or not readable?", ex);
        }
        minRelationship = Integer.parseInt(props.getProperty("minRelationship"));
        relatedness = Float.parseFloat(props.getProperty("relatedness"));
        goldTerms = Integer.parseInt(props.getProperty("goldTerms"));

    }

    @Override
    public void relate(Document pDoc) {
//        setRelatedness(0f);
        //TODO: use a cache for avoiding the search within all topics!
        //for (Document doc : pCorpus.getDocQueue()) {
        topicList = pDoc.getTopicList();
        Collections.sort(topicList, (o1, o2) -> o1.getId() > o2.getId() ? -1 : o1.getId() == o2.getId() ? 0 : 1);
//        System.out.println(topicList.size());
//        System.out.println(topicList.stream().distinct().count());
        if (pDoc.getDomainTopics().isEmpty()) {
            obtainGoldTerms(pDoc);
        } else {
            cGold = pDoc.getDomainTopics().keySet().stream().collect(Collectors.toList());
        }
        logger.info("The obtained gold terms are: " + cGold.toString());
        List<Comparison> comparisons = helper.parallelRelate(pDoc.getTopicList(), cGold, getRelatedness(), getMinRelationship());
        List<Topic> goodList = new ArrayList<>();
        int prev = -1;
        int aux = 0;
        Topic rux = null;
        if (comparisons != null) {
            List<Double> list;
            list = new ArrayList<>();
            for (int i = 0; i < comparisons.size();) {
                Comparison comparison = comparisons.get(i);

                if (comparison != null) {
//            System.out.println(comparison.getLowId());
//            System.out.println(comparison.getHighId());
                    if (prev == -1) {
                        prev = comparison.getLowId();
                    }

                    for (Topic top : topicList) {
                        if (top.getId() == prev) {
                            if (top.getId() == comparison.getLowId()) {
                                if (comparison.getRelatedness() >= getRelatedness()) {
                                    aux++;
                                    list.add(comparison.getRelatedness());
                                    rux = top;
                                }
                                break;
                            }
                        }
                    }
                    if (prev != comparison.getLowId()) {
                        prev = comparison.getLowId();
                        if (aux >= getMinRelationship()) {
                            goodList.add(rux);
                            rux.setDomainRelatedness((float) list.stream().mapToDouble(a -> a).average().orElse(0));
                        }
                        aux = 0;
                        list.clear();
                    }else{
                        i++;
                        
                    }}
                }
                //saveToTmp();
                //}

                pDoc.setTopicList(goodList);

            }
            List<Topic> golds = topicList.parallelStream().filter(t -> cGold.contains(t.getId())).collect(Collectors.toList());
            golds.stream().forEach((Topic t) -> t.setDomainRelatedness(1.0f));
            goodList.addAll(golds);
            pDoc.setTopicList(goodList.stream().distinct().collect(Collectors.toList()));
            //saveToTmp();
            //}

        }

    

    

    private void obtainGoldTerms(Document pDoc) {
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

            WikiminnerHelper wiki = helper;
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
                if (top != null) {
//                System.out.println(rex[0]);
                    if (top.getSenseList().size() == 1) {
                        cGold.add(top.getId());
                        numTerms++;
                        if (numTerms == goldTerms) {
                            break;
                        }
                    }
                }
            }
        } else {
            logger.error("This disambiguation method requires that the CValue is processed");
            System.exit(-1);

        }
    }

    @Override
    public void relateNotDelete(Document pDoc) {
        //TODO: use a cache for avoiding the search within all topics!
        //for (Document doc : pCorpus.getDocQueue()) {
        topicList = pDoc.getTopicList();
        Collections.sort(topicList, (o1, o2) -> o1.getId() > o2.getId() ? -1 : o1.getId() == o2.getId() ? 0 : 1);
        obtainGoldTerms(pDoc);
        logger.info("The obtained gold terms are: " + cGold.toString());
        List<Comparison> comparisons = helper.parallelRelate(pDoc.getTopicList(), cGold, getRelatedness(), getMinRelationship());
        List<Topic> goodList = new ArrayList<>();
        int prev = -1;
        int aux = 0;
        Topic rux = null;
        if (comparisons != null) {
            List<Double> list;
            
            for (int i = 0; i < comparisons.size();) {
                
            Comparison comparison= comparisons.get(i);
            
                if (comparison != null) {
//            System.out.println(comparison.getLowId());
//            System.out.println(comparison.getHighId());
                    if (prev == -1) {
                        prev = comparison.getLowId();
                    }
                    list = new ArrayList<>();
                    for (Topic top : topicList) {
                        if (top.getId() == prev) {
                            if (top.getId() == comparison.getLowId()) {
                                list.add(comparison.getRelatedness());
                                rux = top;
                                break;
                            }
                        }
                    }
                    if (prev != comparison.getLowId()) {
                        prev = comparison.getLowId();
                        //we filter here the ones that are 0 and get an average of the rest
                        rux.setDomainRelatedness((float) list.stream().filter(a -> a > 0).mapToDouble(a -> a).average().orElse(0));
                        goodList.add(rux);

                    }else{
                    i++; 
                    }
                }
//                pDoc.setTopicList(goodList);
                //saveToTmp();
                //}
            }
        }
        List<Topic> golds = topicList.parallelStream().filter(t -> cGold.contains((t.getId()))).collect(Collectors.toList());
        //the gold topics have 1.0f relatedness
        golds.forEach(t -> t.setDomainRelatedness(1.0f));
        goodList.addAll(golds);
        HashMap<Integer,Topic> cach=new HashMap<>();
        for (Topic gold : golds) {
            cach.put(gold.getId(), gold);
        }
        for (Topic gold : golds) {
            HashMap<Integer,Double> linksIn=gold.getLinksIn();
            HashMap<Integer,Double> linksOut=gold.getLinksOut();
            if(!linksIn.containsKey(gold.getId())){
                linksIn.remove(gold.getId());
            }
            if(!linksOut.containsKey(gold.getId())){
                linksOut.remove(gold.getId());
            }
        }
//        goodList.parallelStream().filter(t -> t.getDomainRelatedness() > 0f);
        pDoc.setTopicList(goodList.parallelStream().filter(t -> t.getDomainRelatedness() > 0f).collect(Collectors.toList()));
        //saveToTmp();
        //}
    }

    /**
     * @return the relatedness
     */
    public float getRelatedness() {
        return relatedness;
    }

    /**
     * @param relatedness the relatedness to set
     */
    public void setRelatedness(float relatedness) {
        this.relatedness = relatedness;
    }

    /**
     * @return the minRelationship
     */
    public int getMinRelationship() {
        return minRelationship;
    }

    /**
     * @param minRelationship the minRelationship to set
     */
    public void setMinRelationship(int minRelationship) {
        this.minRelationship = minRelationship;
    }
}
