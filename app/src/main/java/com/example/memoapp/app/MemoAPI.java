package com.example.memoapp.app;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
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
    @FormUrlEncoded
    @POST("/api")
    void getMemo(@Field("id") int id, Callback<List<APIHandler.MemoData>> callback);

    @Multipart
    @POST("/api/add")
    void updateMemo(@Part("text") String text, @Part("writerid") int writerid, Callback<APIHandler.AddData> callback);

    @Multipart
    @POST("/api/edit")
    void editMemo(@Part("id") int id, @Part("text") String text, @Part("writerid") int writerid, Callback<APIHandler.AddData> callback);

    @Multipart
    @POST("/api/delete")
    void deleteMemo(@Part("id") int id, Callback<APIHandler.AddData> callback);

    @FormUrlEncoded
    @POST("/api/login")
    void login(@Field("userId") String userId, @Field("userPassword") String userPassword, Callback<APIHandler.User> callback);

    @FormUrlEncoded
    @POST("/api/signup")
    void signup(@Field("userId") String userId, @Field("userPassword") String userPassword, Callback<APIHandler.User> callback);

    @FormUrlEncoded
    @POST("/api/deleteaccount")
    void deleteAccount(@Field("id") int id, Callback<APIHandler.User> callback);
}