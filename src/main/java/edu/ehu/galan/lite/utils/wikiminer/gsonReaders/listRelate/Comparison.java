
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 *
 * @author angel
 */
@Generated("com.googlecode.jsonschema2pojo")
public class Comparison implements Serializable{

    @Expose
    private Integer lowId;
    @Expose
    private String lowTitle;
    @Expose
    private Integer highId;
    @Expose
    private String highTitle;
    @Expose
    private Double relatedness;

    /**
     *
     * @return
     */
    public Integer getLowId() {
        return lowId;
    }

    /**
     *
     * @param lowId
     */
    public void setLowId(Integer lowId) {
        this.lowId = lowId;
    }

    /**
     *
     * @return
     */
    public String getLowTitle() {
        return lowTitle;
    }

    /**
     *
     * @param lowTitle
     */
    public void setLowTitle(String lowTitle) {
        this.lowTitle = lowTitle;
    }

    /**
     *
     * @return
     */
    public Integer getHighId() {
        return highId;
    }

    /**
     *
     * @param highId
     */
    public void setHighId(Integer highId) {
        this.highId = highId;
    }

    /**
     *
     * @return
     */
    public String getHighTitle() {
        return highTitle;
    }

    /**
     *
     * @param highTitle
     */
    public void setHighTitle(String highTitle) {
        this.highTitle = highTitle;
    }

    /**
     *
     * @return
     */
    public Double getRelatedness() {
        return relatedness;
    }

    /**
     *
     * @param relatedness
     */
    public void setRelatedness(Double relatedness) {
        this.relatedness = relatedness;
    }

}
