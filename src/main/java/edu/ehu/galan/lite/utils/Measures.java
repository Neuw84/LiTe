package edu.ehu.galan.lite.utils;

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
import edu.ehu.galan.lite.model.Topic;
import edu.ehu.galan.lite.stemmers.english.PlingStemmerEn;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * Class that contains some measures for the term acquisition process contains methods that
 * accelerates the measuring but require more memory (inMemory methods) or takes the slow approach
 * requiring less memory, if you use inMemory methods remember to call deleteLabelMap when you are
 * done in order to free the ram space
 *
 * @author Angel Conde Manjon
 */
public class Measures {

    private static TObjectIntHashMap<String> hashMap = null;

    /**
     * Given a term list and a List of String representing the gold standard for validation
     * calculates the precision
     *
     * @param finalList
     * @param goldList
     * @return
     */
    public static float calculatePrecission(List<Topic> finalList, List<String> goldList) {

        float precission;
        int positives = 0;
        PlingStemmerEn pling = new PlingStemmerEn();

        int total = finalList.size();
        for (String gold : goldList) {

            for (Topic topic : finalList) {
                List<String> labelList = topic.getLabelList();
                boolean found = false;
                for (String string : labelList) {
                    if (pling.stem(string).equalsIgnoreCase(pling.stem(gold))) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    positives++;
                    break;
                }
            }
        }
        precission = (float) positives / total;
        return precission;

    }

    /**
     * Given a term list and a List of String representing the gold standard for validation
     * calculates the recall (coberture)
     *
     * @param finalList
     * @param goldList
     * @return
     */
    public static float calculateRecall(List<Topic> finalList, List<String> goldList) {

        float recall;
        int positives = 0;
        PlingStemmerEn pling = new PlingStemmerEn();

        int total = goldList.size();
        List<String> finals = new ArrayList<>();
        for (String gold : goldList) {

            for (Topic topic : finalList) {
                List<String> labelList = topic.getLabelList();
                boolean found = false;

                if (topic.getSourceTitle() != null) {
                } else {
                    //TODO: what to do with the Redirects
                }

                for (String string : labelList) {
                    if (pling.stem(string).equalsIgnoreCase(pling.stem(gold))) {
                        found = true;
                        finals.add(gold);
                        break;
                    }
                }
                if (found) {
                    positives++;
                    break;
                }
            }
        }
        System.out.println(positives);
        recall = (float) positives / total;
//        List<String> pollas = new ArrayList<>();
//        for (String string : goldList) {
//            pollas.add(new String(string));
//        }
//
//        pollas.retainAll(finals);
//        for (String string : goldList) {
//            if (!pollas.contains(string)) {
//                System.out.println(string);
//            }
//        }
        return recall;
    }

    /**
     ** Given the recall and the precision calculates the F1 score
     *
     * @param recall
     * @param precission
     * @return
     */
    public static float calculateF1Score(float recall, float precission) {
        float f1 = 2 * ((precission * recall) / (precission + recall));
        return f1;
    }

    private static void initializeHashMap(List<Topic> finalList) {
        LoggerFactory.getLogger(Measures.class).info("Generating label list, remember to call deleteLabelMap() after!! ");
        hashMap = new TObjectIntHashMap<>();
        PlingStemmerEn pling = new PlingStemmerEn();
        for (Topic topic : finalList) {
            List<String> labelList = topic.getLabelList();
            for (String string : labelList) {
                hashMap.put(pling.stem(string), topic.getId());

            }
            if (topic.getTopic() != null) {
                hashMap.put(pling.stem(topic.getTopic()), topic.getId());
            }
        }
    }

    public static void deletetLabelMap() {
        hashMap = null;
    }

    /**
     * Given a term list and a List of String representing the gold standard for validation
     * calculates the precision, uses a pre built hashmap of all the possible labels, may take some
     * ram space! (remember to call to deleteLabelMap when you are finished with measures if you use
     * this method)
     *
     * @param finalList
     * @param goldList
     * @return
     */
    public static float calculatePrecissionInMemory(List<Topic> finalList, List<String> goldList) {

        if (hashMap == null) {
            initializeHashMap(finalList);
        }
        float precission;
        int positives = 0;
        PlingStemmerEn pling = new PlingStemmerEn();

        int total = finalList.size();
        for (String gold : goldList) {
            if (hashMap.containsKey(pling.stem(gold))) {
                positives++;
            }
        }
        precission = (float) positives / total;
        return precission;

    }

    /**
     * Given a term list and a List of String representing the gold standard for validation
     * calculates the recall (coberture) uses a prebuilt hashmap of all the possible labels, may
     * take some ram space! (remember to call to deleteLabelMap when you are finished with measures
     * if you use this method)
     *
     * @param finalList
     * @param goldList
     * @return
     */
    public static float calculateRecallInMemory(List<Topic> finalList, List<String> goldList) {
        if (hashMap == null) {
            initializeHashMap(finalList);
        }

        float recall;
        int positives = 0;
        PlingStemmerEn pling = new PlingStemmerEn();
        
        int total = goldList.size();
        List<String> finals = new ArrayList<>();
        for (String gold : goldList) {
            if (hashMap.containsKey(pling.stem(gold))) {
                positives++;
                finals.add(gold);
            }
        }
        System.out.println(positives);
        recall = (float) positives / total;
        List<String> pollas = new ArrayList<>();
        for (String string : goldList) {
            pollas.add(new String(string));
        }

        pollas.retainAll(finals);
        for (String string : goldList) {
            if (pollas.contains(string)) {
                System.out.println(string);
            }
        }
        return recall;
    }
/**
     
     *
     * @param finalList
     * @param goldList
     * @return
     */
    public static List<Topic> returnGoldTopics(List<Topic> finalList, List<String> goldList) {
        if (hashMap == null) {
            initializeHashMap(finalList);
        }
        List<Topic> topics=new ArrayList<>();
        float recall;
        int positives = 0;
        PlingStemmerEn pling = new PlingStemmerEn();
        HashMap<Integer,Topic> hash=new HashMap<>(finalList.size());        
         for (Topic topic : finalList) {
            List<String> labelList = topic.getLabelList();
            for (String string : labelList) {
                hash.put(topic.getId(), topic);
            }
            if (topic.getTopic() != null) {
                hash.put(topic.getId(),topic);
            }
        }
        int total = goldList.size();
        List<String> finals = new ArrayList<>();
        for (String gold : goldList) {
            if (hashMap.containsKey(pling.stem(gold))) {
                positives++;
                if(hash.containsKey(hashMap.get(pling.stem(gold)))){
                       topics.add(hash.get(hashMap.get(pling.stem(gold))));
                }else{
                    System.out.println(pling.stem(gold));
                }
                
            }
        }
        System.out.println(positives);     
        return topics;
    }
}
