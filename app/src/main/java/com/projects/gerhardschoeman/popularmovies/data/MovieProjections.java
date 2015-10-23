package com.projects.gerhardschoeman.popularmovies.data;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieEntry;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieTrailers;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieReviews;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class MovieProjections {
    public static final class ALL_COLUMNS{
        public static final String[] COLUMNS = {
                MovieEntry._ID,
                MovieEntry.MOVIE_NAME,
                MovieEntry.MOVIE_BACKDROP,
                MovieEntry.MOVIE_POSTER,
                MovieEntry.MOVIE_RELEASEDATE,
                MovieEntry.MOVIE_VOTES,
                MovieEntry.MOVIE_RATING,
                MovieEntry.MOVIE_OVERVIEW,
                MovieEntry.MOVIE_POPULARITY,
                MovieEntry.MOVIE_FAVOURITE,
                MovieEntry.MOVIE_TRAILERS,
                MovieEntry.MOVIE_REVIEWS
        };
        public static final int ID = 0;
        public static final int NAME = 1;
        public static final int BACKDROP = 2;
        public static final int POSTER = 3;
        public static final int RELEASEDATE = 4;
        public static final int VOTES = 5;
        public static final int RATING = 6;
        public static final int OVERVIEW = 7;
        public static final int POPULARITY = 8;
        public static final int FAVOURITE = 9;
        public static final int TRAILERS = 10;
        public static final int REVIEWS = 11;
    }

    public static final class TRAILER_ALL_COLUMNS{
        public static final String[] COLUMNS = {
                MovieTrailers._ID,
                MovieTrailers.NAME,
                MovieTrailers.TYPE,
                MovieTrailers.URL
        };
        public static final int ID = 0;
        public static final int NAME = 1;
        public static final int TYPE = 2;
        public static final int URL = 3;
    }

    public static final class REVIEW_ALL_COLUMNS{
        public static final String[] COLUMNS = {
            MovieReviews._ID,
            MovieReviews.AUTHOR,
            MovieReviews.BRIEF,
            MovieReviews.URL
        };
        public static final int ID = 0;
        public static final int AUTHOR = 1;
        public static final int BRIEF = 2;
        public static final int URL = 3;
    }
}
