
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

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

    public List<Comparison> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<Comparison> comparisons) {
        this.comparisons = comparisons;
    }

    public List<Integer> getInvalidIds() {
        return invalidIds;
    }

    public void setInvalidIds(List<Integer> invalidIds) {
        this.invalidIds = invalidIds;
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

}
