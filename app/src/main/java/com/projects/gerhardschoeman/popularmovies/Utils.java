package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Gerhard on 20/10/2015.
 */
public class Utils {
    public static String getPreferredSortOrder(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.SORT_ORDER_KEY),
                context.getString(R.string.sort_default));
    }

    public static String getPreferrredSortOrderDesc(Context context){
        String so = getPreferredSortOrder(context);
        if(so.startsWith("popularity")) return "Popularity";
        else if(so.startsWith("vote_average")) return "Rating";
        return "";
    }
}
