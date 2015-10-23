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
public class TrailerAdapter extends CursorAdapter {
    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View nv = LayoutInflater.from(context).inflate(R.layout.trailer_list_view,parent,false);
        TrailerListViewHolder tvh = new TrailerListViewHolder(nv);
        nv.setTag(tvh);
        return nv;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerListViewHolder tvh = (TrailerListViewHolder)view.getTag();
        String type = cursor.getString(MovieProjections.TRAILER_ALL_COLUMNS.TYPE);
        if(type.equals("youtube")){
            tvh.trailerImage.setImageResource(R.drawable.youtube);
        }
        tvh.trailerName.setText(cursor.getString(MovieProjections.TRAILER_ALL_COLUMNS.NAME));
    }
}
