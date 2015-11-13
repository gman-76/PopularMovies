package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.gerhardschoeman.popularmovies.data.MovieContract;
import com.projects.gerhardschoeman.popularmovies.data.MovieProjections;
import com.projects.gerhardschoeman.popularmovies.data.MovieProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MovieCellAdapter.ClickHandler{

    private final String LOGTAG = this.getClass().getSimpleName();

    private final int LOADER_ID = 1;

    private final String SEARCH_STATE = "SEARCH_STATE";

    MovieCellAdapter cellAdapter = null;
    private ProgressBar loading = null;
    private Utils.SORTORDER currentSort = Utils.SORTORDER.NO_ORDER;

    private EditText mSearchText;
    private String mCurrentSearch;
    private TextView mEmptyView;

    @Override
    public void onClick(Uri uri) {
        itemClickListener.onItemClicked(uri);
    }

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

        if(savedInstanceState!=null && savedInstanceState.containsKey(SEARCH_STATE)){
            mCurrentSearch = savedInstanceState.getString(SEARCH_STATE);
        }

        cellAdapter = new MovieCellAdapter(getActivity(),this);

        RecyclerView grid = (RecyclerView)rootView.findViewById(R.id.gridView);
        int span = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        grid.setLayoutManager(new GridLayoutManager(getActivity(), span));
        grid.setAdapter(cellAdapter);
        grid.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pxH = getResources().getDimensionPixelSize(R.dimen.grid_horizontal_spacing);
                int pxV = getResources().getDimensionPixelSize(R.dimen.grid_vertical_spacing);
                outRect.top = pxH;
                outRect.right = outRect.left = pxV;
            }
        });
        grid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    GridLayoutManager glm = (GridLayoutManager) recyclerView.getLayoutManager();
                    int count = glm.getItemCount();
                    if (glm.findLastVisibleItemPosition() >= count - 1) {
                        Log.d("GRIDVIEW", "ready to load more data - last visible position is " + Integer.toString(glm.findLastVisibleItemPosition()));
                        int lastPageLoaded = -1;
                        if (cellAdapter.pagesLoaded.size() > 0) {
                            lastPageLoaded = cellAdapter.pagesLoaded.get(cellAdapter.pagesLoaded.size() - 1);
                        }
                        loadExtraData(lastPageLoaded + 1);
                    }
                }
            }
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

        mSearchText = (EditText)rootView.findViewById(R.id.txtSearch);
        ImageView search = (ImageView)rootView.findViewById(R.id.btSearch);
        search.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                searchMovie();
            }
        });

        mEmptyView = (TextView)rootView.findViewById(R.id.grid_empty_view);

        loading = (ProgressBar)rootView.findViewById(R.id.progressLoading);
        loading.setVisibility(ProgressBar.GONE);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(LOADER_ID,null,this);
        //getLoaderManager().initLoader(LOADER_ID_FAVONLY,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    void searchMovie(){
        String searchText = mSearchText.getText().toString();
        if(searchText!=null && searchText.length()>0){
            mCurrentSearch = searchText;
            currentSort = Utils.SORTORDER.NO_ORDER;
            loading.setVisibility(ProgressBar.VISIBLE);
            MovieDBQueryTask queryTask = new MovieDBQueryTask(getActivity(),cellAdapter,loading,0,searchText);
            queryTask.execute();
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }else{
            mCurrentSearch = null;
        }
    }

    void loadExtraData(int pageID){
        loading.setVisibility(ProgressBar.VISIBLE);
        MovieDBQueryTask queryTask = new MovieDBQueryTask(getActivity(),cellAdapter,loading,pageID,mCurrentSearch);
        queryTask.execute();
    }

    void reloadData(){
        //getActivity().getContentResolver().delete(MovieContract.MovieReviews.CONTENT_URI,null,null);
        //getActivity().getContentResolver().delete(MovieContract.MovieTrailers.CONTENT_URI,null,null);
        //getActivity().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,null,null);
        currentSort = Utils.SORTORDER.NO_ORDER;
        loadExtraData(0);
    }

    void refreshData(){
        Utils.SORTORDER preferredSortOrder = Utils.getPreferredSortOrder(getActivity());
        if(currentSort== Utils.SORTORDER.NO_ORDER || currentSort!=preferredSortOrder){
            loading.setVisibility(ProgressBar.VISIBLE);
            currentSort = preferredSortOrder;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mCurrentSearch!=null){
            outState.putString(SEARCH_STATE,mCurrentSearch);
        }
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
        Log.d(LOGTAG,"Creating loader for sort");
        String so = MovieContract.MovieEntry.getSortOrderSelection(getActivity());
        Uri uri = mCurrentSearch!=null && mCurrentSearch.length()>0 ? MovieContract.MovieEntry.buildUriFromTitle(mCurrentSearch) :
                MovieContract.MovieEntry.CONTENT_URI;
        if(Utils.getPreferredSortOrder(getActivity())== Utils.SORTORDER.FAV){
            uri = uri.buildUpon().appendQueryParameter(MovieContract.MovieEntry.QUERY_FAV_PARAM,"true").build();
        }
        return new CursorLoader(getActivity(), uri, MovieProjections.ALL_COLUMNS.COLUMNS, null, null, so);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cellAdapter.swapCursor(data);
        loading.setVisibility(ProgressBar.GONE);
        mEmptyView.setVisibility(cellAdapter.getItemCount()>0 ? TextView.GONE : TextView.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cellAdapter.swapCursor(null);
    }
}
