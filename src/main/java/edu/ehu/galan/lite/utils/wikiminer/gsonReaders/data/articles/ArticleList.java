
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleList implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String definition;
    private Integer id;
    private String title;
    private List<Label> labels = new ArrayList<>();
    private List<Translation> translations = new ArrayList<>();
    private List<ParentCategory> parentCategories = new ArrayList<>();
    private Integer totalParentCategories;
    private List<Link> outLinks=new ArrayList<>();
    private List<Link> inLinks=new ArrayList<>();

    private final Map<String, Object> additionalProperties = new HashMap<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
      public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = title;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }

    public List<ParentCategory> getParentCategories() {
        return parentCategories;
    }

    public void setParentCategories(List<ParentCategory> parentCategories) {
        this.parentCategories = parentCategories;
    }

    public Integer getTotalParentCategories() {
        return totalParentCategories;
    }

    public void setTotalParentCategories(Integer totalParentCategories) {
        this.totalParentCategories = totalParentCategories;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * @return the outLinks
     */
    public List<Link> getOutLinks() {
        return outLinks;
    }

    /**
     * @param outLinks the outLinks to set
     */
    public void setOutLinks(List<Link> outLinks) {
        this.outLinks = outLinks;
    }

    /**
     * @return the inLinks
     */
    public List<Link> getInLinks() {
        return inLinks;
    }

    /**
     * @param inLinks the inLinks to set
     */
    public void setInLinks(List<Link> inLinks) {
        this.inLinks = inLinks;
    }

}
