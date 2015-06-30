
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listSearch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Sense implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String title;
    private Integer linkDocCount;
    private Integer linkOccCount;
    private Double priorProbability;
    private Boolean fromTitle;
    private Boolean fromRedirect;
    private Boolean isSelected;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
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
    public Double getPriorProbability() {
        return priorProbability;
    }

    /**
     *
     * @param priorProbability
     */
    public void setPriorProbability(Double priorProbability) {
        this.priorProbability = priorProbability;
    }

    /**
     *
     * @return
     */
    public Boolean getFromTitle() {
        return fromTitle;
    }

    /**
     *
     * @param fromTitle
     */
    public void setFromTitle(Boolean fromTitle) {
        this.fromTitle = fromTitle;
    }

    /**
     *
     * @return
     */
    public Boolean getFromRedirect() {
        return fromRedirect;
    }

    /**
     *
     * @param fromRedirect
     */
    public void setFromRedirect(Boolean fromRedirect) {
        this.fromRedirect = fromRedirect;
    }

    /**
     *
     * @return
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     *
     * @param isSelected
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
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
