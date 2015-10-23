package com.projects.gerhardschoeman.popularmovies;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class DetailViewHolder {

    public TextView movieTitle;
    public TextView movieRating;
    public TextView movieReleaseDate;
    public ImageView moviePoster;
    public TextView movieOverview;
    public RatingBar movieRatingBar;
    public TextView movieTrailers;
    public TextView movieReviews;
    public ImageView trailersView;
    public ImageView reviewsView;

    public DetailViewHolder(View view){
        movieTitle = (TextView)view.findViewById(R.id.detailTitle);
        movieRating = (TextView)view.findViewById(R.id.rating);
        movieReleaseDate = (TextView)view.findViewById(R.id.releasedate);
        moviePoster = (ImageView)view.findViewById(R.id.imgBackground);
        movieOverview = (TextView)view.findViewById(R.id.txtOverview);
        movieRatingBar = (RatingBar)view.findViewById(R.id.rating_bar);
        movieTrailers = (TextView)view.findViewById(R.id.trailersText);
        movieReviews = (TextView)view.findViewById(R.id.reviewsText);
        trailersView = (ImageView)view.findViewById(R.id.trailersView);
        reviewsView = (ImageView)view.findViewById(R.id.reviewsView);
    }
}
