package com.asmirnov.quizlistclient.service;

import com.asmirnov.quizlistclient.dto.AuthResponse;
import com.asmirnov.quizlistclient.dto.ModuleAdditionalInfo;
import com.asmirnov.quizlistclient.dto.ModuleCardsDTO;
import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerQuery {

    // user auth
    @GET("/auth")
    Call<AuthResponse> userAuthentication(@Query("username") String username, @Query("password") String password);

    @POST("/newUser")
    Call<AuthResponse> addNewUser(@Body User user);


    // module and cards
    @GET("/module/{id}")
    Call<Module> getModuleById(@Path("id") String id);

    @GET("/module")
    Call<List<ModuleAdditionalInfo>> getModules();

    @GET("/cards/{id}")   //@GET("/module/{id}/cards")  //
    Call<List<Card>> getCards(@Path("id") String id);


    @POST("/module")
    Call<Module> createModule(@Body Module module);

    @POST("/cards/{id}")
    Call<Card> createCard(@Path("id") String id, @Body Card card);

    @POST("/cards")
    Call<ModuleCardsDTO> createCards(@Body ModuleCardsDTO requestDTO);
//    Call<String> createCards(@Body Map<String, Object> map);


    @DELETE("/module/{id}")
    Call<Integer> deleteModule(@Path("id") String id);


    @PUT("/module/{id}")
    Call<Module> updateModule(@Path("id") String id, @Body Module module);

    @PUT("/cards/{id}")
    Call<ModuleCardsDTO> updateCards(@Path("id") String id, @Body ModuleCardsDTO requestDTO);




}
