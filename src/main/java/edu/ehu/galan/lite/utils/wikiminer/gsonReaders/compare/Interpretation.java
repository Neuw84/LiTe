
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Interpretation {

    private Integer id1;
    private Integer id2;
    private String title1;
    private String title2;
    private Double relatedness;
    private Double disambiguationConfidence;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public Integer getId1() {
        return id1;
    }

    /**
     *
     * @param id1
     */
    public void setId1(Integer id1) {
        this.id1 = id1;
    }

    /**
     *
     * @return
     */
    public Integer getId2() {
        return id2;
    }

    /**
     *
     * @param id2
     */
    public void setId2(Integer id2) {
        this.id2 = id2;
    }

    /**
     *
     * @return
     */
    public String getTitle1() {
        return title1;
    }

    /**
     *
     * @param title1
     */
    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    /**
     *
     * @return
     */
    public String getTitle2() {
        return title2;
    }

    /**
     *
     * @param title2
     */
    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    /**
     *
     * @return
     */
    public Double getRelatedness() {
        return relatedness;
    }

    /**
     *
     * @param relatedness
     */
    public void setRelatedness(Double relatedness) {
        this.relatedness = relatedness;
    }

    /**
     *
     * @return
     */
    public Double getDisambiguationConfidence() {
        return disambiguationConfidence;
    }

    /**
     *
     * @param disambiguationConfidence
     */
    public void setDisambiguationConfidence(Double disambiguationConfidence) {
        this.disambiguationConfidence = disambiguationConfidence;
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
