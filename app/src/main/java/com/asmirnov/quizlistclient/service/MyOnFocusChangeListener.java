package com.asmirnov.quizlistclient.service;


import android.view.View;
import android.widget.EditText;

public class MyOnFocusChangeListener implements View.OnFocusChangeListener {
    private static final String TAG = "quizlistLogs";

    private int position;
    private MyAdapterInterface myAdapterInterface;

    public MyOnFocusChangeListener(int position, MyAdapterInterface myAdapterInterface) {
        this.position = position;
        this.myAdapterInterface = myAdapterInterface;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String text = ((EditText)v).getText().toString();
//        Log.d(TAG, "onFocusChange: position:"+position+", text:"+text);
        myAdapterInterface.updateCardList(position,text);
    }
}
