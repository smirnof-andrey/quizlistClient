package com.asmirnov.quizlistclient.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.MainActivity;
import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.AuthResponse;
import com.asmirnov.quizlistclient.model.User;
import com.asmirnov.quizlistclient.service.ServerQuery;
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment implements View.OnClickListener{

    private final String SAVED_URL = "saved_URL";
    private final String SAVED_TOKEN = "saved_token";
    private final String CURRENT_USER = "current_user";

    private User currentUser;

    private TextView tvCurrentUser;
    private TextView tokenView;

    private EditText username;
    private EditText password;
    private EditText httpURL;

    private Button buttonGetToken;

    private MyHttpService myHttpService;

    private SharedPreferences sPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_account,container,false);

        tokenView = (TextView) v.findViewById(R.id.tokenView);
        tvCurrentUser = (TextView) v.findViewById(R.id.currentUser);

        httpURL = (EditText) v.findViewById(R.id.httpURL);
        username = (EditText) v.findViewById(R.id.editUsername);
        password = (EditText) v.findViewById(R.id.editPassword);

        buttonGetToken = (Button) v.findViewById(R.id.buttonGetToken);

        buttonGetToken.setOnClickListener(this);

        httpURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveURL();
            }
        });

        refreshMyHttpService();

        loadURL();
        loadUser();
        loadToken();

        return v;
    }

    private void refreshMyHttpService() {
        MainActivity mainActivity = (MainActivity) getActivity();
        myHttpService = mainActivity.getMyHttpService();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonGetToken:
                getToken();
                break;
            default:
                break;
        }
    }

    private void saveURL() {
        String new_URL = httpURL.getText().toString();
        savePreferences(SAVED_URL, new_URL);
        myHttpService.setURL(new_URL);
    }
    private void loadURL() {
        httpURL.setText(getPreferences(SAVED_URL));
    }

    private void loadUser() {
        Gson gson = new Gson();
        String json = getPreferences(CURRENT_USER);
        try {
            currentUser = gson.fromJson(json, User.class);
            refreshUserShowing();
        }catch(Exception e){
            // no logged user
            Toast.makeText(getActivity(), "no logged user", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshUserShowing() {
        tvCurrentUser.setText("you logged as: "+currentUser.toString());
        username.setText(currentUser.getUsername());
        password.setText("");
    }

    private void loadToken() {
        tokenView.setText("token:"+getPreferences(SAVED_TOKEN));
    }

    private void savePreferences(String attribute, String value) {
        sPref = this.getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(attribute, value);
        ed.commit();
    }

    private String getPreferences(String attribute) {
        sPref = this.getActivity().getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(attribute, "");
        return savedText;
    }

    private void getToken() {

        refreshMyHttpService();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        Call<AuthResponse> callToken = serverQuery.getUserToken(username.getText().toString(),password.getText().toString());

        callToken.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {

                    AuthResponse authResponse = response.body();
                    if(authResponse == null){
                        // do smth
                    }
                    String newToken = authResponse.getToken();
                    User newUser = authResponse.getUser();
                    if(newToken == null || newToken.isEmpty()){
                        // do smth
                    }else{
                        setCurrentUser(newUser);
                        setTokenInHttpHeader(newToken);
                        //getUserModules();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                tokenView.setText(t.getMessage());
                //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCurrentUser(User newUser) {
        currentUser = newUser;
        refreshUserShowing();

        Gson gson = new Gson();
        String json = gson.toJson(currentUser);
        savePreferences(CURRENT_USER,json);
    }

    private void setTokenInHttpHeader(String newToken) {
        myHttpService.update(newToken, new Date(), true);
        tokenView.setText("token:"+myHttpService.getToken());

        myHttpService.getHttpClient().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("X-Auth-Token", myHttpService.getToken())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        savePreferences(SAVED_TOKEN,myHttpService.getToken());
    }

}
