package com.projects.gerhardschoeman.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnItemClickListener{

    private final String LOGTAG = this.getClass().getSimpleName();

    private final String DETAIL_FRAG_TAG = "DFTAG";

    private boolean mTwoPane;

    public static final String DETAIL_ARGS_URI_KEY = "URI_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        mTwoPane=false;
        if(findViewById(R.id.detail_fragment_container)!=null){
            mTwoPane = true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_fragment_container,new DetailActivityFragment(),DETAIL_FRAG_TAG)
                        .commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(Uri uri) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DETAIL_ARGS_URI_KEY,uri);
            DetailActivityFragment df = new DetailActivityFragment();
            df.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,df,DETAIL_FRAG_TAG)
                    .commit();
        }else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}
