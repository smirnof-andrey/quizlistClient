package com.asmirnov.quizlistclient.service;

import com.asmirnov.quizlistclient.model.AuthResponse;
import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerQuery {

    // GET + Query || POST + Body

    @GET("/auth")
    Call<AuthResponse> getUserToken(@Query("username") String username, @Query("password") String password);

    @GET("/module/{id}")
    Call<Module> getModuleById(@Path("id") String id);

    @GET("/module")
    Call<List<Module>> getModules();

    @GET("/cards/{id}")   //@GET("/module/{id}/cards")  //
    Call<List<Card>> getCards(@Path("id") String id);


    //@FormUrlEncoded
    @POST("/module")
    Call<Module> createModule(@Body Module module);

    @POST("/cards/{id}")
    Call<Card> createCard(@Path("id") String id, @Body Card card);


    @DELETE("/module/{id}")
    Call<Integer> deleteModule(@Path("id") String id);


    @PUT("/module/{id}")
    Call<Module> updateModule(@Path("id") String id, @Body Module module);

    @PUT("/cards/{id}")
    Call<Module> updateCards(@Path("id") String id, @Body Map<String, Object> map);
//    Call<List<Card>> updateCards(@Path("id") String id, @Query("module") Module module, @Query("cardList") List<Card> cardList);

}
