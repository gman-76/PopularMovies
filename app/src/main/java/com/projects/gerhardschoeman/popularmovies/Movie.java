package com.projects.gerhardschoeman.popularmovies;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Gerhard on 05/10/2015.
 */
public class Movie implements Serializable{

    public String title;
    public long id;
    public String backdrop;
    public String poster;
    public String overview;
    public double rating;
    public long votes;
    public String releaseDate;
    public double popularity;

    Movie(JSONObject json){
        try {
            backdrop = "http://image.tmdb.org/t/p/original" + json.getString("backdrop_path");
            title = json.getString("title");
            id = json.getLong("id");
            poster = "http://image.tmdb.org/t/p/w185" + json.getString("poster_path");
            overview = json.getString("overview");
            rating = json.getDouble("vote_average");
            votes = json.getLong("vote_count");
            releaseDate = json.getString("release_date");
            popularity =json.getDouble("popularity");
        }catch(JSONException e){
            Log.e("MOVIECONST","Unable to create movie from JSON: " + e.getMessage());
        }
    }
}
