
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class GSonCompare {

    private Double relatedness;
    private DisambiguationDetails disambiguationDetails;
    private String service;
    private Request request;
    private final Map<String, Object> additionalProperties = new HashMap<>();

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
    public DisambiguationDetails getDisambiguationDetails() {
        return disambiguationDetails;
    }

    /**
     *
     * @param disambiguationDetails
     */
    public void setDisambiguationDetails(DisambiguationDetails disambiguationDetails) {
        this.disambiguationDetails = disambiguationDetails;
    }

    /**
     *
     * @return
     */
    public String getService() {
        return service;
    }

    /**
     *
     * @param service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     *
     * @return
     */
    public Request getRequest() {
        return request;
    }

    /**
     *
     * @param request
     */
    public void setRequest(Request request) {
        this.request = request;
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
