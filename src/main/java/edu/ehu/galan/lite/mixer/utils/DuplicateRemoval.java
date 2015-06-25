package edu.ehu.galan.lite.mixer.utils;

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
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.ListTerm;
import edu.ehu.galan.lite.model.Topic;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that processes all the different duplicate removal steps!
 *
 * @author Angel Conde Manjon
 */
public class DuplicateRemoval {

    /**
     * First Duplicate removal Step, here a comparison using equalsIgnoreCase
     * from String class is done
     *
     * @param pTermList - a list of list containing terms extracted by the term
     * extraction methods
     *
     * @return - the term list without the duplicates
     */
    public static List<Term> simpleDuplicateRemoval(List<ListTerm> pTermList) {
//        Logger logger=LoggerFactory.getLogger(DuplicateRemoval.class);
//        for (ListTerm pTermList1 : pTermList) {
//            System.out.println(pTermList1.getName()+" "+pTermList1.getTermList().size());
//            pTermList1.getTermList().stream().forEach(System.out::println);
//        }
        List<Term> goodTermList = pTermList.stream().flatMap(listerm -> listerm.getTermList().stream()).distinct().sorted((t1, t2) -> t1.getTerm().compareTo(t2.getTerm())).collect(Collectors.toList());
//        for (Term term : goodTermList) {
//            System.out.println(term);
//
//       }

////        System.out.println(goodTermList.size());
// java < 8 code
//        for (ListTerm list : pTermList) {
//            logger.info(list.getName()+"\t"+list.getTermList().size());
//            List<Term> terms = list.getTermList();
//            for (Term object : terms) {
////                if( object.getTerm().equalsIgnoreCase("Orion Nebula")){
////                    System.out.println("dsasdasdasd");
////                }
//                if (!goodTermList.contains(object)) {
//                    goodTermList.add(object);
//                }
//            }
//        }

        return goodTermList;
    }

    /**
     * If two topics are mapped finally to the same id in the knowledge source,
     * they are deleted
     *
     * @param pDoc
     */
    public static void topicDuplicateRemoval(Document pDoc) {
        //   for (Document doc : pCorpus.getDocQueue()) {
        List<Topic> topicList = pDoc.getTopicList();
        //TODO: this step could be combined with disambiguationRemoval? (check topic.getGoodSense)
        List<Topic> goodTopicList = topicList.stream().filter(topic -> topic.getId() != -1).distinct().collect(Collectors.toList());
// Java < 8 code
//        for (Topic topic : topicList) {
//            if (topic.getId() != -1) {
//                if (!goodTopicList.contains(topic)) {
//                    goodTopicList.add(topic);
//                }
//            } else {
//                goodTopicList.add(topic);
//            }
//        }
        pDoc.setTopicList(goodTopicList);
        //     }
    }

// Only related to the Wikipedia senses, not neede because later we will
// check if two topics are the same based on its id to the knowledge base
//    public static void wikiTopicDuplicateRemoval(Corpus pCorpus) {
//        List<Topic> topicList = pCorpus.getTopicList();
//        List<Topic> goodList = new ArrayList<>();
//        boolean aux;
//        for (Topic topic : topicList) {
//            aux = false;
//            List<String> list = topic.getSenseList();
//            for (Topic string : goodList) {
//                if (string.getSenseList().containsAll(list)) {
//                    aux = true;
//                    break;
//                }
//            }
//            if (!aux) {
//                goodList.add(topic);
//            }
////            pCorpus.setTopicList(topicList);
////        }
//
//
//
//        pCorpus.setTopicList(goodList);
//    }
    /**
     * If a term is not disambiguated correctly, aka in the disambiguation
     * process it has not be mapped to an id within a knowledge source it is
     * deleted
     *
     * @param pDoc - the document we want to process
     */
    public static void disambiguationRemoval(Document pDoc) {
        List<Topic> topicList = pDoc.getTopicList();
        List<Topic> goodList = topicList.parallelStream().filter(topic -> !topic.isDisambiguationFail()).collect(Collectors.toList());
        pDoc.setTopicList(goodList);
    }
}
