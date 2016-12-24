package com.example.xkwei.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by xkwei on 24/12/2016.
 */

public class QueryPreference {
    private final static String PREFERENCE_SEARCH = "SearchHistory";

    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREFERENCE_SEARCH,null);
    }
    public static void setStoredQuery(Context context,String query){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREFERENCE_SEARCH, query)
                .apply();
    }
}
