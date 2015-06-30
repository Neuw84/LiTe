package edu.ehu.galan.lite.model;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a term mapped to a knowledge based source, could contain more than one
 * sense.
 *
 * @author Angel Conde Manjon
 */
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;
    private String topic;
    private int id;
    private final List<String> labels;
    private final List<String> senses;
    private final List<Integer> sensesId;
    private transient int[] sensCount;
    private transient int[] sensCountId;
    private String sourceTitle;
    private final HashMap<String, String> translations;
    private final HashMap<Integer, String> parentCategories;
    private String sourceDef;
    private final transient List<String> images;
    private transient boolean disambiguationFail;
    private boolean isIndividual;
    private transient HashMap<Integer, List<Double>> sensConf;
    private float domainRelatedness = 0f;
    private final HashMap<Integer, Double> linksIn;
    private final HashMap<Integer, Double> linksOut;

    /**
     *
     * @param pTopic -The String extracted by the term extraction algorithm, (the string that was
     * found in the corpus)
     */
    public Topic(String pTopic) {
        topic = pTopic;
        id = -1;
        labels = new ArrayList<>();
        senses = new ArrayList<>();
        translations = new HashMap<>();
        images = new ArrayList<>();
        parentCategories = new HashMap<>();
        disambiguationFail = false;
        sensesId = new ArrayList<>();
        linksIn = new HashMap<>();
        linksOut = new HashMap<>();
    }

    /**
     *
     * @param pTopic - The String extracted by the term extraction algorithm, (the string that was
     * found in the corpus)
     * @param pId - The id in the knowledge base
     */
    public Topic(String pTopic, int pId) {
        labels = new ArrayList<>();
        id = pId;
        senses = new ArrayList<>();
        translations = new HashMap<>();
        images = new ArrayList<>();
        parentCategories = new HashMap<>();
        disambiguationFail = false;
        sensesId = new ArrayList<>();
        linksIn = new HashMap<>();
        linksOut = new HashMap<>();
    }

    /**
     *
     */
    public Topic() {
        labels = new ArrayList<>();
        id = -1;
        senses = new ArrayList<>();
        translations = new HashMap<>();
        images = new ArrayList<>();
        parentCategories = new HashMap<>();
        disambiguationFail = false;
        sensesId = new ArrayList<>();
        linksIn = new HashMap<>();
        linksOut = new HashMap<>();
    }

    /**
     *
     * @return the topic - returns a string containing the exact string with this topic was
     * extracted from the corpus
     */
    public String getTopic() {
        return topic;
    }

    /**
     * @param topic -The String extracted by the term extraction algorithm, (the string that was
     * found in the corpus)
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Return the id in the knowledge source
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * the id or index number in the knowledge source
     *
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("%s\t%d\t%s", topic, id, senses);
    }

    /**
     *
     * @param label - another way of represent this topic
     */
    public void addLabel(String label) {
        labels.add(label);
    }

    /**
     *
     * @param pSense - a possible sense for this term
     */
    public void addSense(String pSense) {
        senses.add(pSense);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Topic) {
            if (id != -1) {
                return this.getId() == ((Topic) o).getId();
            } else {
                return this.getSourceTitle().equals(((Topic) o).getSourceTitle());
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.id;
        return hash;
    }

    /**
     * Returns the possible forms of refering to this topic
     *
     * @return
     */
    public List<String> getLabelList() {
        return labels;
    }

    /**
     * Get the possible senses of this topic
     *
     * @return
     */
    public List<String> getSenseList() {
        return senses;

    }

    /**
     * Returns the title of the topic in the knowledge source
     *
     * @return the sourceTitle
     */
    public String getSourceTitle() {
        return sourceTitle;
    }

    /**
     * The title of the concept in the knowledge base
     *
     * @param pSourceTitle the sourceTitle to set
     */
    public void setSourceTitle(String pSourceTitle) {
        this.sourceTitle = pSourceTitle;
    }

    /**
     * Add a translation to this concept, using a language code, (see wikipedia for language
     * codes)for example. 'es' = spanish
     *
     * @param language
     * @param translation
     */
    public void addTranslation(String language, String translation) {
        getTranslations().put(language, translation);
    }

    /**
     *
     * @param language - the language code (for example 'en')
     * @return
     */
    public String getTranslation(String language) {
        return getTranslations().get(language);
    }

    /**
     * Initializes the sense counts for disambiguation purposes given the number of possible senses
     */
    public void initializeSenseCount() {
        sensCount = new int[senses.size()];
        sensCountId = new int[senses.size()];
        sensConf = new HashMap<>();
    }

    /**
     * The Sense with more votes is returned
     *
     * @return
     */
    public String getGoodSense() {
        //TODO: if the topic has no sense for the domain (like produced) need to change this
        int aux = 0;
        int max = 0;
        if (sensCount.length == 1) {
            return senses.get(0);
        }
        for (int i = 0; i < sensCount.length; i++) {
            int j = sensCount[i];
            if (j > max) {
                max = j;
                aux = i;
            }
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < sensCount.length; i++) {
            if (sensCount[aux] == sensCount[i]) {
                list.add(i);
            }
        }
        if (list.size() > 1) {
            double moz = 0.0;
            Integer ky = 0;
            for (Map.Entry<Integer, List<Double>> entry : sensConf.entrySet()) {
                Integer key = entry.getKey();
                List<Double> value = entry.getValue();
                double average = value.stream().mapToDouble(v -> (v)).average().getAsDouble();
                if (average > moz) {
                    moz = average;
                    ky = key;
                }
            }
            int xos = 0;
            for (int i = 0; i < sensesId.size(); i++) {
                int j = sensesId.get(i);
                if (ky == j) {
                    xos = i;
                    break;
                }

            }
            id = ky;
            return senses.get(xos);

        } else {
            //            aux = 0;
//            max = 0;
//            double aux2=0;
//            for (i= 0; i< list.size();i++) {
//                if(aux2<sensConf[list.get(i).intValue()]){
//                   max= list.get(i).intValue();
//                   aux2=sensConf[list.get(i).intValue()];
//                }
        }

        id = sensCountId[aux];
        return senses.get(aux);
    }

    /**
     * The disambiguation process failed in this topic, aka 'should be deleted' this is only used by
     * cvalueWikiminerDisambiguation method
     *
     * @return the disambiguationFail
     */
    public boolean isDisambiguationFail() {
        return disambiguationFail;
    }

    /**
     * If the disambiguation method has failed on this string (only used by
     * cvalueWikiminerDisambiguation)
     *
     * @param disambiguationFail the disambiguationFail to set
     */
    public void setDisambiguationFail(boolean disambiguationFail) {
        this.disambiguationFail = disambiguationFail;
    }

    /**
     * Add one vote to the input sense
     *
     * @param pSense - the string representing the sense(should be the knoledge base title of this
     * title)
     * @param id - the knowledge source id of this sense.
     * @param confidence
     */
    public void addProbableSense(String pSense, int id, double confidence) {
        pSense = pSense.trim();
        boolean exists = false;
        for (int i = 0; i < senses.size(); i++) {
            if (pSense.equalsIgnoreCase(senses.get(i))) {
                sensCount[i] += 1;
                sensCountId[i] = id;
                if (sensConf.containsKey(id)) {
                    List<Double> count = sensConf.get(id);
                    count.add(confidence);
                } else {

                    List<Double> count = new ArrayList<>();
                    count.add(confidence);
                    sensConf.put(id, count);
                }
                exists = true;
                break;
            }
        }

    }

    /**
     * Return if this topic is an individual, (not a category)
     *
     * @return the isIndividual
     */
    public boolean isIsIndividual() {
        return isIndividual;
    }

    /**
     * Sets whether this topic is an individual (if it's a category or not)
     *
     * @param isIndividual the isIndividual to set
     */
    public void setIsIndividual(boolean isIndividual) {
        this.isIndividual = isIndividual;
    }

    /**
     * Returns the definition extracted from the source
     *
     * @return the source definition
     */
    public String getSourceDef() {
        return sourceDef;
    }

    /**
     * Sets the definition extracted from the knowledge source
     *
     * @param pDef - the definition to set
     */
    public void setSourceDef(String pDef) {
        sourceDef = pDef;
    }

    /**
     * Return a hash maps of the parent categories of this topic represented in a HashMap with
     * Source identifier,SourceTitle pairs
     *
     * @return
     */
    public HashMap<Integer, String> getParentCategories() {
        return parentCategories;
    }

    /**
     * Adds a Parent Category
     *
     * @param pId
     * @param pTitle
     */
    public void addParentCagegory(Integer pId, String pTitle) {
        parentCategories.put(pId, pTitle);
    }

    /**
     * @return the translations
     */
    public HashMap<String, String> getTranslations() {
        return translations;
    }

    /**
     * Adds a id to the current added sense in the senseList The order must be the same as isn't
     * controlled
     *
     * @param pSenseId
     */
    public void addSenseId(int pSenseId) {

        sensesId.add(pSenseId);
    }

    /**
     * Returns the given id for the sense, see the SenseList for obtaining the current position for
     * the desired sense
     *
     * @param pNum
     * @return
     */
    public int getSenseId(int pNum) {
        return sensesId.get(pNum);
    }

    /**
     * @return the domainRelatedness
     */
    public float getDomainRelatedness() {
        return domainRelatedness;
    }

    /**
     * @param domainRelatedness the domainRelatedness to set
     */
    public void setDomainRelatedness(float domainRelatedness) {
        this.domainRelatedness = domainRelatedness;
    }

    /**
     *
     * @param id
     * @param relatedness
     */
    public void addLinkIn(int id, double relatedness) {
        getLinksIn().put(id, relatedness);
    }

    /**
     *
     * @param id
     * @param relatedness
     */
    public void addLinkOut(int id, double relatedness) {
        getLinksOut().put(id, relatedness);
    }

    /**
     * @return the linksIn
     */
    public HashMap<Integer, Double> getLinksIn() {
        return linksIn;
    }

    /**
     * @return the linksOut
     */
    public HashMap<Integer, Double> getLinksOut() {
        return linksOut;
    }
}
