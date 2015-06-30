
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
public class DisambiguationDetailsList implements Serializable{
    @Expose
    private String term1;
    @Expose
    private String term2;
    @Expose
    private Integer term1Candidates;
    @Expose
    private Integer term2Candidates;
    @Expose
    private List<Interpretation> interpretations = new ArrayList<Interpretation>();

    /**
     *
     * @return
     */
    public Integer getTerm1Candidates() {
        return term1Candidates;
    }

    /**
     *
     * @param term1Candidates
     */
    public void setTerm1Candidates(Integer term1Candidates) {
        this.term1Candidates = term1Candidates;
    }

    /**
     *
     * @return
     */
    public Integer getTerm2Candidates() {
        return term2Candidates;
    }

    /**
     *
     * @param term2Candidates
     */
    public void setTerm2Candidates(Integer term2Candidates) {
        this.term2Candidates = term2Candidates;
    }

    /**
     *
     * @return
     */
    public List<Interpretation> getInterpretations() {
        return interpretations;
    }

    /**
     *
     * @param interpretations
     */
    public void setInterpretations(List<Interpretation> interpretations) {
        this.interpretations = interpretations;
    }

    /**
     * @return the term1
     */
    public String getTerm1() {
        return term1;
    }

    /**
     * @param term1 the term1 to set
     */
    public void setTerm1(String term1) {
        this.term1 = term1;
    }

    /**
     * @return the term2
     */
    public String getTerm2() {
        return term2;
    }

    /**
     * @param term2 the term2 to set
     */
    public void setTerm2(String term2) {
        this.term2 = term2;
    }

}
