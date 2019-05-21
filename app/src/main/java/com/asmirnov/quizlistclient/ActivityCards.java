package com.asmirnov.quizlistclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.CardListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCards extends AppCompatActivity {

    private static final String MODULE_NAME = "name";
    private static final String MODULE_INFO = "info";
    private static final String LOG_TAG = "quizlistLogs";

    private ArrayList<Card> cardsList;
    private CardListAdapter adapter;

    private Module currentModule;
    private MyHttpService myHttpService;

    private ListView listViewCards;

    private TextView moduleName;
    private TextView textInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        moduleName = (TextView) findViewById(R.id.moduleName);
        textInfo = (TextView) findViewById(R.id.textInfo2);

        listViewCards = (ListView) findViewById(R.id.listCards);

        // intent
        Intent intent = getIntent();
        String mName = intent.getStringExtra("currentModuleName");
        moduleName.setText(mName);

        String currentModuleId = intent.getStringExtra("currentModuleId");
        currentModule = new Module(Integer.parseInt(currentModuleId),mName,"test");

        try{
            Log.d(LOG_TAG,"start getting currentModule from extra");
            currentModule = intent.getParcelableExtra("currentModule");
            Log.d(LOG_TAG,"success in getting currentModule from extra");
        }catch (Exception e){
            Log.d(LOG_TAG,"fall in getting currentModule from extra");
        }

        try{
            Log.d(LOG_TAG,"start getting myHttpService from extra");
            myHttpService = intent.getParcelableExtra("myHttpService");
            Log.d(LOG_TAG,"success in getting myHttpService from extra");
        }catch (Exception e){
            Log.d(LOG_TAG,"fall in getting myHttpService from extra");
        }

        // module list
        cardsList = new ArrayList<>();
        cardsList.add(new Card(currentModule,"no cards","no cards info"));
        adapter = new CardListAdapter(this, cardsList);
        listViewCards.setAdapter(adapter);

//        refreshMyListByTestValues();

        getCardsByModule();

        listViewCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                try{
                    //currentCard = modulesList.get((int)id);
                    CharSequence tMessage = "id="+id;
                    Toast.makeText(getApplicationContext(),tMessage,Toast.LENGTH_LONG).show();
                }catch (Exception e){

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Edit");
        menu.add("Delete");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private void refreshMyListByTestValues(){
        cardsList.clear();
        for (int i = 0; i < 30;) {
            cardsList.add(new Card(currentModule,"test card "+ ++i,"!test card info"+i));
        }
        adapter.notifyDataSetChanged();
    }

    private void getCardsByModule(){

        Call<List<Card>> call = myHttpService.getServerQuery().getCards(currentModule.getId().toString());

        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                if (response.isSuccessful()) {

                    try {
                        List<Card> cards = response.body();

                        cardsList.clear();
                        cardsList.addAll((ArrayList<Card>) cards);

                        adapter.notifyDataSetChanged();

                        textInfo.setText("Congrats, Success!");
                    }catch (Exception e){

                    }

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
                            // we have to refresh token
                            break;
                        }
                    }
                    textInfo.setText("not success, server response code:"+rawCode);
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                t.printStackTrace();
                textInfo.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
