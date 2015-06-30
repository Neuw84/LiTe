
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.explore;

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
public class GSonExplore implements Serializable{

    private Integer id;
    private String title;
    private List<Label> labels = new ArrayList<>();
    private List<ParentCategory> parentCategories = new ArrayList<>();
    private Integer totalParentCategories;
    private String service;
    private Request request;
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     *
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public List<Label> getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     */
    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     */
    public List<ParentCategory> getParentCategories() {
        return parentCategories;
    }

    /**
     *
     * @param parentCategories
     */
    public void setParentCategories(List<ParentCategory> parentCategories) {
        this.parentCategories = parentCategories;
    }

    /**
     *
     * @return
     */
    public Integer getTotalParentCategories() {
        return totalParentCategories;
    }

    /**
     *
     * @param totalParentCategories
     */
    public void setTotalParentCategories(Integer totalParentCategories) {
        this.totalParentCategories = totalParentCategories;
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
