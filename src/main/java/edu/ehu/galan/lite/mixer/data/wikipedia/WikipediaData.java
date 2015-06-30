package edu.ehu.galan.lite.mixer.data.wikipedia;
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

import edu.ehu.galan.lite.algorithms.AbstractAlgorithm;
import edu.ehu.galan.lite.model.Document;
import edu.ehu.galan.lite.mixer.data.AbstractData;
import edu.ehu.galan.lite.utils.wikiminer.WikiminnerHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.LoggerFactory;

/**
 * Class that obtain data related to a topic from wikipedia (translations),
 * whether is an article or not, parent categories, etc.
 *
 * @author Angel Conde Manjon
 */
public class WikipediaData extends AbstractData {

    private final transient Properties props;
    private transient final org.slf4j.Logger logger = LoggerFactory.getLogger(WikipediaData.class);
    private transient final String resDir;
    private transient final WikiminnerHelper helper;
    private boolean getLinks=false;

    /**
     *
     * @param pPropsDir
     * @param pHelper
     */
    public WikipediaData(String pPropsDir,WikiminnerHelper pHelper) {
        super("Wikipedia", pPropsDir);
        props = new Properties();
        resDir=pPropsDir;
        helper=pHelper;
        try {
            props.load(new FileInputStream(new File(pPropsDir + "lite/configs/general.conf")));
        } catch (FileNotFoundException ex) {
           System.out.println("general.conf not found in: resources/lite/configs/");
            logger.error(AbstractAlgorithm.class.getName(), ex);
            System.exit(-1);
        } catch (IOException ex) {
            System.out.println("general.conf not loaded IO exception");
            logger.error(AbstractAlgorithm.class.getName(), ex);
            System.exit(-1);
        }
    }

    @Override
    public void processDocument(Document pDoc) {
       // for (Document doc : pCorpus.getDocQueue()) {
            super.topicList = pDoc.getTopicList();
            helper.setProperties(resDir);
            helper.getData(pDoc,getLinks);
        //}
            
    }
    
    /**
     *
     * @param links
     */
    public void getLinks(boolean links){
        getLinks=links;
    }
    
    

}
