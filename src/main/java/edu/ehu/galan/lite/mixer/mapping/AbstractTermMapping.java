package edu.ehu.galan.lite.mixer.mapping;

/*
 *    AbstractTermMapping.java
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

import com.google.gson.Gson;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.Topic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that other mapping methods should extend
 *
 * @author Angel Conde Manjon
 */
public abstract class AbstractTermMapping {

    /**
     *The term list to be processed
     */
    protected List<Term> termList;
    /**
     *The name of the Mapping algorithm
     */
    protected String name;
    private final transient Properties properties;
    transient final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractTermMapping.class);
    /**
     *
     */
    protected List<Topic> topicList;

    /**
     * The name of the method for mapping terms to a knowledge source 
     * @param pName
     * @param pPropsDir
     */
    public AbstractTermMapping(String pName,String pPropsDir) {
        name = pName;
        termList = new ArrayList<>();
        topicList = new ArrayList<>();
        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(pPropsDir+"lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            System.out.println("general.conf not found");
            Logger.getLogger(AbstractTermMapping.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractTermMapping.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * maps a document term list to a reference knowledge source (for
     * example to the wikipedia) if and term is ambiguous all the sense must be
     * stored, terms mapped correctly are saved in form of topics with the
     * reference knowledge mapped to the document, those not mapped are deleted.
     * See WikiMap for an example
     *
     * @param pDoc 
     */
    
    public abstract void mapCorpus(Document pDoc);

    /**
     * Saves the current Topic list to tmp folder (configured in the resources
     * folder)
     *
     */
    
    public void saveToTmp() {
        FileWriter outFile = null;
        PrintWriter out = null;
        try {
            outFile = new FileWriter(properties.getProperty("tmpDir") + File.separator + "wikiMap");
            out = new PrintWriter(outFile);
            boolean first = false;
            for (Topic top : getTopicList()) {
                if (!first) {
                    out.printf("%s", top.toString());
                    first = true;
                } else {
                    out.printf("\n%s", top.toString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AbstractTermMapping.class.getName()).log(Level.SEVERE, "Error while saving wikimapping results", ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Return a String with json mapped topics, and name of the mapping method
     *
     * @return - String with the results of the mapping in JSON format
     *
     */
    public String toJson() {
        Gson son = new Gson();
        return son.toJson(this);
    }

    /**
     * The name of the current mapping method
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Save disambiguation results in Json format to tmp directory
     *
     */
    
    public void saveGsonToTmp() {
        FileWriter outFile = null;
        try {
            outFile = new FileWriter(properties.getProperty("tmpDir") + File.separator + this.getName() + ".json");
            try (PrintWriter out = new PrintWriter(outFile)) {
                Gson son = new Gson();
                out.print(son.toJson(this));
            }
        } catch (IOException ex) {
            logger.warn(AbstractTermMapping.class.getName(), "couldn't save the mapping method results to temp directory in json format", ex);
        } finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            } catch (IOException ex) {
                logger.warn(AbstractTermMapping.class.getName(), "Error while closing the file", ex);
            }
        }
    }

    /**
     * Returns the current Term list
     *
     * @return the termList
     */
    public List<Term> getTermList() {
        return termList;
    }

    /**
     * Returns the current Topic list
     *
     * @return the topicList
     */
    public List<Topic> getTopicList() {
        return topicList;
    }

    /**
     * Given a String with a file path containing saved results of the mapping
     * method retuns a list of mapped topics
     *
     * @param pFile
     * @return
     */
    public List<Topic> readFile(String pFile) {
        List<Topic> list = new ArrayList<>();
        List<String> strings = null;
        try {
            strings = Files.readAllLines(Paths.get(pFile), StandardCharsets.ISO_8859_1);

        } catch (IOException ex) {
            Logger.getLogger(AbstractTermMapping.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String str : strings) {
            String[] pas = str.split("\t");
            String topic = pas[0];
            Topic top = new Topic(topic);
            String[] senses = pas[2].split(",");
            for (int i = 0; i < senses.length; i++) {
                if (i == 0 && senses.length == 1) {
                    String string = senses[i].replace("[", "");
                    string = string.replace("]", "");
                    string = string.trim();
                    top.addSense(string);
                } else if (i == 0) {

                    String string = senses[i].replace("[", "");
                    string = string.trim();
                    top.addSense(string);

                } else if (i == senses.length - 1) {

                    String string = senses[i].replace("]", "");
                    string = string.trim();
                    top.addSense(string);

                } else {
                    String string = senses[i];
                    string = string.trim();
                    top.addSense(string);
                }
            }
            int id = Integer.parseInt(pas[1]);
            top.setId(id);
            list.add(top);
        }
        return list;
    }
}
