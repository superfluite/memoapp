package com.example.memoapp.app;

import android.provider.SyncStateContract;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
        @SerializedName("id")
        private int id;
        @SerializedName("text")
        private String text;
        @SerializedName("writetime")
        private String writetime;
        @SerializedName("writerid")
        private String writerid;

        public final int getId(){
            return this.id;
        }

        public String getText() {
            return this.text;
        }

        public String getWritetime() {
            return this.writetime;
        }

        public String getWriterid() {
            return this.writerid;
        }
        /*
        int id;
        String text;
        String writetime;
        */
    }
}