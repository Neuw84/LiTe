
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Request implements Serializable{
    private static final long serialVersionUID = 1L;

    private String term1List;
    private String term2List;
    private String responseFormat;
    private final Map<String, Object> additionalProperties = new HashMap<>();
    private String wikipedia;
    
    public String getTerm1List() {
        return term1List;
    }

    public void setTerm1List(String term1List) {
        this.term1List = term1List;
    }

    public String getTerm2List() {
        return term2List;
    }

    public void setTerm2List(String term2List) {
        this.term2List = term2List;
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
     public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

}
