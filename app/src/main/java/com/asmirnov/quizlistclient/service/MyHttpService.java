package com.asmirnov.quizlistclient.service;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.asmirnov.quizlistclient.dto.AuthResponse;
import com.asmirnov.quizlistclient.model.User;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyHttpService implements Parcelable {
    private static final String TAG = "quizlistLogs";
    private final String SAVED_TOKEN = "saved_token";

    private String URL;
    private String token;
    private Date lastCheckDate;
    private boolean isActual;

    DataAccessProvider dataAccessProvider;

    public MyHttpService() {
    }

    public MyHttpService(String URL,String token){
        this.setURL(URL);
        this.update(token,null,false);
    }

    public MyHttpService(String URL,String token, Date lastCheckDate){
        this.setURL(URL);
        this.update(token,lastCheckDate,false);
    }

    public void refreshUserToken(DataAccessProvider dataAccessProvider){
        this.dataAccessProvider = dataAccessProvider;
        User user = dataAccessProvider.getCurrentUser();
        if(user != null) {
            getTokenFromServer(user.getUsername(), user.getPassword());
        }
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
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
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

        return httpClient;
    }

    public void loadSavedData(){

    }

    public ServerQuery getServerQuery(){

        OkHttpClient client = getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(ServerQuery.class);
    }

    public ServerQuery getServerQueryWithoutToken(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ServerQuery.class);
    }

    private void getTokenFromServer(String usernameStr, String passwordStr) {

        Call<AuthResponse> callToken = getServerQueryWithoutToken()
                .userAuthentication(usernameStr,passwordStr);

        Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.d(TAG,"Start token refresh");
        callToken.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    AuthResponse authResponse = response.body();
                    if(authResponse == null){
                        Log.d(TAG,"fall in getting token: nullable response body");
                        return;
                    }else if(authResponse.getErrorCode() == 1){
                        Log.d(TAG,"fall in getting token: user is not found (error code = 1)");
                        return;
                    }
                    String newToken = authResponse.getToken();
                    if(newToken == null || newToken.isEmpty()){
                        Log.d(TAG,"fall in getting token: empty token body");
                    }else{
                        Log.d(TAG,"refreshed token successful");
                        update(newToken, new Date(), true);
                        dataAccessProvider.saveStringPreferences(SAVED_TOKEN,newToken);
                    }
                }
                Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG,"fall in getting token:"+t.getMessage());
                Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        });
    }

    // Parcelable methods

    protected MyHttpService(Parcel in) {
        URL = in.readString();
        token = in.readString();
        long tmpLastCheckDate = in.readLong();
        lastCheckDate = tmpLastCheckDate != -1 ? new Date(tmpLastCheckDate) : null;
        isActual = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(URL);
        dest.writeString(token);
        dest.writeLong(lastCheckDate != null ? lastCheckDate.getTime() : -1L);
        dest.writeByte((byte) (isActual ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyHttpService> CREATOR = new Parcelable.Creator<MyHttpService>() {
        @Override
        public MyHttpService createFromParcel(Parcel in) {
            return new MyHttpService(in);
        }

        @Override
        public MyHttpService[] newArray(int size) {
            return new MyHttpService[size];
        }
    };
}
