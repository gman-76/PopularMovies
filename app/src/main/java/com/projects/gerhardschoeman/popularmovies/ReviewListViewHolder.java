package com.projects.gerhardschoeman.popularmovies;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Gerhard on 23/10/2015.
 */
public class ReviewListViewHolder {
    public TextView reviewAuthor;
    public TextView reviewDetail;

    public ReviewListViewHolder(View view){
        reviewAuthor = (TextView)view.findViewById(R.id.reviewAuthor);
        reviewDetail = (TextView)view.findViewById(R.id.reviewDetail);
    }
}
