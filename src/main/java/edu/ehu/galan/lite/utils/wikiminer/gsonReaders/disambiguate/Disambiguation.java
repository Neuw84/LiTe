
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate;

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
public class Disambiguation implements Serializable {

    @Expose
    private List<DisambiguationDetailsList> disambiguationDetailsList = new ArrayList<DisambiguationDetailsList>();
    @Expose
    private String service;
    @Expose
    private Request request;

    /**
     *
     * @return
     */
    public List<DisambiguationDetailsList> getDisambiguationDetailsList() {
        return disambiguationDetailsList;
    }

    /**
     *
     * @param disambiguationDetailsList
     */
    public void setDisambiguationDetailsList(List<DisambiguationDetailsList> disambiguationDetailsList) {
        this.disambiguationDetailsList = disambiguationDetailsList;
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
