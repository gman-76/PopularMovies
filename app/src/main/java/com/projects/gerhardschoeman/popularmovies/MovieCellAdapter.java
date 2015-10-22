package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Gerhard on 05/10/2015.
 */
public class MovieCellAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater inflater;
    public ArrayList<Movie> movies = new ArrayList<Movie>();
    public int pageCount;
    public int currentPage;
    public ArrayList<Integer> pagesLoaded = new ArrayList<Integer>();

    MovieCellAdapter(Context c,LayoutInflater i){
        mContext = c;
        inflater = i;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        if(convertView==null){
            ret = inflater.inflate(R.layout.tile_view,null);
        }
        ImageView img = (ImageView)ret.findViewById(R.id.imageView);
        TextView title = (TextView)ret.findViewById(R.id.textTitle);
        TextView sort = (TextView)ret.findViewById(R.id.textSortSelection);
        String sortText = Utils.getPreferrredSortOrderDesc(mContext) + ": ";
        Movie m = (Movie)getItem(position);
        title.setText(m.title);
        //title.setGravity(Gravity.CENTER_HORIZONTAL);
        if(sortText.startsWith("Popularity")) sortText += String.format("%.1f",m.popularity);
        else if(sortText.startsWith("Rating")) sortText += Double.toString(m.rating);
        else sortText = null;
        if(sortText!=null) sort.setText(sortText);
        Picasso.with(mContext).load(m.poster).error(R.drawable.noimage).into(img);
        return ret;
    }

}
