package com.asmirnov.quizlistclient.service;

import android.app.Activity;
import android.util.Log;

import com.asmirnov.quizlistclient.model.User;

import java.util.Date;

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
        saveToPreferences(CURRENT_USER, currentUser);
    }

    public String getCurrentURL() {
        return getCurrentURLFromPreferences();
    }

    public boolean userIsLogged() {
        return getCurrentUser() != null;
    }

    private User getCurrentUserFromPreferences() {
        try {
            User user = (User) preferencesService.getFromStringPreferences(CURRENT_USER,User.class);
            return user;
        }catch(Exception e){
            Log.d(TAG, "no logged user");
            return null;
        }
    }

    private String getCurrentURLFromPreferences() {
        try {
            String subj = preferencesService.getStringPreferences(SAVED_URL);
            return subj;
        }catch(Exception e){
            Log.d(TAG, "no saved URL");
            return null;
        }
    }

    public void saveToPreferences(String attribute, Object obj) {
        preferencesService.saveToStringPreferences(attribute,obj);
    }

    public Date getLastCheckDatePreferences() {

        try {
            Date lastCheckDate = (Date) preferencesService.getFromStringPreferences(LAST_CHECK_DATE,Date.class);
            if(lastCheckDate==null){
                Log.d(TAG, "no saved last Check Date.");
            }else {
                Log.d(TAG, "last Check Date = " + lastCheckDate);
            }
            return lastCheckDate;
        }catch(Exception e){
            Log.d(TAG, "fall in getting last Check Date.");
            return null;
        }
    }



    public String getStringPreferences(String attribute) {
        String value = preferencesService.getStringPreferences(attribute);
        if(value.isEmpty()){
            Log.d(TAG, "no saved "+attribute);
        }
        return value;
    }

    public void saveStringPreferences(String attribute, String value) {
        preferencesService.saveStringPreferences(attribute,value);
    }

}
