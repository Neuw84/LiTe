
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.explore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;


@Generated("com.googlecode.jsonschema2pojo")
public class Label implements Serializable{

    private String text;
    private Integer occurrances;
    private Double proportion;
    private Boolean isPrimary;
    private Boolean fromRedirect;
    private Boolean fromTitle;
    private Map<String, Object> additionalProperties = new HashMap<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getOccurrances() {
        return occurrances;
    }

    public void setOccurrances(Integer occurrances) {
        this.occurrances = occurrances;
    }

    public Double getProportion() {
        return proportion;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getFromRedirect() {
        return fromRedirect;
    }

    public void setFromRedirect(Boolean fromRedirect) {
        this.fromRedirect = fromRedirect;
    }

    public Boolean getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(Boolean fromTitle) {
        this.fromTitle = fromTitle;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
