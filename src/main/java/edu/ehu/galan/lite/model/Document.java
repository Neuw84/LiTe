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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ehu.galan.lite.mixer.utils.DuplicateRemoval;
import edu.ehu.galan.lite.utils.TermListUtils;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A document represents the piece of a corpus containing text.
 *
 * @author Angel Conde Manjon
 */
public class Document {

    /**
     * @return the source
     */
    public SourceType getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(SourceType source) {
        this.source = source;
    }
    
    public enum SourceType {
        
        wikipedia, wordnet;
    }
    
    private transient String path;
    private transient List<String> sentenceList;
    private transient List<LinkedList<Token>> tokenList;
    private String name;
    private transient List<ListTerm> termList;
    private List<Topic> topicList;
    private transient List<Term> mixedTermList;
    private transient static final Logger logger = LoggerFactory.getLogger(Document.class);
    private HashMap<Integer, String> domainTopics;
    private transient SourceType source;

    /**
     * Default constructor, by default its assumed that the document will be mapped to Wikipedia
     *
     * @param pPath
     * @param pName
     */
    public Document(String pPath, String pName) {
        path = pPath;
        name = pName;
        topicList = new ArrayList<>();
        domainTopics = new HashMap<>();
        source = SourceType.wikipedia;
    }
    
    public Document(String pPath, String pName, SourceType pSourceType) {
        path = pPath;
        name = pName;
        termList = new ArrayList<>();
        topicList = new ArrayList<>();
        domainTopics = new HashMap<>();
        source = pSourceType;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the sentenceList
     */
    public List<String> getSentenceList() {
        return sentenceList;
    }

    /**
     * @param sentenceList the sentenceList to set
     */
    public void setSentenceList(List<String> sentenceList) {
        this.sentenceList = sentenceList;
    }

    /**
     * @return the tokenList
     */
    public List<LinkedList<Token>> getTokenList() {
        return tokenList;
    }

    /**
     * @param tokenList the tokenList to set
     */
    public void setTokenList(List<LinkedList<Token>> tokenList) {
        this.tokenList = tokenList;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public List<ListTerm> getTermList() {
        return termList;
    }

    /**
     * Sets the topic list of this documetn
     *
     * @param pTopicList
     */
    public void setTopicList(List<Topic> pTopicList) {
        topicList = pTopicList;
    }

    /**
     * Returns the associated Topic List with this document
     *
     * @return
     */
    public List<Topic> getTopicList() {
        return topicList;
    }

    /**
     * This method its syncronized over the list of term list, as the different algorithms could try
     * to add a term list at the same time
     *
     * @param pList
     */
    public void addListTerm(ListTerm pList) {
        synchronized (termList) {
            termList.add(pList);
        }
        
    }

    /**
     * @return the mixedTermList
     */
    public List<Term> getMixedTermList() {
        return mixedTermList;
    }

    /**
     * @param mixedTermList the mixedTermList to set
     */
    public void setMixedTermList(List<Term> mixedTermList) {
        this.mixedTermList = mixedTermList;
    }

    /**
     * Apply a stopwordlist to all the candidate list terms obtained from this document
     *
     * @param standardStop
     */
    public void applyGlobalStopWordList(List<String> standardStop) {
        getTermList().parallelStream().forEach((listTerm) -> {
            TermListUtils.applyStopwordList(listTerm.getTermList(), standardStop);
        });
    }

    /**
     * Given a threshold and a List of algorithm names, it applys a threshold to each algorithm term
     * candidate list
     *
     * @param f
     * @param pAlgsNames
     */
    public void mapThreshold(float f, String... pAlgsNames) {
        List<String> names = Arrays.asList(pAlgsNames);
        List<ListTerm> thesholdList=new ArrayList<>();
        boolean aux=false;
        if (names.size() > 0) {
                for (ListTerm termL : termList) {
                    aux=false;
                    for (String name1 : names) {
                        if(termL.getClass().equals(name1)){
                            aux=true;
                            List<Term> terms=TermListUtils.getThresholdedTermList(termL.getTermList(), f);
                            thesholdList.add(new ListTerm(name1, terms));
                        }
                    }
                    if(!aux){
                      thesholdList.add(termL);
                    }
                }                        
            termList=thesholdList;
        }
    }

    /**
     *
     */
    public void removeAndMixTerms() {
        setMixedTermList(DuplicateRemoval.simpleDuplicateRemoval(getTermList()));
    }

    /**
     * Save results in a plain text file following the pattern.... topic + \tab +
     * Source_knowledge_id+ \tab + Source_knowledge_title
     *
     * @param pFolder - where you want to save the results
     * @param pDoc
     */
    public static void saveResults(String pFolder, Document pDoc) {
        if (Files.isDirectory(Paths.get(pFolder), LinkOption.NOFOLLOW_LINKS)) {
            PrintWriter printWriter = null;
            try {
                printWriter = new PrintWriter(new File(pFolder + "/" + pDoc.name), "UTF-8");
                
                for (Topic topic : pDoc.topicList) {
                    printWriter.println(topic.getTopic() + "\t" + topic.getId() + "\t" + topic.getSourceTitle());
                }
                printWriter.close();
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                logger.error("error printing the file: " + pFolder, ex);
            }
            logger.info(pDoc.path + "  Saved... ");
        } else if (Files.exists(Paths.get(pFolder), LinkOption.NOFOLLOW_LINKS)) {
            logger.error("The folder exists but it isn't a directory...maybe a file?");
        } else {
            logger.warn("The directory doesn't exist... will be created");
            try {
                FileUtils.forceMkdir(new File(pFolder));
                PrintWriter printWriter = null;
                printWriter = new PrintWriter(new File(pFolder + "/" + pDoc.name), "UTF-8");
                for (Topic topic : pDoc.topicList) {
                    printWriter.println(topic.getTopic() + "\t" + topic.getId() + "\t" + topic.getSourceTitle());
                }
                printWriter.close();
                logger.info(pDoc.path + "  Saved... ");
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                logger.error("error printing the file: " + pFolder, ex);
            } catch (IOException ex) {
                logger.error("Error while creating directories in or printing the file: " + pFolder, ex);
            }
        }
        
    }

    /**
     * Save the results of the document in a Json text file, if you want to use current directory
     * use "./" or the absolute path, the method will check whether the Dir exists or not and will
     * try to create according to that
     *
     * @param pFolder - where you want to save the results
     * @param pDoc
     */
    public static void saveJsonToDir(String pFolder, Document pDoc) {
        Doc msg = new Doc();
        msg.setName(pDoc.name);
        msg.setKnowledgeSource(pDoc.getSource().toString());
        List<MsgTop> list = new ArrayList<>();
        for (Topic string : pDoc.topicList) {
            MsgTop top = new MsgTop();
            top.setSourceTitle(string.getSourceTitle());
            top.setDefinition(string.getSourceDef());
            top.setId(string.getId());
            top.setTopic(string.getTopic());
            top.setLabels(string.getLabelList());
            top.setDomainRelatedness(string.getDomainRelatedness());
            
            List<Translation> transList = new ArrayList<>();
            HashMap<String, String> map = string.getTranslations();
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = it.next();
                Translation trans = new Translation();
                trans.setLang(pairs.getKey());
                trans.setText(pairs.getValue());
                transList.add(trans);
            }
            List<Link> linksIn=new ArrayList<>();
            HashMap<Integer,Double> map3 = string.getLinksIn();
            Iterator<Map.Entry<Integer, Double>> it2 = map3.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<Integer,Double> pairs = it2.next();
                Link link=new Link();
                link.setId(pairs.getKey());
                link.setRelatedness(pairs.getValue());
                linksIn.add(link);
            }
            List<Link> linksOut=new ArrayList<>();
            HashMap<Integer,Double> map4 = string.getLinksOut();
            Iterator<Map.Entry<Integer, Double>> it3 = map4.entrySet().iterator();
            while (it3.hasNext()) {
                Map.Entry<Integer,Double> pairs = it3.next();
                Link link=new Link();
                link.setId(pairs.getKey());
                link.setRelatedness(pairs.getValue());
                linksOut.add(link);
            }
            top.setLinksIn(linksIn);
            top.setLinksOut(linksOut);
            top.setTranslations(transList);
            List<Parent> parentList = new ArrayList<>();
            HashMap<Integer, String> map2 = string.getParentCategories();
            Iterator<Map.Entry<Integer, String>> itr = map2.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<Integer, String> pairs = itr.next();
                Parent trans = new Parent();
                trans.setId(pairs.getKey());
                trans.setSourceTitle(pairs.getValue());
                parentList.add(trans);
            }
            top.setParentCategories(parentList);
            list.add(top);
        }
        msg.setTopics(list);
        List<DomainRel> listRel = new ArrayList<>();
        for (Map.Entry<Integer, String> pair : pDoc.domainTopics.entrySet()) {
            DomainRel rel = new DomainRel();
            rel.setId(pair.getKey());
            rel.setTitle(pair.getValue());
            listRel.add(rel);
        }
        msg.setDomainTopics(listRel);
        if (Files.isDirectory(Paths.get(pFolder), LinkOption.NOFOLLOW_LINKS)) {
            FileWriter outFile = null;
            try {
                FileUtils.deleteQuietly(new File(pFolder + "/" + pDoc.getName() + ".json"));
                outFile = new FileWriter(pFolder + "/" + pDoc.getName() + ".json");
                boolean first = true;
                try (PrintWriter out = new PrintWriter(outFile)) {
                    Gson son = new GsonBuilder().setPrettyPrinting().create();
                    out.print(son.toJson(msg));
                }
            } catch (IOException ex) {
                logger.warn("couldn't save the document results in json format", ex);
            } finally {
                try {
                    if (outFile != null) {
                        outFile.close();
                    }
                } catch (IOException ex) {
                    logger.warn("Error while closing the file", ex);
                }
            }
            logger.info(pDoc.path + "  Saved... ");
        } else if (Files.exists(Paths.get(pFolder), LinkOption.NOFOLLOW_LINKS)) {
            logger.error("The folder exists but it isn't a directory...maybe a file?");
        } else {
            logger.warn("The directory doesn't exist... will be created");
            FileWriter outFile = null;
            try {
                FileUtils.forceMkdir(new File(pFolder));
                FileUtils.deleteQuietly(new File(pFolder + "/" + pDoc.getName() + ".json"));
                outFile = new FileWriter(pFolder + "/" + pDoc.getName() + ".json");
                boolean first = true;
                try (PrintWriter out = new PrintWriter(outFile)) {
                    Gson son = new GsonBuilder().setPrettyPrinting().create();
                    out.print(son.toJson(pDoc));
                }
            } catch (IOException ex) {
                logger.warn("couldn't save the document results in json format", ex);
            } finally {
                try {
                    if (outFile != null) {
                        outFile.close();
                    }
                } catch (IOException ex) {
                    logger.warn("Error while closing the file", ex);
                }
            }
            logger.info(pDoc.path + "  Saved... ");
            
        }
        
    }

    /**
     * Return a Json string with the results of the document using GSon library
     *
     * @param pDoc
     * @return String - the String containing the Json
     */
    public static String getJsonResults(Document pDoc) {
        Doc msg = new Doc();
        msg.setName(pDoc.name);
        msg.setKnowledgeSource(pDoc.getSource().toString());
        List<MsgTop> list = new ArrayList<>();
        for (Topic string : pDoc.topicList) {
            MsgTop top = new MsgTop();
            top.setSourceTitle(string.getSourceTitle());
            top.setDefinition(string.getSourceDef());
            top.setId(string.getId());
            top.setTopic(string.getTopic());
            top.setLabels(string.getLabelList());
            top.setDomainRelatedness(string.getDomainRelatedness());
            List<Translation> transList = new ArrayList<>();
            HashMap<String, String> map = string.getTranslations();
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = it.next();
                Translation trans = new Translation();
                trans.setLang(pairs.getKey());
                trans.setText(pairs.getValue());
                transList.add(trans);
            }
            top.setTranslations(transList);
            List<Parent> parentList = new ArrayList<>();
            HashMap<Integer, String> map2 = string.getParentCategories();
            Iterator<Map.Entry<Integer, String>> itr = map2.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<Integer, String> pairs = itr.next();
                Parent trans = new Parent();
                trans.setId(pairs.getKey());
                trans.setSourceTitle(pairs.getValue());
                parentList.add(trans);
            }
            top.setParentCategories(parentList);
            list.add(top);
        }
        msg.setTopics(list);
        msg.setTopics(list);
        List<DomainRel> listRel = new ArrayList<>();
        for (Map.Entry<Integer, String> pair : pDoc.domainTopics.entrySet()) {
            DomainRel rel = new DomainRel();
            rel.setId(pair.getKey());
            rel.setTitle(pair.getValue());
            listRel.add(rel);
        }
        msg.setDomainTopics(listRel);
        Gson son = new GsonBuilder().setPrettyPrinting().create();
        return (son.toJson(msg));
        
    }

    /**
     * Adds a Topic that is important in this Document (Domain representative topic), will extract
     * from it the id and the source title
     *
     * @param pTop
     */
    public void addDomainTopic(Topic pTop) {
        domainTopics.put(pTop.getId(), pTop.getSenseList().get(0));
    }

    /**
     * Tries to convert the content of this document to UTF-8 using java CharsetDecoders
     */
    public void convertToUTF8() {
        FileInputStream istream = null;
        Writer out = null;
        try {
            istream = new FileInputStream(path);
            BufferedInputStream in = new BufferedInputStream(istream);
            CharsetDecoder charsetDecoder = Charset.forName("UTF-8").newDecoder();
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
            charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            Reader inputReader = new InputStreamReader(in, charsetDecoder);
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputReader, writer);
            String theString = writer.toString();
            FileUtils.deleteQuietly(new File(path));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
            out.write(theString);
            out.close();
//            System.out.println("");
        } catch (FileNotFoundException ex) {
            logger.error("Error converting the file to utf8", ex);
        } catch (IOException ex) {
            logger.error("Error converting the file to utf8", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (istream != null) {
                    istream.close();
                }
            } catch (IOException ex) {
                logger.error("Error converting the file to utf8", ex);
            }
        }
        
    }

    /**
     * Saves the Document text in Json Format, specifically it saves the sentence list splitted by
     * the pos tager ("My cat is brown ." look at the punctuation) , and the tokenized (POS TAGS)
     * sentence list
     *
     * @param pDoc
     * @return
     */
    public static String getDocumentJson(Document pDoc) {
        Gson son = new GsonBuilder().setPrettyPrinting().create();
        LiteDoc msg = new LiteDoc(pDoc.getName(), pDoc.getSentenceList(), pDoc.getTokenList());
        return son.toJson(msg);
    }

    /**
     *
     * @return
     */
    public HashMap<Integer, String> getDomainTopics() {
        return domainTopics;
    }
    
    public void setDomainTopics(HashMap<Integer,String> pDomain){
       domainTopics=pDomain;
    }
    
    static class Doc {
        
        private String knowledgeSource;
        
        private String name;
        private List<DomainRel> domainTopics;
        
        private List<MsgTop> topics;
        
        public Doc() {
            name = "";
            topics = new ArrayList<>();
        }
        
        public void setName(String pName) {
            name = pName;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the topics
         */
        public List<MsgTop> getTopics() {
            return topics;
        }

        /**
         * @param topics the topics to set
         */
        public void setTopics(List<MsgTop> topics) {
            this.topics = topics;
        }

        /**
         * @return the domainTopics
         */
        public List<DomainRel> getDomainTopics() {
            return domainTopics;
        }

        /**
         * @param domainTopics the domainTopics to set
         */
        public void setDomainTopics(List<DomainRel> domainTopics) {
            this.domainTopics = domainTopics;
        }

        /**
         *
         * @return the knowledgeSource
         */
        public String getKnowledgeSource() {
            return knowledgeSource;
        }

        /**
         *
         * @param knowledgeSource the knowledgeSource to set
         */
        public void setKnowledgeSource(String knowledgeSource) {
            this.knowledgeSource = knowledgeSource;
        }
    }
    
    static class MsgTop {
        
        private List<String> labels;
        private List<Translation> translations;
        private String definition;
        private List<Parent> parentCategories;
        private String topic;
        private String sourceTitle;
        private int id;
        private float domainRelatedness;
        private List<Link> linksIn;
        private List<Link> linksOut;
                
        
        public MsgTop() {
            linksOut=new ArrayList<>();
            linksIn = new ArrayList<>();
            labels = new ArrayList<>();
            translations = new ArrayList<>();
            definition = "";
            parentCategories = new ArrayList<>();
            id = -1;
            topic = "";
            sourceTitle = "";
            domainRelatedness = 0f;
        }

        /**
         * @return the labels
         */
        public List<String> getLabels() {
            return labels;
        }

        /**
         * @param labels the labels to set
         */
        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        /**
         * @return the translations
         */
        public List<Translation> getTranslations() {
            return translations;
        }

        /**
         * @param translations the translations to set
         */
        public void setTranslations(List<Translation> translations) {
            this.translations = translations;
        }

        /**
         * @return the definition
         */
        public String getDefinition() {
            return definition;
        }

        /**
         * @param definition the definition to set
         */
        public void setDefinition(String definition) {
            this.definition = definition;
        }

        /**
         * @return the parentCategories
         */
        public List<Parent> getParentCategories() {
            return parentCategories;
        }

        /**
         * @param parentCategories the parentCategories to set
         */
        public void setParentCategories(List<Parent> parentCategories) {
            this.parentCategories = parentCategories;
        }

        /**
         * @return the topic
         */
        public String getTopic() {
            return topic;
        }

        /**
         * @param topic the topic to set
         */
        public void setTopic(String topic) {
            this.topic = topic;
        }

        /**
         * @return the sourceTitle
         */
        public String getSourceTitle() {
            return sourceTitle;
        }

        /**
         * @param sourceTitle the sourceTitle to set
         */
        public void setSourceTitle(String sourceTitle) {
            this.sourceTitle = sourceTitle;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * @return the Domainrelatedness
         */
        public float getDomainRelatedness() {
            return domainRelatedness;
        }

        /**
         * @param relatedness the Domainrelatedness to set
         */
        public void setDomainRelatedness(float relatedness) {
            this.domainRelatedness = relatedness;
        }

        private void setLinksIn(List<Link> linksIn) {
            this.linksIn=linksIn;
        }

        private void setLinksOut(List<Link> linksOut) {
            this.linksOut=linksOut;
            
        }

        /**
         * @return the linksIn
         */
        public List<Link> getLinksIn() {
            return linksIn;
        }

        /**
         * @return the linksOut
         */
        public List<Link> getLinksOut() {
            return linksOut;
        }
        
    }
    static class Link{
        private int id =-1;
        private double relatedness =0;

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * @return the relatedness
         */
        public double getRelatedness() {
            return relatedness;
        }

        /**
         * @param relatedness the relatedness to set
         */
        public void setRelatedness(double relatedness) {
            this.relatedness = relatedness;
        }
    }
    static class Translation {
        
        private String lang;
        private String text;
        
        public Translation() {
            lang = "";
            text = "";
            
        }

        /**
         * @return the lang
         */
        public String getLang() {
            return lang;
        }

        /**
         * @param lang the lang to set
         */
        public void setLang(String lang) {
            this.lang = lang;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * @param text the text to set
         */
        public void setText(String text) {
            this.text = text;
        }
        
    }
    
    static class Parent {
        
        private int id;
        private String sourceTitle;

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * @return the sourceTitle
         */
        public String getSourceTitle() {
            return sourceTitle;
        }

        /**
         * @param sourceTitle the sourceTitle to set
         */
        public void setSourceTitle(String sourceTitle) {
            this.sourceTitle = sourceTitle;
        }
    }
    
    static class DomainRel {
        
        private String title;
        private int id;
        
        public DomainRel() {
            title = "";
            id = -1;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title the title to set
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(int id) {
            this.id = id;
        }
        
    }
    
    static class LiteDoc {
        
        private String name;
        private List<String> sentenceList;
        private List<LinkedList<Token>> tokenizedSentenceList;
        
        public LiteDoc(String pName, List<String> pSentenceList, List<LinkedList<Token>> pTokenizedSentenceList) {
            name = pName;
            sentenceList = pSentenceList;
            tokenizedSentenceList = pTokenizedSentenceList;
            
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the sentenceList
         */
        public List<String> getSentenceList() {
            return sentenceList;
        }

        /**
         * @param sentenceList the sentenceList to set
         */
        public void setSentenceList(List<String> sentenceList) {
            this.sentenceList = sentenceList;
        }

        /**
         * @return the tokenizedSentenceList
         */
        public List<LinkedList<Token>> getTokenizedSentenceList() {
            return tokenizedSentenceList;
        }

        /**
         * @param tokenizedSentenceList the tokenizedSentenceList to set
         */
        public void setTokenizedSentenceList(List<LinkedList<Token>> tokenizedSentenceList) {
            this.tokenizedSentenceList = tokenizedSentenceList;
        }
    }
}
