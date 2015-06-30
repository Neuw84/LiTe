
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Request {

    private String term1;
    private String responseFormat;
    private String term2;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public String getTerm1() {
        return term1;
    }

    /**
     *
     * @param term1
     */
    public void setTerm1(String term1) {
        this.term1 = term1;
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
    public String getTerm2() {
        return term2;
    }

    /**
     *
     * @param term2
     */
    public void setTerm2(String term2) {
        this.term2 = term2;
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
