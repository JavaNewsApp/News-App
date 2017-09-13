package com.example.ForMoreNews.bigproject;


import android.util.Log;

import java.util.ArrayList;

/**
* Created by ihandysoft on 16/8/23.
*/

public class New {

    private String lang_Type;
    private String newsClassTag;
    private String news_Author;
    private String news_ID;
    private String news_Pictures;
    private String news_Source;
    private String news_Time;
    private String news_Title;
    private String news_URL;
    private String news_Video;
    private String news_Intro;
    private String forSearch;
    private boolean liked;
    private boolean clicked = false;
    private String body = "";
    private ArrayList<String> name = new ArrayList<>();

    public String getPostid() {
        return news_ID;
    }

    public String getCategory() {
        return newsClassTag;
    }

    public String getDigest() {
        return news_Intro;
    }

    public String getTitle() {
        return news_Title;
    }

    public void setTitle(String news_Title) {
        this.news_Title = news_Title;
    }

    public String getLtitle() {
        return "";
    }

    public String getUrl() {
        return news_URL;
    }

    public String getSource() {
        return news_Author;
    }

    public void setSource(String news_Author) {
        this.news_Author = news_Author;
    }

    public String getImgsrc() {
        return news_Pictures.split(";")[0].split("%20")[0];
    }

    public String getPtime() {
        return news_Time;
    }

    public boolean getIsLiked() {
        return liked;
    }

    public boolean getIsClicked() {
        return clicked;
    }

    public void setIsClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setSearch(String search) {
        this.forSearch = search;

    }

    public String getSearch() {
        return this.forSearch;
    }

    public void setBody(String body) {
        this.body = body;

    }

    public String getBody() {
        return this.body;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setPtime(String news_Time) {
        this.news_Time = news_Time;
    }

    public void setIsLiked(boolean like) {
        this.liked = like;
    }
    public void add(String title, String origin, String image, String id, String category, String source, String body, String like, String name, String click) {
        this.news_Pictures = image;
        this.news_Title = title;
        this.news_Author = origin;
        this.news_ID = id;
        this.newsClassTag = category;
        this.news_URL = source;
        this.body = body;
        this.liked = Boolean.parseBoolean(like);
        this.clicked = Boolean.parseBoolean(click);
        String [] _name = name.split(";");
        for(int i = 0; i < _name.length; i++){
            this.name.add(_name[i]);
        }
    }
}


