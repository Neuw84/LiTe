
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.markUp;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

@Generated("org.jsonschema2pojo")
public class GSonMarkUp implements Serializable{
    private static final long serialVersionUID = 1L;

    @Expose
    private List<Object> invalidList = new ArrayList<Object>();
    @Expose
    private List<ArticleList> articleList = new ArrayList<ArticleList>();
    @Expose
    private List<Object> nullList = new ArrayList<Object>();
    @Expose
    private List<Object> invalidTitles = new ArrayList<Object>();
    @Expose
    private String service;
    @Expose
    private Request request;

    /**
     * 
     * @return
     *     The invalidList
     */
    public List<Object> getInvalidList() {
        return invalidList;
    }

    /**
     * 
     * @param invalidList
     *     The invalidList
     */
    public void setInvalidList(List<Object> invalidList) {
        this.invalidList = invalidList;
    }

    /**
     * 
     * @return
     *     The articleList
     */
    public List<ArticleList> getArticleList() {
        return articleList;
    }

    /**
     * 
     * @param articleList
     *     The articleList
     */
    public void setArticleList(List<ArticleList> articleList) {
        this.articleList = articleList;
    }

    /**
     * 
     * @return
     *     The nullList
     */
    public List<Object> getNullList() {
        return nullList;
    }

    /**
     * 
     * @param nullList
     *     The nullList
     */
    public void setNullList(List<Object> nullList) {
        this.nullList = nullList;
    }

    /**
     * 
     * @return
     *     The invalidTitles
     */
    public List<Object> getInvalidTitles() {
        return invalidTitles;
    }

    /**
     * 
     * @param invalidTitles
     *     The invalidTitles
     */
    public void setInvalidTitles(List<Object> invalidTitles) {
        this.invalidTitles = invalidTitles;
    }

    /**
     * 
     * @return
     *     The service
     */
    public String getService() {
        return service;
    }

    /**
     * 
     * @param service
     *     The service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * 
     * @return
     *     The request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * 
     * @param request
     *     The request
     */
    public void setRequest(Request request) {
        this.request = request;
    }

}
