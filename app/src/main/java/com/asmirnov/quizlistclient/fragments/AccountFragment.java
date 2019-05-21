package com.asmirnov.quizlistclient.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment implements View.OnClickListener{

    private final String LOG_TAG = "quizlistLogs";
    private final String SAVED_URL = "saved_URL";
    private final String SAVED_TOKEN = "saved_token";
    private final String CURRENT_USER = "current_user";
    private final String LAST_CHECK_DATE = "last_Check_Date";

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
        loadLastCheckDate();

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
            Log.d(LOG_TAG, "no logged user");
        }
    }

    private void loadToken() {
        String token = getPreferences(SAVED_TOKEN);
        if(token==null || token.isEmpty()){
            tokenView.setText("no saved token");
            Log.d(LOG_TAG, "no saved token");
        }else{
            myHttpService.setToken(token);
            tokenView.setText("token:"+token);
            Log.d(LOG_TAG, "saved token: "+token);
        }
    }

    private void loadLastCheckDate() {
        Date lastCheckDate;
        Gson gson = new Gson();
        String json = getPreferences(LAST_CHECK_DATE);
        try {
            lastCheckDate = gson.fromJson(json, Date.class);
            if(lastCheckDate==null){
                Log.d(LOG_TAG, "no saved last Check Date.");
            }else {
                Log.d(LOG_TAG, "last Check Date = " + lastCheckDate);
                myHttpService.setLastCheckDate(lastCheckDate);
            }
        }catch(Exception e){
            Toast.makeText(getActivity(), "fall in getting last Check Dat", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "fall in getting last Check Dat.");
        }
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

    private void refreshUserShowing() {
        tvCurrentUser.setText("you logged as: "+currentUser.toString());
        username.setText(currentUser.getUsername());
        password.setText("");
        Log.d(LOG_TAG, "you logged as: "+currentUser.toString());
    }

    private void getToken() {

        refreshMyHttpService();

        Call<AuthResponse> callToken = myHttpService.getServerQueryWithoutToken()
                .getUserToken(username.getText().toString(),password.getText().toString());

        callToken.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {

                    AuthResponse authResponse = response.body();
                    if(authResponse == null){
                        Log.d(LOG_TAG,"fall in getting token: nullable response body");
                        return;
                    }else if(authResponse.getErrorCode() == 1){
                        Log.d(LOG_TAG,"fall in getting token: user is not found (error code = 1)");
                        return;
                    }
                    String newToken = authResponse.getToken();
                    User newUser = authResponse.getUser();
                    if(newToken == null || newToken.isEmpty()){
                        Log.d(LOG_TAG,"fall in getting token: empty token body");
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
                Log.d(LOG_TAG,"fall in getting token:"+t.getMessage());
            }
        });
    }

    private void setCurrentUser(User newUser) {
        currentUser = newUser;
        refreshUserShowing();

        Gson gson = new Gson();
        String json = gson.toJson(currentUser);
        savePreferences(CURRENT_USER,json);

        Log.d(LOG_TAG,"set current user: "+newUser.getUsername());
    }

    private void setTokenInHttpHeader(String newToken) {
        myHttpService.update(newToken, new Date(), true);
        tokenView.setText("token:"+myHttpService.getToken());
        Log.d(LOG_TAG,"new token:"+myHttpService.getToken());

//        myHttpService.getHttpClient().addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//
//                Request request = original.newBuilder()
//                        .header("Content-Type", "application/json")
//                        .header("X-Auth-Token", myHttpService.getToken())
//                        .method(original.method(), original.body())
//                        .build();
//
//                return chain.proceed(request);
//            }
//        });

        savePreferences(SAVED_TOKEN,myHttpService.getToken());

        Gson gson = new Gson();
        String json = gson.toJson(myHttpService.getLastCheckDate());
        savePreferences(LAST_CHECK_DATE,json);
    }

}
