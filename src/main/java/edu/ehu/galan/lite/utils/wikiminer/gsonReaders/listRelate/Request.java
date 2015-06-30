
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Request implements Serializable{
    private static final long serialVersionUID = 1L;

    private String relatedness;
    private String numTerms;
    private String termList;
    private String goldList;
    private String responseFormat;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public String getRelatedness() {
        return relatedness;
    }

    /**
     *
     * @param relatedness
     */
    public void setRelatedness(String relatedness) {
        this.relatedness = relatedness;
    }

    /**
     *
     * @return
     */
    public String getNumTerms() {
        return numTerms;
    }

    /**
     *
     * @param numTerms
     */
    public void setNumTerms(String numTerms) {
        this.numTerms = numTerms;
    }

    /**
     *
     * @return
     */
    public String getTermList() {
        return termList;
    }

    /**
     *
     * @param termList
     */
    public void setTermList(String termList) {
        this.termList = termList;
    }

    /**
     *
     * @return
     */
    public String getGoldList() {
        return goldList;
    }

    /**
     *
     * @param goldList
     */
    public void setGoldList(String goldList) {
        this.goldList = goldList;
    }

    /**
     *
     * @return
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     *
     * @param responseFormat
     */
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
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
