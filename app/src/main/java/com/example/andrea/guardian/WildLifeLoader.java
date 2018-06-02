package com.example.andrea.guardian;


import android.content.AsyncTaskLoader;

import android.content.Context;

import java.util.List;

public class WildLifeLoader extends AsyncTaskLoader<List<wildlife>> {
    private String mUrl;

    /**
     * New NewsLoader.
     *
     * @param context of the activity
     * @param url     url for
     */
    public WildLifeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }
    // Background thread.
    @Override
    public List<wildlife> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Make the network request and after extraction of list of news parse the response.
        List<wildlife> wild = null;
        try {
            wild = WildLifeUtilis.fetchNewsData(mUrl);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wild;
    }
}

