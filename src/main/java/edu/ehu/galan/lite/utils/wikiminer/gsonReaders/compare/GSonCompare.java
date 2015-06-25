
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class GSonCompare {

    private Double relatedness;
    private DisambiguationDetails disambiguationDetails;
    private String service;
    private Request request;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public Double getRelatedness() {
        return relatedness;
    }

    public void setRelatedness(Double relatedness) {
        this.relatedness = relatedness;
    }

    public DisambiguationDetails getDisambiguationDetails() {
        return disambiguationDetails;
    }

    public void setDisambiguationDetails(DisambiguationDetails disambiguationDetails) {
        this.disambiguationDetails = disambiguationDetails;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
