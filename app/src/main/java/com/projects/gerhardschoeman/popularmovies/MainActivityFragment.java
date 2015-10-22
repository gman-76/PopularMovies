package com.projects.gerhardschoeman.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public final String SORT_STATE_KEY = "SSKEY";

    MovieCellAdapter cellAdapter = null;
    private ProgressBar loading = null;
    private String currentSort = null;

    public MainActivityFragment() {
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey(SORT_STATE_KEY)){
                //currentSort = savedInstanceState.getString(SORT_STATE_KEY);
            }
        }
        cellAdapter = new MovieCellAdapter(getActivity(),inflater);

        GridView grid = (GridView)rootView.findViewById(R.id.gridView);
        grid.setAdapter(cellAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie m = (Movie) cellAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", m);
                startActivity(intent);
            }
        });

        grid.setOnScrollListener(new GridView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState==SCROLL_STATE_IDLE) {
                    int count = view.getCount();
                    if(view.getLastVisiblePosition()>=count - 1){
                        Log.d("GRIDVIEW", "ready to load more data - last visible position is " + Integer.toString(view.getLastVisiblePosition()));
                        int lastPageLoaded = cellAdapter.pagesLoaded.get(cellAdapter.pagesLoaded.size()-1);
                        MovieDBQueryTask queryTask = new MovieDBQueryTask(getActivity(),cellAdapter,loading,lastPageLoaded+1);
                        queryTask.execute();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }
        });
        TextView st = (TextView)rootView.findViewById(R.id.sortTitle);
        st.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SortActivity.class);
                getActivity().startActivity(intent);
            }
        });

        ImageView refresh = (ImageView)rootView.findViewById(R.id.btRefresh);
        refresh.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSort = null;
                refreshData();
            }
        });

        loading = (ProgressBar)rootView.findViewById(R.id.progressLoading);
        loading.setVisibility(ProgressBar.GONE);

        return rootView;
    }

    void refreshData(){
        String ds = Utils.getPreferredSortOrder(getActivity());
        if(currentSort==null || !currentSort.equals(ds)) {
            loading.setVisibility(ProgressBar.VISIBLE);
            cellAdapter.movies.clear();
            MovieDBQueryTask queryTask = new MovieDBQueryTask(getActivity(), cellAdapter, loading, 0);
            queryTask.execute();
            currentSort = ds;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentSort!=null){
            outState.putString(SORT_STATE_KEY,currentSort);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mainfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh){
            refreshData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
