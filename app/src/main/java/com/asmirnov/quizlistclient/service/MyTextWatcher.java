package com.asmirnov.quizlistclient.service;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.asmirnov.quizlistclient.model.Card;

public class MyTextWatcher implements TextWatcher {
    private static final String TAG = "quizlistLogs";

    private Card card;
    private Integer count1;
    private Integer count2;
    private MyAdapterInterface mMyInterface;
    int position;

    public MyTextWatcher(Card card, MyAdapterInterface mMyInterface, int position) {
        this.card = card;
        this.count1 = 0;
        this.count2 = 0;
        this.position = position;
        this.mMyInterface = mMyInterface;
//        Log.d(TAG, "######## "+card.getId()+" ###### init MyTextWatcher"+count2);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        count1++;
//        Log.d(TAG, "---onTextChanged: [ "+card.getId() + " ]. c1="+count1+". New str:"+s+". start:"+start+", before:"+before+", count:"+count);
        if(start != before){
            card.setTerm(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        count2++;
        Log.d(TAG, "!!!!!!afterTextChanged: ["+ card.getId()+ "]. c2="+ count2);
    }
}
