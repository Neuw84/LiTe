
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.disambiguate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("com.googlecode.jsonschema2pojo")
public class DisamList implements Serializable {
    private static final long serialVersionUID = 1L;

    private String term2;
    private String term1;
    private Integer term1Candidates;
    private Integer term2Candidates;
    private List<List<Interpretation>> interpretations = new ArrayList<>();
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public String getTerm2() {
        return term2;
    }

    public void setTerm2(String term2) {
        this.term2 = term2;
    }

    public String getTerm1() {
        return term1;
    }

    public void setTerm1(String term1) {
        this.term1 = term1;
    }

    public Integer getTerm1Candidates() {
        return term1Candidates;
    }

    public void setTerm1Candidates(Integer term1Candidates) {
        this.term1Candidates = term1Candidates;
    }

    public Integer getTerm2Candidates() {
        return term2Candidates;
    }

    public void setTerm2Candidates(Integer term2Candidates) {
        this.term2Candidates = term2Candidates;
    }

    public List<List<Interpretation>> getInterpretations() {
        return interpretations;
    }

    public void setInterpretations(List<List<Interpretation>> interpretations) {
        this.interpretations = interpretations;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
