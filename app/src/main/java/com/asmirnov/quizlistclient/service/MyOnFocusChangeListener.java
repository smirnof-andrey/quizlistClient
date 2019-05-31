package com.asmirnov.quizlistclient.service;


import android.view.View;
import android.widget.EditText;

public class MyOnFocusChangeListener implements View.OnFocusChangeListener {
    private static final String TAG = "quizlistLogs";

    private int position;
    private MyAdapterInterface myAdapterInterface;
    private boolean itIsTerm;

    public MyOnFocusChangeListener(int position, MyAdapterInterface myAdapterInterface, boolean itIsTerm) {
        this.position = position;
        this.myAdapterInterface = myAdapterInterface;
        this.itIsTerm = itIsTerm;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String text = ((EditText)v).getText().toString();
        myAdapterInterface.updateCardListTerm(position, text, itIsTerm);
    }
}
