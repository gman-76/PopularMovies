package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;

/**
 * Created by Gerhard on 23/10/2015.
 */
public class ReviewAdapter extends CursorAdapter {
    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View nv = LayoutInflater.from(context).inflate(R.layout.review_list_view,parent,false);
        ReviewListViewHolder rh = new ReviewListViewHolder(nv);
        nv.setTag(rh);
        return nv;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ReviewListViewHolder rh = (ReviewListViewHolder)view.getTag();
        rh.reviewAuthor.setText(cursor.getString(MovieProjections.REVIEW_ALL_COLUMNS.AUTHOR));
        rh.reviewDetail.setText(cursor.getString(MovieProjections.REVIEW_ALL_COLUMNS.BRIEF));
    }
}
