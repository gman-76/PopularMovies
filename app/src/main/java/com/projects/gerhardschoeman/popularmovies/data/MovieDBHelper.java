package com.projects.gerhardschoeman.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieEntry;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieTrailers;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieReviews;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql_create_movies_table = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.MOVIE_NAME + " TEXT," +
                MovieEntry.MOVIE_BACKDROP + " TEXT," +
                MovieEntry.MOVIE_POSTER + " TEXT," +
                MovieEntry.MOVIE_OVERVIEW + " TEXT," +
                MovieEntry.MOVIE_RATING + " REAL," +
                MovieEntry.MOVIE_VOTES + " INTEGER," +
                MovieEntry.MOVIE_RELEASEDATE + " TEXT," +
                MovieEntry.MOVIE_POPULARITY + " REAL," +
                MovieEntry.MOVIE_FAVOURITE + " INTEGER," +
                MovieEntry.MOVIE_TRAILERS + " INTEGER," +
                MovieEntry.MOVIE_REVIEWS + " INTEGER)";
        db.execSQL(sql_create_movies_table);

        final String sql_create_trailers = "CREATE TABLE " + MovieTrailers.TABLE_NAME + "( " +
                MovieTrailers._ID + " INTEGER NOT NULL," +
                MovieTrailers.NAME + " TEXT," +
                MovieTrailers.TYPE + " TEXT," +
                MovieTrailers.URL + " TEXT," +
                "FOREIGN KEY (" + MovieTrailers._ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + "))";
        db.execSQL(sql_create_trailers);

        final String sql_create_reviews = "CREATE TABLE " + MovieReviews.TABLE_NAME + "( " +
                MovieReviews._ID + " INTEGER NOT NULL," +
                MovieReviews.AUTHOR + " TEXT," +
                MovieReviews.BRIEF + " TEXT," +
                MovieReviews.URL + " TEXT," +
                "FOREIGN KEY (" + MovieReviews._ID + ") REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + "))";
        db.execSQL(sql_create_reviews);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieTrailers.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieReviews.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if(!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
