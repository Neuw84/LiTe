
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.search;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;


@Generated("com.googlecode.jsonschema2pojo")
public class Sense {

    private Integer id;
    private String title;
    private Integer linkDocCount;
    private Integer linkOccCount;
    private Double priorProbability;
    private Boolean fromTitle;
    private Boolean fromRedirect;
    private Boolean isSelected;
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

    public Integer getLinkDocCount() {
        return linkDocCount;
    }

    public void setLinkDocCount(Integer linkDocCount) {
        this.linkDocCount = linkDocCount;
    }

    public Integer getLinkOccCount() {
        return linkOccCount;
    }

    public void setLinkOccCount(Integer linkOccCount) {
        this.linkOccCount = linkOccCount;
    }

    public Double getPriorProbability() {
        return priorProbability;
    }

    public void setPriorProbability(Double priorProbability) {
        this.priorProbability = priorProbability;
    }

    public Boolean getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(Boolean fromTitle) {
        this.fromTitle = fromTitle;
    }

    public Boolean getFromRedirect() {
        return fromRedirect;
    }

    public void setFromRedirect(Boolean fromRedirect) {
        this.fromRedirect = fromRedirect;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
