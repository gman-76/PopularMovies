package com.projects.gerhardschoeman.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private final String LOGTAG = this.getClass().getSimpleName();

    private final int LOADER_ID = 1;
    private Uri mUri;

    private ShareActionProvider shareActionProvider;
    private Intent shareIntent;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOGTAG,"Creating Detail Fragment View");

        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        rootView.setTag(new DetailViewHolder(rootView));

        Bundle args = getArguments();
        if(args!=null && args.containsKey(MainActivity.DETAIL_ARGS_URI_KEY)) {
            mUri = args.getParcelable(MainActivity.DETAIL_ARGS_URI_KEY);
        }

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

        dvh.reviewsView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getVisibility() == View.VISIBLE) {
                    String movieid = mUri.getQueryParameter(MovieContract.MovieEntry.QUERY_ID_PARAM);
                    Uri uri = MovieContract.MovieReviews.buildUriFromMovieID(Long.parseLong(movieid));
                    Intent intent = new Intent(getActivity(), ViewReviewActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });

        dvh.btFavourite.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView img = (ImageView) v;
                int fav = (int) img.getTag();
                fav = ~fav;
                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.MOVIE_FAVOURITE, fav);
                getActivity().getContentResolver().update(mUri, cv, null, null);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOGTAG, "Creating options menu");
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem mi = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(mi);
        if(shareIntent==null) {
            Log.d(LOGTAG,"Setting share intent from create options menu");
            shareActionProvider.setShareIntent(getShareIntent());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(mUri==null) return null;
        return new CursorLoader(getActivity(),mUri, MovieProjections.ALL_COLUMNS.COLUMNS,null,null,null);
    }

    private String getFirstTrailerURL(){
        if(mUri==null) return null;
        Uri tUri = MovieContract.MovieTrailers.buildUriFromMovieID(Long.parseLong(mUri.getQueryParameter(MovieContract.MovieEntry.QUERY_ID_PARAM)));
        Cursor t = getActivity().getContentResolver().query(tUri,MovieProjections.TRAILER_ALL_COLUMNS.COLUMNS,null,null,null);
        if(t!=null && t.moveToFirst()) {
            return "http://www.youtube.com/watch?v=" + t.getString(MovieProjections.TRAILER_ALL_COLUMNS.URL);
        }
        return null;
    }

    private Intent getShareIntent(){
        String url = getFirstTrailerURL();
        if(url!=null){
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,"Watch this: " + url);
            return shareIntent;
        }
        return null;
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
        dvh.movieRatingBar.setRating((float) rating / 2);

        int trailers = data.getInt(MovieProjections.ALL_COLUMNS.TRAILERS);
        dvh.movieTrailers.setText(String.format("%s (%d %s)", getActivity().getString(R.string.detail_trailer_text),
                trailers,
                getActivity().getString(R.string.available)));
        dvh.trailersView.setVisibility(ImageView.GONE);
        if(trailers>0) {
            dvh.trailersView.setVisibility(ImageView.VISIBLE);
            if(shareActionProvider!=null && shareIntent==null){
                Log.d(LOGTAG,"Setting share intent after load finished");
                shareActionProvider.setShareIntent(getShareIntent());
            }
        }

        int reviews = data.getInt(MovieProjections.ALL_COLUMNS.REVIEWS);
        dvh.movieReviews.setText(String.format("%s (%d %s)", getActivity().getString(R.string.detail_review_text),
                reviews,
                getActivity().getString(R.string.available)));
        dvh.reviewsView.setVisibility(ImageView.GONE);
        if(reviews>0) dvh.reviewsView.setVisibility(ImageView.VISIBLE);

        int fav = data.getInt(MovieProjections.ALL_COLUMNS.FAVOURITE);
        dvh.btFavourite.setImageResource(fav!=0 ? R.drawable.favstar : R.drawable.favstar_grey);
        dvh.btFavourite.setTag(fav);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
