package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;
import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Gerhard on 05/10/2015.
 */
public class MovieCellAdapter extends RecyclerView.Adapter<MovieCellViewHolder> implements MovieCellViewHolder.onClickCallback{

    public int pageCount;
    public int currentPage;
    public ArrayList<Integer> pagesLoaded = new ArrayList<Integer>();

    private Context mContext;
    private Cursor mCursor;

    public interface ClickHandler{
        void onClick(Uri uri);
    }

    private ClickHandler clickHandler;

    @Override
    public void onViewHolderClick(int cursorPosition) {
        mCursor.moveToPosition(cursorPosition);
        clickHandler.onClick(MovieContract.MovieEntry.buildUriFromID(mCursor.getInt(MovieProjections.ALL_COLUMNS.ID)));
    }

    public MovieCellAdapter(Context context,ClickHandler cH) {
        mContext = context;
        clickHandler = cH;
    }

    @Override
    public int getItemCount() {
        if(mCursor==null) return 0;
        return mCursor.getCount();
    }

    @Override
    public MovieCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View nv = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_view, parent, false);
        return new MovieCellViewHolder(nv,this);
    }

    @Override
    public void onBindViewHolder(MovieCellViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Picasso.with(mContext).load(mCursor.getString(MovieProjections.ALL_COLUMNS.POSTER)).error(R.drawable.noimage).into(holder.movieImage);
        holder.movieTitle.setText(mCursor.getString(MovieProjections.ALL_COLUMNS.NAME));
        String sortText = Utils.getPreferrredSortOrderDesc(mContext) + ": ";
        if(sortText.startsWith("Popularity")) sortText += String.format("%.1f", mCursor.getDouble(MovieProjections.ALL_COLUMNS.POPULARITY));
        else if(sortText.startsWith("Rating")) sortText += Double.toString(mCursor.getDouble(MovieProjections.ALL_COLUMNS.RATING));
        else if(sortText.startsWith("Fav")){
            sortText = "";
        }
        else sortText = null;
        if(sortText!=null) holder.movieSortText.setText(sortText);
    }

    public void swapCursor(Cursor c){
        mCursor = c;
        notifyDataSetChanged();
    }

}
