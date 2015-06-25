
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Request implements Serializable{
    private static final long serialVersionUID = 1L;

    private String relatedness;
    private String numTerms;
    private String termList;
    private String goldList;
    private String responseFormat;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getRelatedness() {
        return relatedness;
    }

    public void setRelatedness(String relatedness) {
        this.relatedness = relatedness;
    }

    public String getNumTerms() {
        return numTerms;
    }

    public void setNumTerms(String numTerms) {
        this.numTerms = numTerms;
    }

    public String getTermList() {
        return termList;
    }

    public void setTermList(String termList) {
        this.termList = termList;
    }

    public String getGoldList() {
        return goldList;
    }

    public void setGoldList(String goldList) {
        this.goldList = goldList;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
