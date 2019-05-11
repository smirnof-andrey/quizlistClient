package com.asmirnov.quizlistclient.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.MainActivity;
import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.asmirnov.quizlistclient.service.ServerQuery;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment implements View.OnClickListener{

    private MyHttpService myHttpService;
    private Module currentModule;

    private ListView listOfModules;

    private TextView textInfo;

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
        View v = inflater.inflate(R.layout.fragment_home,container,false);

        textInfo = (TextView) v.findViewById(R.id.textInfo);

        textId = (EditText) v.findViewById(R.id.editText);
        moduleName = (EditText) v.findViewById(R.id.editModuleName);
        moduleInfo = (EditText) v.findViewById(R.id.editModuleInfo);

        buttonGetModules = (Button) v.findViewById(R.id.buttonGetModules);
        buttonGetModuleById = (Button) v.findViewById(R.id.buttonGetModuleById);
        buttonDeleteModule = (Button) v.findViewById(R.id.buttonDeleteModule);
        buttonCreateModule = (Button) v.findViewById(R.id.buttonCreateModule);
        buttonUpdateModule = (Button) v.findViewById(R.id.buttonUpdateModule);

        listOfModules = (ListView) v.findViewById(R.id.listView);

        buttonGetModules.setOnClickListener(this);
        buttonGetModuleById.setOnClickListener(this);
        buttonDeleteModule.setOnClickListener(this);
        buttonCreateModule.setOnClickListener(this);
        buttonUpdateModule.setOnClickListener(this);

        return v;
    }

    private void refreshMyHttpService() {
        MainActivity mainActivity = (MainActivity) getActivity();
        myHttpService = mainActivity.getMyHttpService();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonGetModules:
                getUserModules();
                break;
            case R.id.buttonGetModuleById:
                getModuleById();
                break;
            case R.id.buttonUpdateModule:
                updateModule();
                break;
            case R.id.buttonCreateModule:
                createModule();
                break;
            case R.id.buttonDeleteModule:
                deleteModule();
                break;
            default:
                break;
        }
    }

    private void getUserModules() {

        refreshMyHttpService();

        OkHttpClient client = myHttpService.getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

//                Map<String,String> mapJson = new HashMap<>();
//                mapJson.put("key",KEY);
//                mapJson.put("text",text.getText().toString());
//                mapJson.put("lang","en-ru");

        Call<List<Module>> call = serverQuery.getModules();

        call.enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                if (response.isSuccessful()) {

                    List<Module> modules = response.body();

                    String[] moduleNamesList = new String[modules.size()];

                    for (int i = 0; i < modules.size(); i++) {
                        moduleNamesList[i] = "["+modules.get(i).getId()+"], "+modules.get(i).getName();
                    }

                    listOfModules.setAdapter(
                            new ArrayAdapter<String>(
                                    getActivity().getApplicationContext(),  // maybe change for smth shorter?
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
                    );
                    textInfo.setText("Congrats, Success!");

                      /*      String json = response.body().toString();
                            Gson gson = new GsonBuilder().create();

                            Type type = new TypeToken<List<Module>>(){}.getType();
                            List<Module> modules = gson.fromJson(json, type);

                            ///

                            Gson gson = new Gson();
                            String data = gson.toJson(response.body)
                            ToolsItem toolsItem = gson.fromJson(data,ToolsItem.class);
                            toolItem.mConstructor(response.body());

                            */
                }else{
                    textInfo.setText("not success, server response code:"+response.raw().code());
                }
            }

            @Override
            public void onFailure(Call<List<Module>> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getModuleById() {
        refreshMyHttpService();

        OkHttpClient client = myHttpService.getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        Call<Module> call = serverQuery.getModuleById(textId.getText().toString());

        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                if (response.isSuccessful()) {

                    Module module = response.body();

                    String[] moduleNamesList = new String[1];

                    //for (int i = 0; i < modules.size(); i++) {
                    moduleNamesList[0] = "["+module.getId()+"], "+module.getName();
                    //}

                    listOfModules.setAdapter(
                            new ArrayAdapter<String>(
                                    getActivity().getApplicationContext(),
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
                    );
                    textInfo.setText("Congrats, Success!");
                }
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createModule() {
        refreshMyHttpService();
        OkHttpClient client = myHttpService.getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        currentModule = new Module(moduleName.getText().toString(),
                moduleInfo.getText().toString());

        Call<Module> call = serverQuery.createModule(currentModule);

        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                if (response.isSuccessful()) {
                    Module module = response.body();
                    if(module == null){

                    }else{
                        currentModule = module;
                        getUserModules();
                    }
                }
                textInfo.setText("response code:"+response.code());
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateModule() {
        refreshMyHttpService();
        OkHttpClient client = myHttpService.getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        Call<Module> call = serverQuery.updateModule(textId.getText().toString(),
                new Module(moduleName.getText().toString(),
                        moduleInfo.getText().toString()));

        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                //if (response.isSuccessful()) {
                textInfo.setText("response code:"+response.code());
                //}
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteModule() {
        refreshMyHttpService();
        OkHttpClient client = myHttpService.getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        Call<Integer> call = serverQuery.deleteModule(textId.getText().toString());

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                //if (response.isSuccessful()) {
                textInfo.setText("Delete successful:"+response.code());
                //}
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
