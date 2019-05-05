package com.asmirnov.quizlistclient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.model.AuthResponse;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.ServerQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    private final String URL = "http://192.168.0.102:8080";
    private String URL;
    private String token;
    private Module currentModule;

    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private ListView listOfModules;

    private  TextView tokenView;
    private  TextView textInfo;

    private EditText textId;
    private EditText username;
    private EditText password;
    private EditText moduleName;
    private EditText moduleInfo;
    private EditText httpURL;

    private Button buttonGetModules;
    private Button buttonGetModuleById;
    private Button buttonGetToken;
    private Button buttonDeleteModule;
    private Button buttonCreateModule;
    private Button buttonUpdateModule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenView = (TextView) findViewById(R.id.tokenView);
        textInfo = (TextView) findViewById(R.id.textInfo);

        textId = (EditText) findViewById(R.id.editText);
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        moduleName = (EditText) findViewById(R.id.editModuleName);
        moduleInfo = (EditText) findViewById(R.id.editModuleInfo);
        httpURL = (EditText) findViewById(R.id.httpURL);

        buttonGetModules = (Button) findViewById(R.id.buttonGetModules);
        buttonGetModuleById = (Button) findViewById(R.id.buttonGetModuleById);
        buttonGetToken = (Button) findViewById(R.id.buttonGetToken);
        buttonDeleteModule = (Button) findViewById(R.id.buttonDeleteModule);
        buttonCreateModule = (Button) findViewById(R.id.buttonCreateModule);
        buttonUpdateModule = (Button) findViewById(R.id.buttonUpdateModule);

        listOfModules = (ListView) findViewById(R.id.listView);

        URL = httpURL.getText().toString();
        httpURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                URL = httpURL.getText().toString();
            }
        });

        buttonGetToken.setOnClickListener(this);
        buttonGetModules.setOnClickListener(this);
        buttonGetModuleById.setOnClickListener(this);
        buttonDeleteModule.setOnClickListener(this);
        buttonCreateModule.setOnClickListener(this);
        buttonUpdateModule.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        textInfo.setText("");

        switch(v.getId()){
            case R.id.buttonGetToken:
                onButtonGetTokenClick(v);
                break;
            case R.id.buttonGetModules:
                onButtonGetModulesClick(v);
                break;
            case R.id.buttonGetModuleById:
                onButtonGetModuleByIdClick(v);
                break;
            case R.id.buttonDeleteModule:
                onButtonDeleteModuleClick(v);
                break;
            case R.id.buttonCreateModule:
                onButtonCreateModuleClick(v);
                break;
            case R.id.buttonUpdateModule:
                onButtonUpdateModuleClick(v);
                break;
                default:
                    break;
        }
    }

    private void onButtonGetTokenClick(View v) {
        getToken();
    }
    private void onButtonGetModuleByIdClick(View v) {
        getModuleById();
    }
    private void onButtonUpdateModuleClick(View v) {
        updateModule();
    }
    private void onButtonCreateModuleClick(View v) {
        createModule();
    }
    private void onButtonDeleteModuleClick(View v) {
        deleteModule();
    }
    private void onButtonGetModulesClick(View v) {
        getUserModules();
    }


    private void getToken() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        Call<AuthResponse> callToken = serverQuery.getUserToken(username.getText().toString(),password.getText().toString());

        callToken.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {

                    AuthResponse authResponse = response.body();
                    if(authResponse == null){
                        // do smth
                    }
                    String newToken = authResponse.getToken();
                    if(newToken == null || newToken.isEmpty()){
                        // do smth
                    }else{
                        setTokenInHttpHeader(newToken);
                        getUserModules();
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                t.printStackTrace();
                tokenView.setText(t.getMessage());
                //Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void setTokenInHttpHeader(String newToken) {
        token = newToken;
        tokenView.setText("token:"+token);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("X-Auth-Token", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
    }

    private void getUserModules() {

        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
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
                }
            }

            @Override
            public void onFailure(Call<List<Module>> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createModule() {
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
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
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void getModuleById() {
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
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
                    );
                    textInfo.setText("Congrats, Success!");
                }
            }

            @Override
            public void onFailure(Call<Module> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateModule() {
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
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
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteModule() {
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
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
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }



}
