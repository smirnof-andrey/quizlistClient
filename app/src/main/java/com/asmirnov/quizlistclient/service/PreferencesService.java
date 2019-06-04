package com.asmirnov.quizlistclient.service;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesService {
    private Activity activity;
    private SharedPreferences sPref;

    public PreferencesService(Activity activity) {
        this.activity = activity;
        sPref = activity.getPreferences(MODE_PRIVATE);
    }

    public void saveStringPreferences(String attribute, String value) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(attribute, value);
        ed.commit();
    }

    public String getStringPreferences(String attribute) {
        String savedText = sPref.getString(attribute, "");
        return savedText;
    }

    public void saveToStringPreferences(String attribute, Object value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);

        saveStringPreferences(attribute, json);
    }

    public Object getFromStringPreferences(String attribute, Class classOfT) {
        Gson gson = new Gson();
        String json = getStringPreferences(attribute);
        return gson.fromJson(json, classOfT);
    }
}
