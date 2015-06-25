
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.listRelate;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

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

    public Integer getLowId() {
        return lowId;
    }

    public void setLowId(Integer lowId) {
        this.lowId = lowId;
    }

    public String getLowTitle() {
        return lowTitle;
    }

    public void setLowTitle(String lowTitle) {
        this.lowTitle = lowTitle;
    }

    public Integer getHighId() {
        return highId;
    }

    public void setHighId(Integer highId) {
        this.highId = highId;
    }

    public String getHighTitle() {
        return highTitle;
    }

    public void setHighTitle(String highTitle) {
        this.highTitle = highTitle;
    }

    public Double getRelatedness() {
        return relatedness;
    }

    public void setRelatedness(Double relatedness) {
        this.relatedness = relatedness;
    }

}
