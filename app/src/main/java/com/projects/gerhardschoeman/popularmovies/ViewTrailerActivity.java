package com.projects.gerhardschoeman.popularmovies;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;
import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;

public class ViewTrailerActivity extends Activity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOGTAG = this.getClass().getSimpleName();

    private final int LOADER_ID = 1;

    TrailerAdapter ad;

    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trailer);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.5));

        mUri = getIntent().getData();

        ad = new TrailerAdapter(this,null,0);
        ListView lv = (ListView)findViewById(R.id.trailerListView);
        lv.setAdapter(ad);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                if(cursor!=null){
                    String url = cursor.getString(MovieProjections.TRAILER_ALL_COLUMNS.URL);
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("vnd.youtube:" + url));
                    try {
                        startActivity(intent);
                    }catch(ActivityNotFoundException ex) {
                        intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + url));
                        startActivity(intent);
                    }
                }
            }
        });
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.content.CursorLoader(this, mUri,
                MovieProjections.TRAILER_ALL_COLUMNS.COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        ad.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        ad.swapCursor(null);
    }
}
