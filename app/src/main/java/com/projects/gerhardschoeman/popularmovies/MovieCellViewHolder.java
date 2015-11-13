package com.projects.gerhardschoeman.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Gerhard on 22/10/2015.
 */
public class MovieCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface onClickCallback{
        void onViewHolderClick(int cursorPosition);
    }

    public ImageView movieImage;
    public TextView movieTitle;
    public TextView movieSortText;

    private onClickCallback callback;

    public MovieCellViewHolder(View view,onClickCallback cb){
        super(view);
        movieImage = (ImageView)view.findViewById(R.id.imageView);
        movieTitle = (TextView)view.findViewById(R.id.textTitle);
        movieSortText = (TextView)view.findViewById(R.id.textSortSelection);
        view.setOnClickListener(this);
        callback = cb;
    }

    @Override
    public void onClick(View v) {
        callback.onViewHolderClick(getAdapterPosition());
    }
}
