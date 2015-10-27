package com.projects.gerhardschoeman.popularmovies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

public class SortActivity extends Activity {

    private final String LOGTAG = "SORTACTIVITY";

    private String sortorder;
    private String[] sortOrderValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((int) (dm.widthPixels * 0.8), (int) (dm.heightPixels * 0.6));

        sortOrderValues = getResources().getStringArray(R.array.sortbyValues);

        sortorder = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.SORT_ORDER_KEY), getString(R.string.sort_default));

        Log.d(LOGTAG,"sort order from preferences: " + sortorder + " using key: " + getString(R.string.SORT_ORDER_KEY));

        setButtons(getSortID(sortorder));
    }

    public void radioClicked(View view){
        int id = view.getId();
        String so = null;
        switch (id){
            case R.id.radioPopularityUp: so = sortOrderValues[0]; break;
            case R.id.radioPopularityDown: so = sortOrderValues[1]; break;
            case R.id.radioRatingUp: so = sortOrderValues[2]; break;
            case R.id.radioRatingDown: so = sortOrderValues[3]; break;
            case R.id.radioFavourite: so = sortOrderValues[4]; break;
        }
        if(so==null) return;
        if(so.equals(sortorder)) return;
        int si = getSortID(so);
        if(si<0) return;
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(getString(R.string.SORT_ORDER_KEY),sortOrderValues[si]);
        editor.commit();
        Log.d(LOGTAG,"setting " + getString(R.string.SORT_ORDER_KEY) + " to " + sortOrderValues[si]);
        Log.d(LOGTAG, "new sort order = " + PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.SORT_ORDER_KEY),
                getString(R.string.sort_default)));
        sortorder = so;
        setButtons(si);
        finish();
    }

    private int getSortID(String so){
        int i=0;
        for (String s:sortOrderValues) {
            if(s.equals(so)) return i;
            i++;
        }
        return -1;
    }

    private void setButtons(int id){
        RadioButton rbPU = (RadioButton)findViewById(R.id.radioPopularityUp);
        RadioButton rbPD = (RadioButton)findViewById(R.id.radioPopularityDown);
        RadioButton rbRU = (RadioButton)findViewById(R.id.radioRatingUp);
        RadioButton rbRD = (RadioButton)findViewById(R.id.radioRatingDown);
        RadioButton rbF = (RadioButton)findViewById(R.id.radioFavourite);
        rbPU.setChecked(false);
        rbPD.setChecked(false);
        rbRU.setChecked(false);
        rbRD.setChecked(false);
        rbF.setChecked(false);
        switch(id){
            case 0: rbPU.setChecked(true); break;
            case 1: rbPD.setChecked(true); break;
            case 2: rbRU.setChecked(true); break;
            case 3: rbRD.setChecked(true); break;
            case 4: rbF.setChecked(true); break;
        }
    }
}
