
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.explore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Label implements Serializable{

    private String text;
    private Integer occurrances;
    private Double proportion;
    private Boolean isPrimary;
    private Boolean fromRedirect;
    private Boolean fromTitle;
    private Map<String, Object> additionalProperties = new HashMap<>();

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
    public Integer getOccurrances() {
        return occurrances;
    }

    /**
     *
     * @param occurrances
     */
    public void setOccurrances(Integer occurrances) {
        this.occurrances = occurrances;
    }

    /**
     *
     * @return
     */
    public Double getProportion() {
        return proportion;
    }

    /**
     *
     * @param proportion
     */
    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }

    /**
     *
     * @return
     */
    public Boolean getIsPrimary() {
        return isPrimary;
    }

    /**
     *
     * @param isPrimary
     */
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
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
