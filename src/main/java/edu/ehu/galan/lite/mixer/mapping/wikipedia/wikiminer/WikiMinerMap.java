package edu.ehu.galan.lite.mixer.mapping.wikipedia.wikiminer;

/*
 *    WikiMinerMap.java
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


import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Term;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.mixer.mapping.AbstractTermMapping;
import edu.ehu.galan.lite.model.Topic;
import edu.ehu.galan.lite.utils.wikiminer.WikiminnerHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that takes the a list of term candidates, then maps the candidates
 * terms to the wikipedia using Wikiminner search procedure. For configuration changes check the
 * Wikiminner configuration/help (basically what label changes are taken into account aka stemmers for 
 * label index generation)
 *
 * @author Angel Conde Manjon
 */
public class WikiMinerMap extends AbstractTermMapping {

    private final transient Properties properties;
    private final transient WikiminnerHelper helper;

    /**
     * Default constructor
     *
     * @param pPropDirs
     * @param pHelper
     */
    
    public WikiMinerMap(String pPropDirs,WikiminnerHelper pHelper) {
        
        super("WikipediaMinerMap", pPropDirs);
        properties = new Properties();
        helper=pHelper;
        try {
            properties.load(new FileInputStream(new File(pPropDirs + "lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
            System.out.println("general.conf not found");
            Logger.getLogger(AbstractAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Maps a set of terms in a corpus to articles (if the maps is ambiguous
     * then all the possible wikipedia article ids are returned), the terms that
     * are mapped correctly are returned and saved in the corpus those not
     * mapped are deleted. Then another step of duplicate removal based on the
     * wikipedia article ids is done.
     *
     */
    @Override
    public void mapCorpus(Document pDoc) {
            List<Term> mixedTermList = pDoc.getMixedTermList();
            WikiminnerHelper wiki = helper;
            List<Topic> aux=topicList = wiki.parallelSearch(mixedTermList);
            pDoc.setTopicList(aux);                              
    }

    /**
     *
     * @param pTermList
     */
    public void addTermList(List<Term> pTermList) {
        termList = pTermList;
    }

}
