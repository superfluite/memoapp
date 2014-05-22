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
    //private static final String API_URL = "http://192.168.0.96:5000";
    private static final String API_URL = "http://memopage.herokuapp.com";
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
        private int writerid;

        public final int getId(){
            return this.id;
        }

        public String getText() {
            return this.text;
        }

        public String getWritetime() {
            return this.writetime;
        }

        public int getWriterid() {
            return this.writerid;
        }
    }

    public static class AddData {
        @SerializedName("text")
        private String text;
        @SerializedName("writerid")
        private int writerid;

        public String getText() {
            return this.text;
        }

        public int getWriterid() {
            return this.writerid;
        }
    }

    public static class User {
        @SerializedName("id")
        private int id;
        @SerializedName("userId")
        private String userId;
        @SerializedName("userPassword")
        private String userPassword;

        public int getId() {
            return this.id;
        }

        public String getUserId() {
            return this.userId;
        }

        public String getUserPassword() {
            return this.userPassword;
        }
    }
}