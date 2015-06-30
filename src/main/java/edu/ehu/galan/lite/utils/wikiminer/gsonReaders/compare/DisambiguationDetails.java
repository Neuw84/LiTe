
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.compare;

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
public class DisambiguationDetails {

    private Integer term1Candidates;
    private Integer term2Candidates;
    private List<Interpretation> interpretations = new ArrayList<>();
    private final Map<String, Object> additionalProperties = new HashMap<>();

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
