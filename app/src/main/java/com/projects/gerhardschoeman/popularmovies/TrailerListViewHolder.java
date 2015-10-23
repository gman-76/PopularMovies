package com.projects.gerhardschoeman.popularmovies;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gerhard on 23/10/2015.
 */
public class TrailerListViewHolder {
    public ImageView trailerImage;
    public TextView trailerName;

    public TrailerListViewHolder(View view){
        trailerImage = (ImageView)view.findViewById(R.id.trailerImage);
        trailerName = (TextView)view.findViewById(R.id.trailerName);
    }
}
