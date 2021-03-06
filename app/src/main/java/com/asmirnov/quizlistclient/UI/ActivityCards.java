package com.asmirnov.quizlistclient.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.MyCardListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCards extends AppCompatActivity{

    private static final String MODULE_NAME = "name";
    private static final String MODULE_INFO = "info";
    private static final String TAG = "quizlistLogs";
    private static final Integer EDIT_REQUEST_CODE = 1;

    private ArrayList<Card> cardsList;
    private MyCardListAdapter adapter;

    private Module currentModule;
    private MyHttpService myHttpService;

    private ListView listViewCards;

    private TextView currentInfo;
    private TextView textInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        currentInfo = (TextView) findViewById(R.id.moduleName);
        textInfo = (TextView) findViewById(R.id.textInfo2);

        listViewCards = (ListView) findViewById(R.id.listCards);
        listViewCards.addHeaderView(createHeader("Cards"));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // intent
        Intent intent = getIntent();

        try{
            currentModule = intent.getParcelableExtra("currentModule");
            setTitle(currentModule.getName());
            textInfo.setText(currentModule.getInfo());
        }catch (Exception e){
            Log.d(TAG,"fall in getting currentModule from extra");
        }

        try{
            myHttpService = intent.getParcelableExtra("myHttpService");
        }catch (Exception e){
            Log.d(TAG,"fall in getting myHttpService from extra");
        }

        // card list
        cardsList = new ArrayList<>();
        cardsList.add(new Card(currentModule,"no cards","no cards info"));
        adapter = new MyCardListAdapter(this, cardsList);
        listViewCards.setAdapter(adapter);

//        refreshMyListByTestValues();

        getCardsByModule();

        listViewCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                try{
                    //currentCard = modulesList.get((int)id);
//                    Toast.makeText(getApplicationContext(),"id="+id,Toast.LENGTH_LONG).show();
                }catch (Exception e){

                }
            }
        });
    }

    View createHeader(String text) {
        View view = getLayoutInflater().inflate(R.layout.list_header, null);
        ((TextView)view.findViewById(R.id.header_text)).setText(text);
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_edit:
                startEditActivity();
                break;
            case R.id.menu_delete:
                // dialog!
                deleteModule();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMyListByTestValues(){
        cardsList.clear();
        for (int i = 0; i < 30;) {
            cardsList.add(new Card(++i,currentModule,"test card "+i,"!test card info"+i));
        }
        adapter.notifyDataSetChanged();
    }

    private void startEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("editMode", true);
        intent.putExtra("currentModule", currentModule);
        intent.putExtra("cardsList", cardsList);
        intent.putExtra("myHttpService", myHttpService);
        startActivityForResult(intent,EDIT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == EDIT_REQUEST_CODE){
            try {
                currentModule = data.getParcelableExtra("currentModule");
                setTitle(currentModule.getName());
                currentInfo.setText("");
                textInfo.setText(currentModule.getInfo());
                getCardsByModule();
            } catch (Exception e) {
                Log.d(TAG, "ActivityCards: fall in getting module from extra");
            }
        }
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

                        currentInfo.setText("");
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
                            Log.d(TAG, "not valid token. need to refresh it!");
                            break;
                        }
                    }
                    currentInfo.setText("not success, server response code:"+rawCode);
                }
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                t.printStackTrace();
                currentInfo.setText(t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteModule() {

        if(currentModule == null || currentModule.getId() == 0){
            Log.d(TAG, "delete module. error:no current module or empty id");
        }else {
            Call<Integer> call = myHttpService.getServerQuery().deleteModule(currentModule.getId().toString());

            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    //if (response.isSuccessful()) {
                    Log.d(TAG, "delete module: success");
                    finish();
                    //}
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d(TAG, "delete module. error:"+t.getMessage());
                    finish();
                }
            });
        }
    }

}
