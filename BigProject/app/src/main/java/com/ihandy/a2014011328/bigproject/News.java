package com.ihandy.a2014011328.bigproject;

/**
 * Created by ihandysoft on 16/8/23.
 */
public class News {
    private String image;
    private String title;
    private String origin;
    private String id;
    private String source;
    private String category;
    boolean isLiked = false;

    public News(String title, String origin, String image, String id, String category, String source) {
        this.image = image;
        this.title = title;
        this.origin = origin;
        this.id = id;
        this.category = category;
        this.source = source;
    }

    public String getImage() {
        return image;
    }
    public String getTitle() {
        return title;
    }
    public String getOrigin() {
        return origin;
    }
    public String getId() {
        return id;
    }
    public String getSource() {
        return source;
    }
    public String getCategory() {
        return category;
    }
    public boolean getIsLiked(){
        return isLiked;
    }
    public void setIsLiked(boolean s){
        this.isLiked = s;
    }

}
