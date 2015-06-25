package edu.ehu.galan.lite.mixer.disambiguation;

/*
 *    AbstractDisambiguation.java
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract class that should extends the different disambiguation Methods
 *
 * @author Angel Conde Manjon
 */

public abstract class AbstractDisambiguation {

    /**
     * The Topic list that will be processed
     */
    protected List<Topic> topicList; // the topic list to be disambiguated
    /**
     * The name of the disambiguation method
     */
    protected  String name; // the name of the Disambiguation method
    private final transient Properties properties;
    transient final Logger logger = LoggerFactory.getLogger(AbstractDisambiguation.class);

    /**
     *
     * @param pName
     * @param pPropsDir
     */
    public AbstractDisambiguation(String pName,String pPropsDir) {
        topicList = new ArrayList<>();

        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(pPropsDir+"lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            System.out.println("general.conf not found in: resources/config directory");
            logger.error(AbstractDisambiguation.class.getName(), ex);
            System.exit(-1);
        } catch (IOException ex) {
            System.out.println("general.conf not loaded IO exception");
            logger.error(AbstractDisambiguation.class.getName(), ex);
            System.exit(-1);

        }

    }

    /**
     * Given a topic list with contains topics that need
     * disambituation aca, topics that are mapped to more than one article in
     * the wikipedia (topics those id is -1) returns a list where all the topics
     * are disambiguated. (check CValueDisambiguator for an example)
     *
     * @param pDoc - the document that contains the topics to be disambiguated
     */
    public abstract void disambiguateTopics(Document pDoc);

    /**
     * the name of the current disambiguation method
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Return a String with json extracted terms, name of algorithm and whether
     * is scored or not folder)
     *
     * @return - String with the results of the disambiguation in JSON format
     *
     */
    public String toJson() {
        Gson son = new Gson();
        return son.toJson(this);
    }

    /**
     * Save disambiguation results in Json format to tmp directory
     *
     */
    
    public void saveGsonToTmp() {
        FileWriter outFile = null;
        try {
            outFile = new FileWriter(properties.getProperty("tmpDir") + File.separator + this.getName() + ".json");
            boolean first = true;
            try (PrintWriter out = new PrintWriter(outFile)) {
                Gson son = new Gson();
                out.print(son.toJson(this));
            }
        } catch (IOException ex) {
            logger.warn(AbstractDisambiguation.class.getName(), "couldn't save the algorithm results to temp directory in json format", ex);
        } finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            } catch (IOException ex) {
                logger.warn(AbstractDisambiguation.class.getName(), "Error while closing the file", ex);
            }
        }
    }

    /**
     * Given a String with a file path retuns a list of disambiguated topics
     *
     * @param pFile
     * @return a topic list
     */
    
   public List<Topic> readFile(String pFile) {
       //TODO CHECK it
        List<Topic> list = new ArrayList<>();
        List<String> strings = null;
        try {
            strings = Files.readAllLines(Paths.get(pFile), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.warn(AbstractDisambiguation.class.getName(), "Error reading the disamgiguation file", ex);
        }
        for (String str : strings) {
            String[] pas = str.split("\t");
            String topic = pas[0];
            int id = Integer.parseInt(pas[1]);
            Topic top = new Topic(topic);
            top.setId(id);
            list.add(top);
        }
        list.stream().forEach((top) ->  System.out.println(top.getTopic() + "\t" + top.getId()));
        return list;
    }
}
