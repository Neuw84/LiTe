package edu.ehu.galan.lite.mixer.data;

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
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.model.Topic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class that represent a method for obtaining the data related 
 * to a topic
 * @author Angel Conde Manjon
 */
public abstract class AbstractData {

    /**
     * The Topic list that will be processed
     */
    protected List<Topic> topicList; // the topic list to be disambiguated
    /**
     * The name of the disambiguation method
     */
    
    protected String name; // the name of the Disambiguation method
    private final transient Properties properties;
    private transient final Logger logger = LoggerFactory.getLogger(AbstractData.class);

    /**
     * Default constructor for superclass, needs the name of source knowledge
     * where the data will be extracted
     *
     * @param pName
     * @param pPropsDir
     */
    public AbstractData(String pName,String pPropsDir) {
        name = pName;
        topicList = new ArrayList<>();

        properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(pPropsDir+"lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            logger.error("general.conf not found in: resources/config directory" , ex);
            System.exit(-1);
        } catch (IOException ex) {
            logger.error("general.conf not loaded IO exception", ex);
            System.exit(-1);

        }

    }

    /**
     * Given a Document it obtain all the metadata from the topics, whether if
     * its a class or an article (for example in  Wikipedia), translations to other
     * languages, labels (different ways of refering to this topic),etc
     *
     * @param pDoc
     */
    public abstract void processDocument(Document pDoc);

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
     * the name of the current data method
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
            boolean first = true;
            try (PrintWriter out = new PrintWriter(outFile)) {
                Gson son = new Gson();
                out.print(son.toJson(this));
            }
        } catch (IOException ex) {
            logger.warn("couldn't save the algorithm results to temp directory in json format", ex);
        } finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            } catch (IOException ex) {
                logger.warn("Error while closing the file", ex);
            }
        }
    }
}
