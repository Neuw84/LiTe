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

package edu.ehu.galan.lite.utils.euslemutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper for Euslem the Basque Part Of Speech tagger, uses a rest service in order to analyze 
 * senteces writen in Basque 
 * !!Not supported!!
 * @author Angel Conde MAnjon
 */

public class EuslemHelper {

    private DefaultHttpClient httpClient;
    private final String url=null;
    private static  Properties props=null;
    private String wikiminerUrl;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    static{
        try {
            props=new Properties();
            props.load(new FileInputStream(new File( "resources/lite/configs/general.conf")));
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EuslemHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public EuslemHelper() {
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

    }

    public void closeConnection() {
        httpClient.getConnectionManager().shutdown();
    }

    public void openConnection() {
        httpClient = null;
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
    }

    public void analyzeText(String pText, int pLevel) {
        System.out.println(props.getProperty("euslemUrl"));
        try {
            HttpPost getRequest = new HttpPost(
                    //&definitionmaxImageWidth=800&maxImageHeight=600&emphasisFormat=HTML&definitionLenght=LONG
                    props.getProperty("euslemUrl") + "/services/EuslemWS?responseFormat=JSON");
            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            System.out.println(EntityUtils.toString(response.getEntity()));
                    } catch (IOException ex) {
            java.util.logging.Logger.getLogger(EuslemHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
