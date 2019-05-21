package com.asmirnov.quizlistclient.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.ActivityCards;
import com.asmirnov.quizlistclient.MainActivity;
import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.ModuleListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.asmirnov.quizlistclient.service.ServerQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String LOG_TAG = "quizlistLogs";

    private MyHttpService myHttpService;
    private Module currentModule;

    private ArrayList<Module> modulesList;
    private ModuleListAdapter adapter;
    private List<Module> modules;

    private ListView listViewModules;

    private TextView textInfo;

    private EditText textId;
    private EditText moduleName;
    private EditText moduleInfo;

    private Button buttonGetModules;
    private Button buttonGetModuleById;
    private Button buttonDeleteModule;
    private Button buttonCreateModule;
    private Button buttonUpdateModule;
    private Button createCard;


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
        createCard = (Button) v.findViewById(R.id.createCard);

        listViewModules = (ListView) v.findViewById(R.id.listView);

        buttonGetModules.setOnClickListener(this);
        buttonGetModuleById.setOnClickListener(this);
        buttonDeleteModule.setOnClickListener(this);
        buttonCreateModule.setOnClickListener(this);
        buttonUpdateModule.setOnClickListener(this);
        createCard.setOnClickListener(this);

        // module list
        modulesList = new ArrayList<>();
        currentModule = new Module(12,"no modules","no modules info");
        modulesList.add(currentModule);
        adapter = new ModuleListAdapter(getActivity(), modulesList);
        listViewModules.setAdapter(adapter);

        listViewModules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                try{
                    currentModule = modulesList.get((int)id);
                }catch (Exception e){

                }

                Intent intent = new Intent(getActivity(), ActivityCards.class);
                intent.putExtra("currentModuleName", currentModule.getId().toString()+". "+currentModule.getName());
                intent.putExtra("currentModuleId", currentModule.getId().toString());
                intent.putExtra("currentModule", currentModule);
                intent.putExtra("myHttpService", myHttpService);

                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserModules();
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
            case R.id.createCard:
                createCard();
                break;
            default:
                break;
        }
    }

    private void refreshMyListByTestValues(){
        modulesList.clear();
        for (int i = 0; i < 30;) {
            modulesList.add(new Module("test module "+ ++i,"!test module info"+i));
        }

        adapter.notifyDataSetChanged();
    }

    private void getUserModules() {

        //refreshMyHttpService();
//        myHttpService = MyHttpService.getInstance();

        refreshMyHttpService();

        OkHttpClient client = myHttpService.getHttpClient().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(myHttpService.getURL())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ServerQuery serverQuery = retrofit.create(ServerQuery.class);

        Call<List<Module>> call = serverQuery.getModules();

        call.enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                if (response.isSuccessful()) {

                    modules = response.body();

                    modulesList.clear();
                    modulesList.addAll((ArrayList<Module>) modules);

                    adapter.notifyDataSetChanged();

                    textInfo.setText("Congrats, Success!");

//                            String json = response.body().toString();
//                            Gson gson = new GsonBuilder().create();
//
//                            Type type = new TypeToken<List<Module>>(){}.getType();
//                            List<Module> modules = gson.fromJson(json, type);
//
//                            ///
//
//                            Gson gson = new Gson();
//                            String data = gson.toJson(response.body)
//                            ToolsItem toolsItem = gson.fromJson(data,ToolsItem.class);
//                            toolItem.mConstructor(response.body());

                }else{
                    int rawCode = response.raw().code();
                    switch (rawCode){
                        case 500:{
                            Log.d(LOG_TAG, "not valid token. need to refresh it!");
                            break;
                        }
                    }
                    textInfo.setText("not success, server response code:"+rawCode);
                    Log.d(LOG_TAG, "getting user modules from srv. error. server response code:"+rawCode);
                }
            }

            @Override
            public void onFailure(Call<List<Module>> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG,"getting user modules from srv. error:"+t.getMessage());
            }
        });
    }

    private void createCard() {
        refreshMyHttpService();

        Module choosenModule=null;
        Integer currId = Integer.parseInt(textId.getText().toString());
        for (Module module : modules) {
            if(module.getId() == currId){
                choosenModule = module;
            }
        }
        if(choosenModule == null){
            return;
        }

        Call<Card> call = myHttpService.getServerQuery().createCard(textId.getText().toString(),
                new Card(choosenModule,"term_1","value_1"));

        call.enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                if (response.isSuccessful()) {

                    Card card = response.body();

                    textInfo.setText("Congrats, Success!");
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getModuleById() {
        refreshMyHttpService();

        Call<Module> call = myHttpService.getServerQuery().getModuleById(textId.getText().toString());

        call.enqueue(new Callback<Module>() {
            @Override
            public void onResponse(Call<Module> call, Response<Module> response) {
                if (response.isSuccessful()) {

                    modules.clear();
                    try {
                        modules.add(response.body());

                        modulesList.clear();
                        modulesList.addAll((ArrayList<Module>) modules);

                        adapter.notifyDataSetChanged();

                        textInfo.setText("Congrats, Success!");
                    }catch(Exception e){
                        textInfo.setText("Exception in getting module");
                    }
                }else{
                    int rawCode = response.raw().code();
                    switch (rawCode){
                        case 500:{
                            // we have to refresh token
                            break;
                        }
                    }
                    textInfo.setText("not success, server response code:"+rawCode);
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

        currentModule = new Module(moduleName.getText().toString(),
                moduleInfo.getText().toString());

        Call<Module> call = myHttpService.getServerQuery().createModule(currentModule);

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

        Call<Module> call = myHttpService.getServerQuery().updateModule(textId.getText().toString(),
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

        Call<Integer> call = myHttpService.getServerQuery().deleteModule(textId.getText().toString());

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
