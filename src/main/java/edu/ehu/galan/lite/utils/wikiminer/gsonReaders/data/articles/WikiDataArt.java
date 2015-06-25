package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class WikiDataArt implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Integer> invalidList = new ArrayList<>();
    private List<ArticleList> articleList = new ArrayList<>();
    private List<Integer> nullList = new ArrayList<>();
    private String service;
    private Request request;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public List<Integer> getInvalidList() {
        return invalidList;
    }

    public void setInvalidList(List<Integer> invalidList) {
        this.invalidList = invalidList;
    }

    public List<ArticleList> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleList> articleList) {
        this.articleList = articleList;
    }

    public List<Integer> getNullList() {
        return nullList;
    }

    public void setNullList(List<Integer> nullList) {
        this.nullList = nullList;
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
