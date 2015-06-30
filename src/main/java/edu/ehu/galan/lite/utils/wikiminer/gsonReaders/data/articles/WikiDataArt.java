package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.data.articles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class WikiDataArt implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Integer> invalidList = new ArrayList<>();
    private List<ArticleList> articleList = new ArrayList<>();
    private List<Integer> nullList = new ArrayList<>();
    private String service;
    private Request request;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public List<Integer> getInvalidList() {
        return invalidList;
    }

    /**
     *
     * @param invalidList
     */
    public void setInvalidList(List<Integer> invalidList) {
        this.invalidList = invalidList;
    }

    /**
     *
     * @return
     */
    public List<ArticleList> getArticleList() {
        return articleList;
    }

    /**
     *
     * @param articleList
     */
    public void setArticleList(List<ArticleList> articleList) {
        this.articleList = articleList;
    }

    /**
     *
     * @return
     */
    public List<Integer> getNullList() {
        return nullList;
    }

    /**
     *
     * @param nullList
     */
    public void setNullList(List<Integer> nullList) {
        this.nullList = nullList;
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
