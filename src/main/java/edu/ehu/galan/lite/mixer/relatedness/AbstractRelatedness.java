package edu.ehu.galan.lite.mixer.relatedness;

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
import edu.ehu.galan.lite.mixer.disambiguation.cValueWikiminerDisambiguation.CValueWikiDisambiguator;
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
 * Abstract class that the different relatedness methods should extend
 *
 * @author Angel Conde Manjon
 */
public abstract class AbstractRelatedness {

    private final String name;
    private final transient Properties props;
    private transient final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractRelatedness.class);

    /**
     * The topic list to be processed
     */
    protected List<Topic> topicList;

    public AbstractRelatedness(String pName, String pPropsDir) {
        name = pName;
        props = new Properties();
        try {
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            System.out.println("general.conf not found");
            Logger.getLogger(AbstractRelatedness.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractRelatedness.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Given a Document, it returns a List of topics that pass the applied relatedness measure.
     * Terms below the relatedness are deleted!!
     *
     * @param pDoc - the Document whose topics will be processed
     */
    public abstract void relate(Document pDoc);

    /**
     * Given a Document, it returns a List of topics with their relatedness scores using the given
     * measures, return all topics with a score > 0 !!
     *
     * @param pDoc - the Document whose topics will be processed
     */
    public abstract void relateNotDelete(Document pDoc);

    /**
     * Saves the current Topic list to tmp folder (configured in the resources folder)
     */
    public void saveToTmp() {
        FileWriter outFile = null;
        PrintWriter out = null;
        try {
            outFile = new FileWriter(props.getProperty("tmpDir") + File.separator + this.name);
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
            Logger.getLogger(CValueWikiDisambiguator.class.getName()).log(Level.SEVERE, "Error while saving relatedness results", ex);
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
     * Save disambiguation results in Json format to tmp directory
     *
     */
    public void saveGsonToTmp() {
        FileWriter outFile = null;
        try {
            outFile = new FileWriter(props.getProperty("tmpDir") + File.separator + name + ".json");
            try (PrintWriter out = new PrintWriter(outFile)) {
                Gson son = new Gson();
                out.print(son.toJson(this));
            }
        } catch (IOException ex) {
            logger.warn(AbstractRelatedness.class.getName(), "couldn't save the relatedness method results to temp directory in json format", ex);
        } finally {
            try {
                if (outFile != null) {
                    outFile.close();
                }
            } catch (IOException ex) {
                logger.warn(AbstractRelatedness.class.getName(), "Error while closing the file", ex);
            }
        }
    }

    /**
     * The name of the current relatedness method
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Given a String with a file path retuns a list of related topics
     *
     * @param pFile
     * @return a topic list
     */
    public List<Topic> readFile(String pFile) {
        List<Topic> tlist = new ArrayList<>();
        List<String> list = null;
        try {

            list = Files.readAllLines(Paths.get(pFile), StandardCharsets.ISO_8859_1);
        } catch (IOException ex) {
            Logger.getLogger(AbstractRelatedness.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String line : list) {
            String[] split = line.split("\t");
            Topic top = new Topic(split[0]);
            top.addLabel(split[0]);
            top.addLabel(split[2]);
            top.setId(Integer.parseInt(split[1]));
            top.setSourceTitle(split[2]);
            tlist.add(top);
        }
        return tlist;
    }
}
