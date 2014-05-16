package com.example.memoapp.app;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Limesty on 2014-05-15.
 */
public interface MemoAPI {
    @GET("/api")
    void getMemo(@Query("number") String number, Callback<APIHandler.MemoData> callback);
}
