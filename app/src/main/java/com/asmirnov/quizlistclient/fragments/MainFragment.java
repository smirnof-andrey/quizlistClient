package com.asmirnov.quizlistclient.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.asmirnov.quizlistclient.R;

public class MainFragment extends Fragment {

    private  TextView textInfo;

    private EditText textId;
    private EditText moduleName;
    private EditText moduleInfo;

    private Button buttonGetModules;
    private Button buttonGetModuleById;
    private Button buttonDeleteModule;
    private Button buttonCreateModule;
    private Button buttonUpdateModule;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }


}
