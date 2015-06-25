
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class Translation implements Serializable{
    private static final long serialVersionUID = 1L;

    private String lang;
    private String text;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
