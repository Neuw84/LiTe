
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

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

    private String ids;
    private String labels;
    private String translations;
    private String responseFormat;
    private String parentCategories;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public String getIds() {
        return ids;
    }

    /**
     *
     * @param ids
     */
    public void setIds(String ids) {
        this.ids = ids;
    }

    /**
     *
     * @return
     */
    public String getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     */
    public void setLabels(String labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     */
    public String getTranslations() {
        return translations;
    }

    /**
     *
     * @param translations
     */
    public void setTranslations(String translations) {
        this.translations = translations;
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
    public String getParentCategories() {
        return parentCategories;
    }

    /**
     *
     * @param parentCategories
     */
    public void setParentCategories(String parentCategories) {
        this.parentCategories = parentCategories;
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
