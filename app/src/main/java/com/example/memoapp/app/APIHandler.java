package com.example.memoapp.app;

import retrofit.RestAdapter;

/**
 * Created by Limesty on 2014-05-15.
 */
public class APIHandler {
    private static final String API_URL="http://memopage.herokuapp.com/";
    private static RestAdapter restAdapter;

    private static RestAdapter getRestAdapter(){
        if(restAdapter == null){
            restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).build();
        }
        return restAdapter;
    }

    public static MemoAPI getApiInterface(){
        MemoAPI memoAPI = null;
        try {
            if(restAdapter == null){
                restAdapter = getRestAdapter();
            }
            memoAPI = restAdapter.create(MemoAPI.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return memoAPI;
    }

    public static class MemoData{
        int id;
        String text;
        String writetime;
    }
}