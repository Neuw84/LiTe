
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Label implements Serializable{
    private static final long serialVersionUID = 1L;
    private String text;
    private Integer linkDocCount;
    private Integer linkOccCount;
    private Integer docCount;
    private Integer occCount;
    private Double linkProbability;
    private List<Sense> senses = new ArrayList<>();
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Integer getDocCount() {
        return docCount;
    }

    public void setDocCount(Integer docCount) {
        this.docCount = docCount;
    }

    public Integer getOccCount() {
        return occCount;
    }

    public void setOccCount(Integer occCount) {
        this.occCount = occCount;
    }

    public Double getLinkProbability() {
        return linkProbability;
    }

    public void setLinkProbability(Double linkProbability) {
        this.linkProbability = linkProbability;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
