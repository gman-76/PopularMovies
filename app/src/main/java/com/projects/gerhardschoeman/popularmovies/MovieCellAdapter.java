package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Gerhard on 05/10/2015.
 */
public class MovieCellAdapter extends CursorAdapter {

    //public ArrayList<Movie> movies = new ArrayList<Movie>();
    public int pageCount;
    public int currentPage;
    public ArrayList<Integer> pagesLoaded = new ArrayList<Integer>();

    public MovieCellAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View nv = LayoutInflater.from(context).inflate(R.layout.tile_view,parent,false);
        MovieCellViewHolder vh = new MovieCellViewHolder(nv);
        nv.setTag(vh);
        return nv;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MovieCellViewHolder vh = (MovieCellViewHolder)view.getTag();
        Picasso.with(context).load(cursor.getString(MovieProjections.ALL_COLUMNS.POSTER)).error(R.drawable.noimage).into(vh.movieImage);
        vh.movieTitle.setText(cursor.getString(MovieProjections.ALL_COLUMNS.NAME));
        String sortText = Utils.getPreferrredSortOrderDesc(context) + ": ";
        if(sortText.startsWith("Popularity")) sortText += String.format("%.1f", cursor.getDouble(MovieProjections.ALL_COLUMNS.POPULARITY));
        else if(sortText.startsWith("Rating")) sortText += Double.toString(cursor.getDouble(MovieProjections.ALL_COLUMNS.RATING));
        else sortText = null;
        if(sortText!=null) vh.movieSortText.setText(sortText);
    }

}
