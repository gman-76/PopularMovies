package com.projects.gerhardschoeman.popularmovies;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class MovieCellViewHolder {
    public ImageView movieImage;
    public TextView movieTitle;
    public TextView movieSortText;

    public MovieCellViewHolder(View view){
        movieImage = (ImageView)view.findViewById(R.id.imageView);
        movieTitle = (TextView)view.findViewById(R.id.textTitle);
        movieSortText = (TextView)view.findViewById(R.id.textSortSelection);
    }
}
