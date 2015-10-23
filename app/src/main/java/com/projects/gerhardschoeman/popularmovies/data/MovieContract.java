package com.projects.gerhardschoeman.popularmovies.data;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import com.projects.gerhardschoeman.popularmovies.Utils;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.projects.gerhardschoeman.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "moviequery";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final class MovieTrailers implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static Uri buildUriFromMovieID(long id){
            return CONTENT_URI.buildUpon().appendQueryParameter(MovieEntry.QUERY_ID_PARAM,Long.toString(id)).build();
        }

        public static final String TABLE_NAME = "TRAILERS";
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String URL = "url";
    }

    public static final class MovieReviews implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static Uri buildUriFromMovieID(long id){
            return CONTENT_URI.buildUpon().appendQueryParameter(MovieEntry.QUERY_ID_PARAM,Long.toString(id)).build();
        }

        public static final String TABLE_NAME = "REVIEWS";
        public static final String AUTHOR = "author";
        public static final String BRIEF = "brief";
        public static final String URL = "url";
    }

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE  + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String QUERY_ID_PARAM = "id";
        public static final String QUERY_TITLE_PARAM = "title";

        public static final Uri buildUriFromID(long id){
            return CONTENT_URI.buildUpon().appendQueryParameter(QUERY_ID_PARAM,String.format("%d",id)).build();
        }
        public static final Uri buildUriFromTitle(String title){
            return CONTENT_URI.buildUpon().appendQueryParameter(QUERY_TITLE_PARAM,title).build();
        }

        public static final String TABLE_NAME = "MOVIES";

        public static final String MOVIE_NAME = "title";
        public static final String MOVIE_BACKDROP = "backdropImage";
        public static final String MOVIE_POSTER = "posterImage";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_RATING = "rating";
        public static final String MOVIE_VOTES = "votes";
        public static final String MOVIE_RELEASEDATE = "releaseDate";
        public static final String MOVIE_POPULARITY = "popularity";
        public static final String MOVIE_FAVOURITE = "isFavourite";
        public static final String MOVIE_TRAILERS = "trailers";
        public static final String MOVIE_REVIEWS = "reviews";

        public static String getSortOrderSelection(Context context){
            Utils.SORTORDER so = Utils.getPreferredSortOrder(context);
            switch(so){
                case POP_ASC: return MOVIE_POPULARITY + " ASC";
                case POP_DESC: return MOVIE_POPULARITY + " DESC";
                case RATE_ASC: return MOVIE_RATING + " ASC";
                case RATE_DESC: return MOVIE_RATING + " DESC";
            }
            return null;
        }
    }
}
