package com.projects.gerhardschoeman.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieTrailers;
import com.projects.gerhardschoeman.popularmovies.data.MovieContract.MovieReviews;

/**
 * Created by Gerhard on 05/10/2015.
 */
public class MovieDBQueryTask extends AsyncTask<Void,Void,Void> {

    private final String LOGTAG=this.getClass().getSimpleName();

    MovieCellAdapter cellAdapter = null;
    Context context;
    ProgressBar loading;
    int pageToLoad;

    MovieDBQueryTask(Context c, MovieCellAdapter ad,ProgressBar p,int page){
        context = c;
        cellAdapter = ad;
        loading = p;
        pageToLoad=page;
    }

    private String getDiscoverURL(){
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("api.themoviedb.org");
        uri.appendPath("3");
        uri.appendPath("discover");
        uri.appendPath("movie");
        uri.appendQueryParameter("api_key",BuildConfig.MOVIE_DB_API_KEY);
        String sort = Utils.getPreferredSortOrderValue(context);
        uri.appendQueryParameter("sort_by",sort);
        if(sort.equals("vote_average.desc") || sort.equals("vote_average.asc")){
            uri.appendQueryParameter("vote_count.gte","50");
        }
        if(pageToLoad>0){
            boolean loaded=false;
            for (int i=0;!loaded && i<cellAdapter.pagesLoaded.size();++i) {
                loaded = cellAdapter.pagesLoaded.get(i)==pageToLoad;
            }
            if(loaded) return null;
            if(pageToLoad>1000 || pageToLoad>cellAdapter.pageCount) return null; //api restriction
            uri.appendQueryParameter("page",Integer.toString(pageToLoad));
        }else{
            cellAdapter.pagesLoaded.clear();
        }
        return uri.build().toString();
    }

    private String getTrailerURL(long id){
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("api.themoviedb.org");
        uri.appendPath("3");
        uri.appendPath("movie");
        uri.appendPath(Long.toString(id));
        uri.appendPath("trailers");
        uri.appendQueryParameter("api_key",BuildConfig.MOVIE_DB_API_KEY);
        return uri.build().toString();
    }

    private String getReviewURL(long id,int page){
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("api.themoviedb.org");
        uri.appendPath("3");
        uri.appendPath("movie");
        uri.appendPath(Long.toString(id));
        uri.appendPath("reviews");
        uri.appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
        uri.appendQueryParameter("page",Integer.toString(page));
        return uri.build().toString();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = getDiscoverURL();
        Log.d(LOGTAG, url);
        String json = HttpGETJSONResponse(url);
        Log.d(LOGTAG, json);
        if(json.length()<1) return null;
        try{
            JSONObject jsonRoot = new JSONObject(json);
            cellAdapter.currentPage = jsonRoot.getInt("page");
            cellAdapter.pageCount = jsonRoot.getInt("total_pages");
            cellAdapter.pagesLoaded.add(cellAdapter.currentPage);
            JSONArray results = jsonRoot.getJSONArray("results");
            if(results!=null){
                ArrayList<ContentValues> movies = new ArrayList<>();
                ArrayList<ContentValues> trailers = new ArrayList<>();
                ArrayList<ContentValues> reviews = new ArrayList<>();
                for (int i=0;i<results.length();++i) {
                    JSONObject result = results.getJSONObject(i);
                    Movie movie = new Movie(result);
                    //get trailers
                    String trailerJSON = HttpGETJSONResponse(getTrailerURL(movie.id));
                    if(trailerJSON!=null && trailerJSON.length()>0) {
                        JSONObject trailerList = new JSONObject(trailerJSON);
                        JSONArray youtube = trailerList.getJSONArray("youtube");
                        if(youtube!=null){
                            for(int j=0;j<youtube.length();++j){
                                JSONObject trailer = youtube.getJSONObject(j);
                                ContentValues trailerCV = new ContentValues();
                                trailerCV.put(MovieTrailers._ID,movie.id);
                                trailerCV.put(MovieTrailers.NAME,trailer.getString("name"));
                                trailerCV.put(MovieTrailers.TYPE,"youtube");
                                trailerCV.put(MovieTrailers.URL,trailer.getString("source"));
                                trailers.add(trailerCV);
                                movie.trailers++;
                            }
                        }
                    }
                    //get reviews
                    String reviewJSON = HttpGETJSONResponse(getReviewURL(movie.id,1));
                    if(reviewJSON!=null && reviewJSON.length()>0) {
                        JSONObject reviewList = new JSONObject(reviewJSON);
                        movie.reviews = reviewList.getInt("total_results");
                        int thisPage=1;
                        int pages = reviewList.getInt("total_pages");
                        do{
                            JSONArray reviewArray = reviewList.getJSONArray("results");
                            for(int j=0;j<reviewArray.length();++j){
                                JSONObject review = reviewArray.getJSONObject(j);
                                ContentValues reviewCV = new ContentValues();
                                reviewCV.put(MovieReviews._ID,movie.id);
                                reviewCV.put(MovieReviews.AUTHOR,review.getString("author"));
                                String content = review.getString("content");
                                if(content.length()>50){
                                    content = content.substring(0,46) + "...";
                                }
                                reviewCV.put(MovieReviews.BRIEF,content);
                                reviewCV.put(MovieReviews.URL,review.getString("url"));
                                reviews.add(reviewCV);
                            }
                            thisPage++;
                            reviewJSON = HttpGETJSONResponse(getReviewURL(movie.id,thisPage));
                            reviewList = new JSONObject(reviewJSON);
                        }while(thisPage<=pages);
                    }
                    if(movie!=null) movies.add(movie.getContentValues());
                }
                context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                        movies.toArray(new ContentValues[movies.size()]));
                for(ContentValues cv:movies){
                    context.getContentResolver().delete(MovieTrailers.CONTENT_URI,MovieTrailers._ID+"=?",new String[]{cv.getAsString(MovieTrailers._ID)});
                    context.getContentResolver().delete(MovieReviews.CONTENT_URI,MovieReviews._ID+"=?",new String[]{cv.getAsString(MovieReviews._ID)});
                }
                for(ContentValues cv:trailers){
                    context.getContentResolver().insert(MovieTrailers.CONTENT_URI, cv);
                }
                for(ContentValues cv:reviews){
                    context.getContentResolver().insert(MovieReviews.CONTENT_URI,cv);
                }
            }
        }catch(JSONException e){
            Log.e(LOGTAG,"Unable to parse JSON response: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        loading.setVisibility(ProgressBar.GONE);
        Toast toast = Toast.makeText(context,"Data refreshed",Toast.LENGTH_SHORT);
        toast.show();
    }

    private String HttpGETJSONResponse(String urlStr)
    {
        String json="";
        HttpURLConnection conn = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.connect();
            InputStream is = conn.getInputStream();
            if(is==null){
                Log.w(LOGTAG,"Input stream is null");
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line;
            while((line=bufferedReader.readLine())!=null) buffer.append(line);
            json = buffer.toString();
        }catch(MalformedURLException e){
            Log.e(LOGTAG,"URL not properly formed: " + e.getMessage());
        }catch(IOException e){
            Log.e(LOGTAG,"IOException opening URL: " + e.getMessage());
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch(IOException e){
                    Log.e(LOGTAG,"Unable to close buffered reader: " + e.getMessage());
                }
            }
        }
        return json;
    }
}
