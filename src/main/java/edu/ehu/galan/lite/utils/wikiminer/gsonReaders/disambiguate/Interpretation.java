package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Interpretation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id1;
    private Integer id2;
    private String title1;
    private String title2;
    private Double relatedness;
    private Double disambiguationConfidence;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public Integer getId1() {
        return id1;
    }

    public void setId1(Integer id1) {
        this.id1 = id1;
    }

    public Integer getId2() {
        return id2;
    }

    public void setId2(Integer id2) {
        this.id2 = id2;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public Double getRelatedness() {
        return relatedness;
    }

    public void setRelatedness(Double relatedness) {
        this.relatedness = relatedness;
    }

    public Double getDisambiguationConfidence() {
        return disambiguationConfidence;
    }

    public void setDisambiguationConfidence(Double disambiguationConfidence) {
        this.disambiguationConfidence = disambiguationConfidence;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
