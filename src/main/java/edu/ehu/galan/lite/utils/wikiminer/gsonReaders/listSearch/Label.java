
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listSearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
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

    /**
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     */
    public Integer getLinkDocCount() {
        return linkDocCount;
    }

    /**
     *
     * @param linkDocCount
     */
    public void setLinkDocCount(Integer linkDocCount) {
        this.linkDocCount = linkDocCount;
    }

    /**
     *
     * @return
     */
    public Integer getLinkOccCount() {
        return linkOccCount;
    }

    /**
     *
     * @param linkOccCount
     */
    public void setLinkOccCount(Integer linkOccCount) {
        this.linkOccCount = linkOccCount;
    }

    /**
     *
     * @return
     */
    public Integer getDocCount() {
        return docCount;
    }

    /**
     *
     * @param docCount
     */
    public void setDocCount(Integer docCount) {
        this.docCount = docCount;
    }

    /**
     *
     * @return
     */
    public Integer getOccCount() {
        return occCount;
    }

    /**
     *
     * @param occCount
     */
    public void setOccCount(Integer occCount) {
        this.occCount = occCount;
    }

    /**
     *
     * @return
     */
    public Double getLinkProbability() {
        return linkProbability;
    }

    /**
     *
     * @param linkProbability
     */
    public void setLinkProbability(Double linkProbability) {
        this.linkProbability = linkProbability;
    }

    /**
     *
     * @return
     */
    public List<Sense> getSenses() {
        return senses;
    }

    /**
     *
     * @param senses
     */
    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    /**
     *
     * @return
     */
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    /**
     *
     * @param name
     * @param value
     */
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
