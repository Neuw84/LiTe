
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.markUp;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Request {

    @Expose
    private String images;
    @Expose
    private String markUp;
    @Expose
    private String ids;
    @Expose
    private String responseFormat;
    @Expose
    private String wikipedia;

    /**
     * 
     * @return
     *     The images
     */
    public String getImages() {
        return images;
    }

    /**
     * 
     * @param images
     *     The images
     */
    public void setImages(String images) {
        this.images = images;
    }

    /**
     * 
     * @return
     *     The markUp
     */
    public String getMarkUp() {
        return markUp;
    }

    /**
     * 
     * @param markUp
     *     The markUp
     */
    public void setMarkUp(String markUp) {
        this.markUp = markUp;
    }

    /**
     * 
     * @return
     *     The ids
     */
    public String getIds() {
        return ids;
    }

    /**
     * 
     * @param ids
     *     The ids
     */
    public void setIds(String ids) {
        this.ids = ids;
    }

    /**
     * 
     * @return
     *     The responseFormat
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * 
     * @param responseFormat
     *     The responseFormat
     */
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    /**
     * 
     * @return
     *     The wikipedia
     */
    public String getWikipedia() {
        return wikipedia;
    }

    /**
     * 
     * @param wikipedia
     *     The wikipedia
     */
    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

}
