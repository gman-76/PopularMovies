package com.projects.gerhardschoeman.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState==null){
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.DETAIL_ARGS_URI_KEY, getIntent().getData());
            DetailActivityFragment df = new DetailActivityFragment();
            df.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_fragment_container,df,null)
                    .commit();
        }
    }

}
