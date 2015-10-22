package com.projects.gerhardschoeman.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if(intent!=null && intent.hasExtra("movie")){
            Bundle bundle = intent.getExtras();
            Movie movie = (Movie)bundle.getSerializable("movie");
            if(movie==null) return rootView;
            TextView overview = (TextView)rootView.findViewById(R.id.txtOverview);
            TextView rating = (TextView)rootView.findViewById(R.id.rating);
            TextView release = (TextView)rootView.findViewById(R.id.releasedate);
            TextView title = (TextView)rootView.findViewById(R.id.detailTitle);
            ImageView imgback = (ImageView)rootView.findViewById(R.id.imgBackground);
            overview.setText(movie.overview);
            Picasso.with(getActivity()).load(movie.poster).error(R.drawable.noimage).into(imgback);
            imgback.setContentDescription(movie.title);
            rating.setText("Rating: " + Double.toString(movie.rating));
            release.setText("Release Date: " + movie.releaseDate);
            title.setText(movie.title);
        }
        return rootView;
    }
}
