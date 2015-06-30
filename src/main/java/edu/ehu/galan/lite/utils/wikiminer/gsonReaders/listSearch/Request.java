
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listSearch;

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

    private String responseFormat;
    private String list;
    private final Map<String, Object> additionalProperties = new HashMap<>();

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
    public String getList() {
        return list;
    }

    /**
     *
     * @param list
     */
    public void setList(String list) {
        this.list = list;
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
