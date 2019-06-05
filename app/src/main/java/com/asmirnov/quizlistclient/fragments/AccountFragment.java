package com.asmirnov.quizlistclient.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.asmirnov.quizlistclient.MainActivity;
import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.AuthResponse;
import com.asmirnov.quizlistclient.model.User;
import com.asmirnov.quizlistclient.service.DataAccessProvider;
import com.asmirnov.quizlistclient.service.MyHttpService;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "quizlistLogs";
    private final String SAVED_URL = "saved_URL";
    private final String SAVED_TOKEN = "saved_token";
    private final String CURRENT_USER = "current_user";
    private final String LAST_CHECK_DATE = "last_Check_Date";

    private boolean isEditebleServerAddress = false;

    private User currentUser;

    private TextView tvCurrentUser;
    private TextView tokenView;

    private EditText username;
    private EditText password;
    private EditText httpURL;

    private Button buttonGetToken;
    private Button buttonLogin;
    private Button buttonLogout;
    private Button buttonRegistration;

    private ImageButton buttonEditServerAddress;

    private MyHttpService myHttpService;
    private DataAccessProvider dataAccessProvider;

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
        buttonLogin = (Button) v.findViewById(R.id.buttonLogin);
        buttonLogout = (Button) v.findViewById(R.id.buttonLogout);
        buttonRegistration = (Button) v.findViewById(R.id.buttonRegistration);
        buttonEditServerAddress = (ImageButton) v.findViewById(R.id.buttonEditServerAddress);

        buttonGetToken.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        buttonRegistration.setOnClickListener(this);
        buttonEditServerAddress.setOnClickListener(this);

        dataAccessProvider = new DataAccessProvider(getActivity());

        refreshMyHttpService();

        loadAllParams();

        refreshUI();

        return v;
    }

    private void refreshMyHttpService() {
        MainActivity mainActivity = (MainActivity) getActivity();
        myHttpService = mainActivity.getMyHttpService();
    }

    private void loadAllParams() {
        loadURL();
        loadUser();
        loadToken();
        loadLastCheckDate();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonGetToken:
                getToken();
                break;
            case R.id.buttonLogin:
                loginUser();
                break;
            case R.id.buttonLogout:
                logoutUser();
                break;
            case R.id.buttonEditServerAddress:
                changeEditable();
                break;
            case R.id.buttonRegistration:
                registrationUser();
                break;
            default:
                break;
        }
    }

    // UI
    private void refreshUI(){

        refreshUserRepresentation();

        refreshUIVisibility();

        refreshEditURL();
    }

    private void refreshUIVisibility() {

        if(currentUser == null){
            username.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);

            buttonGetToken.setVisibility(View.GONE);
            buttonLogin.setVisibility(View.VISIBLE);
            buttonLogout.setVisibility(View.GONE);
            buttonRegistration.setVisibility(View.VISIBLE);

            tokenView.setVisibility(View.GONE);
        }else{
            username.setVisibility(View.GONE);
            password.setVisibility(View.GONE);

            buttonGetToken.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.VISIBLE);
            buttonRegistration.setVisibility(View.GONE);

            tokenView.setVisibility(View.VISIBLE);
        }
    }

    private void changeEditable() {
        isEditebleServerAddress = !isEditebleServerAddress;

        if(!isEditebleServerAddress ){
            saveURL();
        }
        refreshEditURL();
    }

    private void refreshEditURL(){
        httpURL.setInputType(isEditebleServerAddress ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
        httpURL.setBackgroundColor(isEditebleServerAddress ? Color.GREEN : Color.WHITE);
    }

    private void refreshUserRepresentation() {
        if(currentUser == null){
            tvCurrentUser.setText("no current user");
        }else{
            tvCurrentUser.setText("you logged as: "+currentUser.getUsername());
            Log.d(TAG, "you logged as: " + currentUser.toString());
        }
    }

    // Functionality
    private void getToken() {
        getTokenFromServer(currentUser.getUsername(),currentUser.getPassword());
    }

    private void loginUser() {
        getTokenFromServer(username.getText().toString(),password.getText().toString());
    }

    private void logoutUser() {
        setCurrentUser(null);
        setNewToken("");
        refreshUI();
    }

    private void registrationUser() {
        addNewUser(username.getText().toString(),password.getText().toString());
    }

    private void getTokenFromServer(String usernameStr, String passwordStr) {

        refreshMyHttpService();

        Call<AuthResponse> callToken = myHttpService.getServerQueryWithoutToken()
                .getUserToken(usernameStr,passwordStr);

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
                        tvCurrentUser.setText("user is not found");
                        return;
                    }
                    String newToken = authResponse.getToken();
                    User newUser = authResponse.getUser();
                    if(newToken == null || newToken.isEmpty()){
                        Log.d(TAG,"fall in getting token: empty token body");
                        tvCurrentUser.setText("empty token body");
                    }else{
                        setCurrentUser(newUser);
                        setNewToken(newToken);
                        refreshUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
//                tvCurrentUser.setText(t.getMessage());
                Log.d(TAG,"fall in getting token:"+t.getMessage());
            }
        });
    }

    private void addNewUser(String usernameStr, String passwordStr) {
        refreshMyHttpService();

        Call<AuthResponse> callToken = myHttpService.getServerQueryWithoutToken()
                .addNewUser(new User(usernameStr,passwordStr));

        callToken.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {

                    AuthResponse authResponse = response.body();
                    if(authResponse == null){
                        Log.d(TAG,"fall in adding new user: nullable response body");
                        return;
                    }else if(authResponse.getErrorCode() != 0){
                        Log.d(TAG,"fall in adding new user: "+authResponse.getMessage());
                        tvCurrentUser.setText(authResponse.getMessage()+"!");
                        return;
                    }else{
                        Log.d(TAG,"new user created in server (error code = 0)");

                        try {
                            User newUser = authResponse.getUser();
                            setCurrentUser(newUser);
                            refreshUI();
                            Log.d(TAG,"new user adding complete.");
                        }catch(Exception e){
                            Log.d(TAG,"fall in getting new user response body");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d(TAG,"fall in adding new user:"+t.getMessage());
            }
        });
    }

    private void setNewToken(String newToken) {
        myHttpService.update(newToken, new Date(), true);
        tokenView.setText("token:"+myHttpService.getToken());
        Log.d(TAG,"new token:"+myHttpService.getToken());

        saveToken(myHttpService.getToken());
        saveLastCheckDate(new Date());
    }

    // Save and get data

    private void setCurrentUser(User newUser) {
        currentUser = newUser;
        dataAccessProvider.saveCurrentUser(currentUser);
        refreshUserRepresentation();
    }

    private void saveURL() {
        String new_URL = httpURL.getText().toString();
        dataAccessProvider.saveStringPreferences(SAVED_URL, new_URL);
        myHttpService.setURL(new_URL);
    }

    private void loadURL() {
        httpURL.setText(dataAccessProvider.getCurrentURL());
    }

    private void loadUser() {
        currentUser = dataAccessProvider.getCurrentUser();
    }

    private void loadToken() {
        String token = dataAccessProvider.getStringPreferences(SAVED_TOKEN);
        if(token.isEmpty()){
            tokenView.setText("no saved token");
        }else{
            myHttpService.setToken(token);
            tokenView.setText("token:"+token);
            Log.d(TAG, "saved token: "+token);
        }
    }

    private void loadLastCheckDate() {

        Date lastCheckDate = dataAccessProvider.getLastCheckDatePreferences();

        if (lastCheckDate != null) {
            myHttpService.setLastCheckDate(lastCheckDate);
        }
    }

    private void saveToken(String newToken) {
        dataAccessProvider.saveStringPreferences(SAVED_TOKEN,newToken);
    }

    private void saveLastCheckDate(Date newDate) {
        dataAccessProvider.saveToPreferences(LAST_CHECK_DATE,newDate);
    }

}
