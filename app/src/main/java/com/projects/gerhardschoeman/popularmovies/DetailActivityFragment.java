package com.projects.gerhardschoeman.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;
import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_ID = 1;
    private Uri mUri;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        rootView.setTag(new DetailViewHolder(rootView));

        Intent intent = getActivity().getIntent();
        mUri = intent.getData();

        DetailViewHolder dvh = new DetailViewHolder(rootView);
        dvh.trailersView.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v.getVisibility()==View.VISIBLE){
                    String movieid = mUri.getQueryParameter(MovieContract.MovieEntry.QUERY_ID_PARAM);
                    Uri uri = MovieContract.MovieTrailers.buildUriFromMovieID(Long.parseLong(movieid));
                    Intent intent = new Intent(getActivity(),ViewTrailerActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });

        dvh.reviewsView.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v.getVisibility()==View.VISIBLE){
                    String movieid = mUri.getQueryParameter(MovieContract.MovieEntry.QUERY_ID_PARAM);
                    Uri uri = MovieContract.MovieReviews.buildUriFromMovieID(Long.parseLong(movieid));
                    Intent intent = new Intent(getActivity(),ViewReviewActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri==null) return null;
        return new CursorLoader(getActivity(),mUri, MovieProjections.ALL_COLUMNS.COLUMNS,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data==null || !data.moveToFirst()) return;
        DetailViewHolder dvh = (DetailViewHolder)getView().getTag();
        Picasso.with(getActivity()).load(data.getString(MovieProjections.ALL_COLUMNS.POSTER)).error(R.drawable.noimage).into(dvh.moviePoster);
        dvh.movieTitle.setText(data.getString(MovieProjections.ALL_COLUMNS.NAME));
        double rating = data.getDouble(MovieProjections.ALL_COLUMNS.RATING);
        dvh.movieRating.setText(String.format("%s %.1f", getActivity().getString(R.string.detail_rating), rating));
        dvh.movieReleaseDate.setText(String.format("%s %s", getActivity().getString(R.string.detail_releasedate),
                data.getString(MovieProjections.ALL_COLUMNS.RELEASEDATE)));
        dvh.movieOverview.setText(data.getString(MovieProjections.ALL_COLUMNS.OVERVIEW));

        dvh.movieRatingBar.setNumStars(5);
        dvh.movieRatingBar.setMax(5);
        dvh.movieRatingBar.setStepSize((float) 0.1);
        dvh.movieRatingBar.setRating((float) rating/2);

        int trailers = data.getInt(MovieProjections.ALL_COLUMNS.TRAILERS);
        dvh.movieTrailers.setText(String.format("%s (%d %s)", getActivity().getString(R.string.detail_trailer_text),
                trailers,
                getActivity().getString(R.string.available)));
        dvh.trailersView.setVisibility(ImageView.GONE);
        if(trailers>0) dvh.trailersView.setVisibility(ImageView.VISIBLE);

        int reviews = data.getInt(MovieProjections.ALL_COLUMNS.REVIEWS);
        dvh.movieReviews.setText(String.format("%s (%d %s)", getActivity().getString(R.string.detail_review_text),
                reviews,
                getActivity().getString(R.string.available)));
        dvh.reviewsView.setVisibility(ImageView.GONE);
        if(reviews>0) dvh.reviewsView.setVisibility(ImageView.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
