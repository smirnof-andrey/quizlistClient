package com.asmirnov.quizlistclient.UI.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.UI.ActivityCards;
import com.asmirnov.quizlistclient.MainActivity;
import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.dto.ModuleAdditionalInfo;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.DataAccessProvider;
import com.asmirnov.quizlistclient.service.ModuleListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.asmirnov.quizlistclient.service.ServerQuery;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment{

    private static final String TAG = "quizlistLogs";

    private MyHttpService myHttpService;

    private ArrayList<ModuleAdditionalInfo> modulesList;
    private ModuleListAdapter adapter;

    private ListView listViewModules;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);

        listViewModules = (ListView) v.findViewById(R.id.listView);
        listViewModules.addHeaderView(createHeader("Modules"));

        // module list
        modulesList = new ArrayList<>();
        modulesList.add(new ModuleAdditionalInfo(new Module(0,"getting modules..."," "),0));
        adapter = new ModuleListAdapter(getActivity(), modulesList);
        listViewModules.setAdapter(adapter);

        listViewModules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                try{
                    Module currentModule = modulesList.get((int)id).getModule();
                    Intent intent = new Intent(getActivity(), ActivityCards.class);
                    intent.putExtra("currentModule", currentModule);
                    intent.putExtra("myHttpService", myHttpService);
                    startActivity(intent);
                }catch (Exception e){
                    Log.d(TAG, "onItemClick: error in getting module form list. id="+id);
                }
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserModules();
    }

    private void refreshMyHttpService() {
        MainActivity mainActivity = (MainActivity) getActivity();
        myHttpService = mainActivity.getMyHttpService();
    }

    private void refreshMyListByTestValues(){
        modulesList.clear();
        for (int i = 0; i < 30;) {
            modulesList.add(new ModuleAdditionalInfo(new Module(0,"test module "+ ++i,"!test module info"+i),0));
        }

        adapter.notifyDataSetChanged();
    }

    View createHeader(String text) {
        View view = getLayoutInflater().inflate(R.layout.list_header, null);
        ((TextView)view.findViewById(R.id.header_text)).setText(text);
        return view;
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

        Call<List<ModuleAdditionalInfo>> call = serverQuery.getModules();

        call.enqueue(new Callback<List<ModuleAdditionalInfo>>() {
            @Override
            public void onResponse(Call<List<ModuleAdditionalInfo>> call, Response<List<ModuleAdditionalInfo>> response) {
                if (response.isSuccessful()) {

                    modulesList.clear();
                    if(response.body().size() > 0) {
                        modulesList.addAll((ArrayList<ModuleAdditionalInfo>) response.body());
                    }

                    adapter.notifyDataSetChanged();

                }else{
                    int rawCode = response.raw().code();
                    switch (rawCode){
                        case 500:{
                            Log.d(TAG, "not valid token. need to refresh it!");
                            myHttpService.refreshUserToken(new DataAccessProvider(getActivity()));
                            Toast.makeText(getActivity().getApplicationContext(), "refreshing token. try again later", Toast.LENGTH_LONG).show();
//                            Toast.makeText(getActivity().getApplicationContext(), "not valid token. need to refresh it!", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                    Log.d(TAG, "getting user modules from srv. error. server response code:"+rawCode);
                }
            }

            @Override
            public void onFailure(Call<List<ModuleAdditionalInfo>> call, Throwable t) {
                t.printStackTrace();
                String msg = t.getMessage();
                Log.d(TAG,"getting user modules from srv. error:"+msg);
//                if(!msg.equals("")){
//                    Toast.makeText(getActivity().getApplicationContext(),msg,Toast.LENGTH_LONG).show();
//                }
//                refreshMyListByTestValues();
            }
        });
    }
}
