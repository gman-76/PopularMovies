package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

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

/**
 * Created by Gerhard on 05/10/2015.
 */
public class MovieDBQueryTask extends AsyncTask<Void,Void,Movie[]> {

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

    @Override
    protected Movie[] doInBackground(Void... params) {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("api.themoviedb.org");
        uri.appendPath("3");
        uri.appendPath("discover");
        uri.appendPath("movie");
        uri.appendQueryParameter("api_key",BuildConfig.MOVIE_DB_API_KEY);
        String sort = Utils.getPreferredSortOrder(context);
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
        String url = uri.build().toString();
        Log.d(LOGTAG,url);
        String json = HttpGETJSONResponse(url);
        Log.d(LOGTAG,json);
        if(json.length()<1) return null;
        ArrayList<Movie> movies = new ArrayList<>();
        try{
            JSONObject jsonRoot = new JSONObject(json);
            cellAdapter.currentPage = jsonRoot.getInt("page");
            cellAdapter.pageCount = jsonRoot.getInt("total_pages");
            cellAdapter.pagesLoaded.add(cellAdapter.currentPage);
            JSONArray results = jsonRoot.getJSONArray("results");
            if(results!=null){
                for (int i=0;i<results.length();++i) {
                    JSONObject result = results.getJSONObject(i);
                    Movie movie = new Movie(result);
                    if(movie!=null) movies.add(movie);
                }
            }
        }catch(JSONException e){
            Log.e(LOGTAG,"Unable to parse JSON response: " + e.getMessage());
        }
        return movies.toArray(new Movie[movies.size()]);
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        if(movies==null) return;
        for(Movie m : movies){
            cellAdapter.movies.add(m);
        }
        cellAdapter.notifyDataSetChanged();
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
