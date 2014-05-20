package com.example.memoapp.app;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Created by Limesty on 2014-05-15.
 */
public interface MemoAPI {
    @GET("/api")
    void getMemo(@Query("number") String number, Callback<List<APIHandler.MemoData>> callback);

    @Multipart
    @POST("/api/add")
    void updateMemo(@Part("text") String text, @Part("writerid") int writerid, Callback<APIHandler.AddData> callback);

    @Multipart
    @POST("/api/edit")
    void editMemo(@Part ("id") int id, @Part("text") String text, @Part("writerid") int writerid, Callback<APIHandler.AddData> callback);

    @Multipart
    @POST("/api/delete")
    void deleteMemo(@Part("id") int id, Callback<APIHandler.AddData> callback);
}