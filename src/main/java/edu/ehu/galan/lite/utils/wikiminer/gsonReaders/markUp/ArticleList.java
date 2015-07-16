
package edu.ehu.galan.lite.utils.wikiminer.gsonReaders.markUp;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class ArticleList {

    @Expose
    private Integer id;
    @Expose
    private String title;
    @Expose
    private List<Image> images = new ArrayList<Image>();
    @Expose
    private String markup;

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The images
     */
    public List<Image> getImages() {
        return images;
    }

    /**
     * 
     * @param images
     *     The images
     */
    public void setImages(List<Image> images) {
        this.images = images;
    }

    /**
     * 
     * @return
     *     The markup
     */
    public String getMarkup() {
        return markup;
    }

    /**
     * 
     * @param markup
     *     The markup
     */
    public void setMarkup(String markup) {
        this.markup = markup;
    }

}
