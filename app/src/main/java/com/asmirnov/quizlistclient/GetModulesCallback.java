package com.asmirnov.quizlistclient;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.asmirnov.quizlistclient.model.Module;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetModulesCallback implements Callback {

    @Override
    public void onResponse(Call call, Response response) {
        if (response.isSuccessful()) {

            /*List<Module> modules = response.body();

            String[] moduleNamesList = new String[modules.size()];

            for (int i = 0; i < modules.size(); i++) {
                moduleNamesList[i] = modules.get(i).getName();
            }

            listOfModules.setAdapter(
                    new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            moduleNamesList
                    ) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            TextView textView = (TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLUE);

                            return view;
                        }
                    }
            );*/
//                            String json = response.body().toString();
//                            Gson gson = new GsonBuilder().create();
//
//                            Type type = new TypeToken<List<Module>>(){}.getType();
//                            List<Module> modules = gson.fromJson(json, type);
        }

    }

    @Override
    public void onFailure(Call call, Throwable t) {
        t.printStackTrace();
        //textInfo.setText(t.getMessage());

    }
}
