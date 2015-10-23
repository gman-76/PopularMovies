package com.projects.gerhardschoeman.popularmovies;

import android.content.ContentValues;
import android.util.Log;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieEntry;

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
    public boolean isFavourite;
    public int trailers;
    public int reviews;

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
            isFavourite=false;
            trailers=0;
            reviews=0;
        }catch(JSONException e){
            Log.e("MOVIECONST","Unable to create movie from JSON: " + e.getMessage());
        }
    }

    ContentValues getContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry._ID,id);
        cv.put(MovieEntry.MOVIE_NAME,title);
        cv.put(MovieEntry.MOVIE_BACKDROP,backdrop);
        cv.put(MovieEntry.MOVIE_POSTER,poster);
        cv.put(MovieEntry.MOVIE_OVERVIEW,overview);
        cv.put(MovieEntry.MOVIE_RATING,rating);
        cv.put(MovieEntry.MOVIE_VOTES,votes);
        cv.put(MovieEntry.MOVIE_RELEASEDATE,releaseDate);
        cv.put(MovieEntry.MOVIE_POPULARITY,popularity);
        cv.put(MovieEntry.MOVIE_FAVOURITE,isFavourite);
        cv.put(MovieEntry.MOVIE_TRAILERS,trailers);
        cv.put(MovieEntry.MOVIE_REVIEWS,reviews);
        return cv;
    }
}
