package com.projects.gerhardschoeman.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieEntry;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieTrailers;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieReviews;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class MovieProvider extends ContentProvider{

    private final String LOGTAG = this.getClass().getSimpleName();

    private MovieDBHelper dbHelper;

    private static final int ALLMOVIES = 0;
    private static final int MOVIETITLE = 1;
    private static final int MOVIEID = 2;
    private static final int MOVIETRAILERS = 3;
    private static final int MOVIEREVIEWS = 4;

    private static class MyUriMatcher extends UriMatcher{
        public MyUriMatcher(int code) {
            super(code);
        }
        @Override
        public int match(Uri uri) {
            int m = super.match(uri);
            switch(m){
                case ALLMOVIES:
                    if(uri.getQueryParameter(MovieEntry.QUERY_ID_PARAM)!=null){
                        return MOVIEID;
                    }else if(uri.getQueryParameter(MovieEntry.QUERY_TITLE_PARAM)!=null){
                        return MOVIETITLE;
                    }
                    return ALLMOVIES;
                case MOVIETRAILERS:
                case MOVIEREVIEWS:
                    return m;
            }
            return UriMatcher.NO_MATCH;
        }
    }
    private static MyUriMatcher uriMatcher;
    static{
        uriMatcher = new MyUriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE,ALLMOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER,MOVIETRAILERS);
        uriMatcher.addURI(authority,MovieContract.PATH_REVIEW,MOVIEREVIEWS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match){
            case ALLMOVIES: return MovieEntry.CONTENT_TYPE;
            case MOVIETITLE: return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIEID: return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIETRAILERS: return MovieTrailers.CONTENT_TYPE;
            case MOVIEREVIEWS: return MovieReviews.CONTENT_TYPE;
        }
        throw new UnsupportedOperationException("Unknown uri can't get type: " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOGTAG,"Query received in provider for: " + uri);
        int match = uriMatcher.match(uri);
        Cursor retCursor = null;
        switch (match){
            case ALLMOVIES: {
                retCursor = dbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

            }
            break;
            case MOVIETITLE:{
                selection = MovieEntry.MOVIE_NAME + "=?";
                selectionArgs = new String[]{uri.getQueryParameter(MovieEntry.QUERY_TITLE_PARAM)};
                retCursor = dbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;
            case MOVIEID:{
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{uri.getQueryParameter(MovieEntry.QUERY_ID_PARAM)};
                retCursor = dbHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;
            case MOVIETRAILERS:{
                String id = uri.getQueryParameter(MovieEntry.QUERY_ID_PARAM);
                if(id!=null){
                    selection = MovieTrailers._ID + "=?";
                    selectionArgs = new String[]{id};
                    retCursor = dbHelper.getReadableDatabase().query(MovieTrailers.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,null,null);
                }
            }
            break;
            case MOVIEREVIEWS:{
                String id = uri.getQueryParameter(MovieEntry.QUERY_ID_PARAM);
                if(id!=null){
                    selection = MovieReviews._ID + "=?";
                    selectionArgs = new String[]{id};
                    retCursor = dbHelper.getReadableDatabase().query(MovieReviews.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,null,null);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri for query: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        Uri retUri = null;
        switch (match){
            case MOVIEID:{
                long ret = dbHelper.getWritableDatabase().insert(MovieEntry.TABLE_NAME,null,values);
                if(ret>0){
                    retUri = MovieEntry.buildUriFromID(ret);
                }else{
                    throw new android.database.SQLException("Failed to insert row into movies: " + uri);
                }
            }
            break;
            case MOVIETRAILERS:{
                long ret = dbHelper.getWritableDatabase().insert(MovieTrailers.TABLE_NAME,null,values);
                if(ret>0){
                    retUri = uri;
                }else{
                    throw new android.database.SQLException("Failed to insert row into trailers: " + uri);
                }
            }
            break;
            case MOVIEREVIEWS:{
                long ret = dbHelper.getWritableDatabase().insert(MovieReviews.TABLE_NAME,null,values);
                if(ret>0){
                    retUri = uri;
                }else{
                    throw new android.database.SQLException("Failed to insert row into reviews: " + uri);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri for insert: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int recsInsertedUpdated = 0;
        switch (match){
            case ALLMOVIES:{
                db.beginTransaction();
                try{
                    for (ContentValues cv:values) {
                        Cursor cursor = db.query(MovieEntry.TABLE_NAME,
                                new String[]{MovieEntry._ID},
                                MovieEntry._ID+"=?",
                                new String[]{cv.getAsString(MovieEntry._ID)},
                                null,null,null);
                        long ret = 0;
                        if(cursor!=null && cursor.moveToFirst()){
                            ret = db.update(MovieEntry.TABLE_NAME,cv,MovieEntry._ID + "=?",new String[]{cv.getAsString(MovieEntry._ID)});
                        }else {
                            ret = db.insert(MovieEntry.TABLE_NAME, null, cv);
                        }
                        if (ret > 0) {
                            recsInsertedUpdated++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri for bulk insert: " + uri);
        }
        Log.d(LOGTAG,"Bulk insert/update affected " + Integer.toString(recsInsertedUpdated) + " records");
        if(recsInsertedUpdated>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return recsInsertedUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if(selection==null) selection="1";
        int recsdeleted = 0;
        switch (match){
            case ALLMOVIES:{
                recsdeleted = dbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,null,null);
            }
            break;
            case MOVIEID:{
                recsdeleted = dbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,selection,selectionArgs);
            }
            break;
            case MOVIETRAILERS:{
                recsdeleted = dbHelper.getWritableDatabase().delete(MovieTrailers.TABLE_NAME,selection,selectionArgs);
            }
            break;
            case MOVIEREVIEWS:{
                recsdeleted = dbHelper.getWritableDatabase().delete(MovieReviews.TABLE_NAME,selection,selectionArgs);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri for delete: " + uri);
        }
        if(recsdeleted>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return recsdeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        int recsupdated = 0;
        switch(match){
            case MOVIEID:{
                recsupdated = dbHelper.getWritableDatabase().update(MovieEntry.TABLE_NAME,values,selection,selectionArgs);
            }
            break;
            case MOVIETRAILERS:{
                recsupdated = dbHelper.getWritableDatabase().update(MovieTrailers.TABLE_NAME,values,selection,selectionArgs);
            }
            break;
            case MOVIEREVIEWS:{
                recsupdated = dbHelper.getWritableDatabase().update(MovieReviews.TABLE_NAME,values,selection,selectionArgs);
            }
            break;
            default:
                throw new UnsupportedOperationException("Unknown uri for update: " + uri);
        }
        if(recsupdated>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return recsupdated;
    }


}
