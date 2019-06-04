package com.asmirnov.quizlistclient.service;

import android.app.Activity;
import android.util.Log;

import com.asmirnov.quizlistclient.model.User;
import com.asmirnov.quizlistclient.service.PreferencesService;

public class DataAccessProvider {
    private final String TAG = "quizlistLogs";
    private final String SAVED_URL = "saved_URL";
    private final String SAVED_TOKEN = "saved_token";
    private final String CURRENT_USER = "current_user";
    private final String LAST_CHECK_DATE = "last_Check_Date";
    private Activity context;
    private PreferencesService preferencesService;

    public DataAccessProvider(Activity context) {
        this.context = context;
        this.preferencesService = new PreferencesService(context);
    }

    public User getCurrentUser() {
        return getCurrentUserFromPreferences();
    }

    public void saveCurrentUser(User currentUser) {
        saveCurrentUserToPreferences(currentUser);
    }

    public String getCurrentURL() {
        return getCurrentURLFromPreferences();
    }

    public boolean userIsLogged() {
        return getCurrentUser() != null;
    }

    public User getCurrentUserFromPreferences() {
        try {
            User user = (User) preferencesService.getFromStringPreferences(CURRENT_USER,User.class);
            return user;
        }catch(Exception e){
            Log.d(TAG, "no logged user");
            return null;
        }
    }

    public String getCurrentURLFromPreferences() {
        try {
            String subj = preferencesService.getStringPreferences(SAVED_URL);
            return subj;
        }catch(Exception e){
            Log.d(TAG, "no saved URL");
            return null;
        }
    }

    public void saveCurrentUserToPreferences(User currentUser) {
        preferencesService.saveToStringPreferences(CURRENT_USER,currentUser);
    }

}
