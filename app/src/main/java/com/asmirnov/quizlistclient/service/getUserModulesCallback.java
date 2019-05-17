package com.asmirnov.quizlistclient.service;

import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;

import com.asmirnov.quizlistclient.model.Module;

import java.util.ArrayList;
import java.util.List;

public class getUserModulesCallback<T> implements retrofit2.Callback<T> {

    Call<List<Module>> call;

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {

            List<Module> modules = (List<Module>) response.body();
//
//            modulesList.clear();
//            modulesList.addAll((ArrayList<Module>) modules);
//
//            adapter.notifyDataSetChanged();
//
//            textInfo.setText("Congrats, Success!");

        }else{
//            int rawCode = response.raw().code();
//            switch (rawCode){
//                case 500:{
//                    // we have to refresh token
//                    break;
//                }
//            }
//            textInfo.setText("not success, server response code:"+rawCode);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
//        textInfo.setText(t.getMessage());
//        Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
    }
}
