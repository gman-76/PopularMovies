package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;
import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOGTAG = this.getClass().getSimpleName();

    public final String SORT_STATE_KEY = "SSKEY";

    private final int LOADER_ID = 1;

    MovieCellAdapter cellAdapter = null;
    private ProgressBar loading = null;
    private Utils.SORTORDER currentSort = Utils.SORTORDER.NO_ORDER;

    public interface OnItemClickListener{
        void onItemClicked(Uri uri);
    }

    OnItemClickListener itemClickListener;

    public MainActivityFragment() {
        //setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            itemClickListener = (OnItemClickListener)getActivity();
        }catch(ClassCastException e){
            Log.e(LOGTAG,"Main activity does not implement onItemClickListener");
        }
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
        cellAdapter = new MovieCellAdapter(getActivity(),null,0);

        GridView grid = (GridView)rootView.findViewById(R.id.gridView);
        grid.setAdapter(cellAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                if(cursor!=null){
                    Uri uri = MovieContract.MovieEntry.buildUriFromID(cursor.getLong(MovieProjections.ALL_COLUMNS.ID));
                    itemClickListener.onItemClicked(uri);
                }
            }
        });

        grid.setOnScrollListener(new GridView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState==SCROLL_STATE_IDLE) {
                    int count = view.getCount();
                    if(view.getLastVisiblePosition()>=count - 1){
                        Log.d("GRIDVIEW", "ready to load more data - last visible position is " + Integer.toString(view.getLastVisiblePosition()));
                        int lastPageLoaded = -1;
                        if(cellAdapter.pagesLoaded.size()>0){
                            lastPageLoaded = cellAdapter.pagesLoaded.get(cellAdapter.pagesLoaded.size()-1);
                        }
                        loadExtraData(lastPageLoaded+1);
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
                reloadData();
            }
        });

        loading = (ProgressBar)rootView.findViewById(R.id.progressLoading);
        loading.setVisibility(ProgressBar.GONE);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    void loadExtraData(int pageID){
        loading.setVisibility(ProgressBar.VISIBLE);
        MovieDBQueryTask queryTask = new MovieDBQueryTask(getActivity(),cellAdapter,loading,pageID);
        queryTask.execute();
    }

    void reloadData(){
        getActivity().getContentResolver().delete(MovieContract.MovieReviews.CONTENT_URI,null,null);
        getActivity().getContentResolver().delete(MovieContract.MovieTrailers.CONTENT_URI,null,null);
        getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,null,null);
        currentSort = Utils.SORTORDER.NO_ORDER;
        loadExtraData(0);
    }

    void refreshData(){
        Utils.SORTORDER preferredSortOrder = Utils.getPreferredSortOrder(getActivity());
        if(currentSort== Utils.SORTORDER.NO_ORDER || currentSort!=preferredSortOrder){
            loading.setVisibility(ProgressBar.VISIBLE);
            getLoaderManager().restartLoader(LOADER_ID,null,this);
            currentSort = preferredSortOrder;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mainfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh){
            reloadData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String so = MovieContract.MovieEntry.getSortOrderSelection(getActivity());
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),uri, MovieProjections.ALL_COLUMNS.COLUMNS,null,null,so);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cellAdapter.swapCursor(data);
        loading.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cellAdapter.swapCursor(null);
    }
}
