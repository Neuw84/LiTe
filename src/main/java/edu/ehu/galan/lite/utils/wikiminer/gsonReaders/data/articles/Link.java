/*
 * Copyright (C) 2014 angel
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
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author angel
 */
public class Link implements Serializable{
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private double relatedness;
    private Map<String, Object> additionalProperties = new HashMap<>();

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

    /**
     * @return the additionalProperties
     */
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * @param additionalProperties the additionalProperties to set
     */
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

}
