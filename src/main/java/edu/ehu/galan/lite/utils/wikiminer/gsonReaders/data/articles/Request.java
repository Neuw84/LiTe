
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Request implements Serializable{
    private static final long serialVersionUID = 1L;

    private String ids;
    private String labels;
    private String translations;
    private String responseFormat;
    private String parentCategories;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getTranslations() {
        return translations;
    }

    public void setTranslations(String translations) {
        this.translations = translations;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public String getParentCategories() {
        return parentCategories;
    }

    public void setParentCategories(String parentCategories) {
        this.parentCategories = parentCategories;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
