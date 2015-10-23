package com.projects.gerhardschoeman.popularmovies;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Gerhard on 20/10/2015.
 */
public class Utils {
    public static final String LOGTAG = Utils.class.getSimpleName();

    public static enum SORTORDER{
        NO_ORDER,
        POP_ASC,
        POP_DESC,
        RATE_ASC,
        RATE_DESC
    }

    public static String getPreferredSortOrderValue(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.SORT_ORDER_KEY),
                context.getString(R.string.sort_default));
    }

    public static SORTORDER getPreferredSortOrder(Context context){
        String so = getPreferredSortOrderValue(context);
        if(so.startsWith("popularity")){
            return so.endsWith("asc") ? SORTORDER.POP_ASC : SORTORDER.POP_DESC;
        }else if(so.startsWith("vote_average")){
            return so.endsWith("asc") ? SORTORDER.RATE_ASC : SORTORDER.RATE_DESC;
        }
        return SORTORDER.NO_ORDER;
    }

    public static String getPreferrredSortOrderDesc(Context context){
        switch(getPreferredSortOrder(context)){
            case POP_ASC:
            case POP_DESC:
                return context.getString(R.string.SORT_DESC_POP);
            case RATE_ASC:
            case RATE_DESC:
                return context.getString(R.string.SORT_DESC_RATE);
        }
        return "";
    }
}
