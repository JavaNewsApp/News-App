package com.example.ForMoreNews.bigproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 金子童 on 2017/9/9.
 */

public class NewDetail {

    private ArrayList<des> Keywords;
    //private List<KeyDetail> bagOfWords;
    private String crawl_Source;
    private String crawl_Time;
    private String inborn_KeyWords;
    private String lang_Type;
    private List<des> locations;
    private String newsClassTag;
    private String news_Author;
    private String news_Category;
    private String news_Content;
    private String news_ID;
    private String news_Journal;
    private String news_Pictures;
    private String news_Source;
    private String news_Time;
    private String news_Title;
    private String news_URL;
    private String news_Video;
    private List<des> persons;
    private String repeat_ID;
    //private List<String> seggedPListOfContent;
    private String seggedTitle;
    private String wordCountOfContent;
    private String wordCountOfTitle;


    public String getBody() {
        return news_Content;
    }

    public void setBody(String news_Content) {
        this.news_Content = news_Content;
    }

    public String getShareLink() {
        return news_URL;
    }

    public String getTitle() {
        return news_Title;
    }

    public void setTitle(String news_Title) {
        this.news_Title = news_Title;
    }

    public String getSource() {
        return news_Author;
    }

    public void setSource(String news_Author) {
        this.news_Author = news_Author;
    }

    public String getPtime() {
        return news_Time;
    }

    public String getImg() {
        return news_Pictures.split(";")[0].split("%20")[0];
    }

    public class des {
        String word;
        double score;
    }
    public ArrayList<String> getLink() {
        ArrayList<String> ret = new ArrayList<>();

        for(int i = 0; i < persons.size(); i++){
            ret.add(persons.get(i).word);
        }
        for(int i = 0; i < locations.size(); i++){
            ret.add(locations.get(i).word);
        }

        return ret;
    }
    public String getKeywords() {
        String key = "";
        for(int i = 0; i < Keywords.size() - 1; i++) {
            key += Keywords.get(i).word;
            key += ",";
            key += Keywords.get(i).score;
            key += ";";
        }
        key += Keywords.get(Keywords.size() - 1).word;
        key += ",";
        key += Keywords.get(Keywords.size() - 1).score;
        key += ";";
        return key;
    }

}
