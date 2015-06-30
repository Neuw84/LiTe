
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Comparisons implements Serializable {

    @Expose
    private List<Comparison> comparisons = new ArrayList<>();
    @Expose
    private List<Integer> invalidIds = new ArrayList<>();
    @Expose
    private String service;
    @Expose
    private Request request;

    /**
     *
     * @return
     */
    public List<Comparison> getComparisons() {
        return comparisons;
    }

    /**
     *
     * @param comparisons
     */
    public void setComparisons(List<Comparison> comparisons) {
        this.comparisons = comparisons;
    }

    /**
     *
     * @return
     */
    public List<Integer> getInvalidIds() {
        return invalidIds;
    }

    /**
     *
     * @param invalidIds
     */
    public void setInvalidIds(List<Integer> invalidIds) {
        this.invalidIds = invalidIds;
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

}
