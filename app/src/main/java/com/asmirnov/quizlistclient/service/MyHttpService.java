package com.asmirnov.quizlistclient.service;

import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.MODE_PRIVATE;

public class MyHttpService {
    private String URL;
    private String token;
    private boolean isActual;
    private Date lastCheckDate;

    private OkHttpClient.Builder httpClient;

    public MyHttpService() {
    }

    public MyHttpService(String URL,String token){
        this.setURL(URL);
        this.update(token,null,false);
    }

    public boolean checkUserToken(){
        return false;
    }

    public void update(String token, Date date, boolean isActual){
        this.setToken(token);
        this.setLastCheckDate(date);
        this.setActual(isActual);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        refrashHttpClient();
    }

    public Boolean getActual() {
        return isActual;
    }

    public void setActual(Boolean actual) {
        isActual = actual;
    }

    public Date getLastCheckDate() {
        return lastCheckDate;
    }

    public void setLastCheckDate(Date lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public OkHttpClient.Builder getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(OkHttpClient.Builder httpClient) {
        this.httpClient = httpClient;
    }

    private void refrashHttpClient() {
        httpClient = new OkHttpClient.Builder();

        this.httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("X-Auth-Token", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
    }
}
