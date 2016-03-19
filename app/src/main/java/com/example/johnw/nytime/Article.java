package com.example.johnw.nytime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by johnw on 3/19/2016.
 */
public class Article implements Serializable {


    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWeburl() {
        return webUrl;
    }
    String webUrl;
    String thumbnail;
    String headline;

    public Article (JSONObject jsonObject){
        try{
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length()>0){
                JSONObject multimediaJSOn = multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/"+multimediaJSOn.getString("url");
            }
            else {
                this.thumbnail = "";
            }
        }catch(JSONException e){e.printStackTrace();}

    }

    public static ArrayList<Article> fromJSONArray(JSONArray array){
        ArrayList<Article> result = new ArrayList<>();
        for (int i=0;i<array.length();i++){
            try{
                result.add(new Article(array.getJSONObject(i)));
            }catch (JSONException e){e.printStackTrace();}
        }
        return result;
    }


}