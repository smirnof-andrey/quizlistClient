package com.asmirnov.quizlistclient.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.MyAdapterInterface;
import com.asmirnov.quizlistclient.service.MyCardListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActivity extends AppCompatActivity implements MyAdapterInterface {
    private static final String MODULE_NAME = "name";
    private static final String MODULE_INFO = "info";
    private static final String TAG = "quizlistLogs";
    private static final Integer EDIT_REQUEST_CODE = 1;

    private boolean editMode;

    private ArrayList<Card> cardsList;
    private MyCardListAdapter adapter;

    private Module currentModule;
    private MyHttpService myHttpService;

    private EditText viewModuleName;
    private EditText viewModuleInfo;

    private ListView listViewCards;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        viewModuleName = (EditText) findViewById(R.id.moduleName);
        viewModuleInfo = (EditText) findViewById(R.id.textInfo2);
        viewModuleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    Log.d(TAG, "beforeTextChanged: s:" + s);
                    currentModule.setName(s.toString());
                }
            }
        });
        viewModuleInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    Log.d(TAG, "beforeTextChanged: s:" + s);
                    currentModule.setInfo(s.toString());
                }
            }
        });

        listViewCards = (ListView) findViewById(R.id.listCards);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsList.add(new Card(0,currentModule,"",""));
                adapter.notifyDataSetChanged();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // intent
        Intent intent = getIntent();

        editMode = intent.getBooleanExtra("editMode",false);
        setTitle((editMode ? "Edit" : "Add new module"));

        if(editMode) {
            try {
                currentModule = intent.getParcelableExtra("currentModule");
            } catch (Exception e) {
                Log.d(TAG, "fall in getting currentModule from extra");
            }
        }else{
            currentModule = new Module("","");
        }

        if(currentModule==null){
            viewModuleName.setText("Error, no module!");
        }else{
            viewModuleName.setText(currentModule.getName());
            viewModuleInfo.setText(currentModule.getInfo());
        }

        try{
            myHttpService = intent.getParcelableExtra("myHttpService");
        }catch (Exception e){
            Log.d(TAG,"fall in getting myHttpService from extra");
        }

        // card list
        if(editMode){
            try{
                cardsList = intent.getParcelableArrayListExtra("cardsList");
            }catch (Exception e){
                cardsList = getListOfTwoEmptyCards(currentModule);
                Log.d(TAG,"fall in getting cardsList from extra");
            }
        }else{
            cardsList = getListOfTwoEmptyCards(currentModule);
        }

        adapter = new MyCardListAdapter(this, cardsList,this);
        listViewCards.setAdapter(adapter);

        listViewCards.addHeaderView(createHeader("Cards"));
    }

    private ArrayList<Card> getListOfTwoEmptyCards(Module currentModule) {
        ArrayList<Card> cardsList = new ArrayList<>();
        cardsList.add(new Card(0,currentModule,"",""));
        cardsList.add(new Card(0,currentModule,"",""));
        return cardsList;
    }

    View createHeader(String text) {
        View view = getLayoutInflater().inflate(R.layout.list_header, null);
        ((TextView)view.findViewById(R.id.header_text)).setText(text);
        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_ok:
                // add or update module and cards
//                Toast.makeText(this, "add or update module and cards", Toast.LENGTH_SHORT).show();
                updateCardListInServer();
                break;
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateCardListTerm(int position, String text, boolean itIsTerm) {
        Card currentCard = cardsList.get(position);
        if(itIsTerm) {
            if (!currentCard.getTerm().equals(text)) {
                cardsList.get(position).setTerm(text);
            }
        }else{
            if (!currentCard.getValue().equals(text)) {
                cardsList.get(position).setValue(text);
            }
        }
    }

    private void updateCardListInServer() {
        boolean needUpdate;
        // compare card lists
        updateModuleCards();

    }

    private void updateModuleCards() {

        Map<String, Object> map = new HashMap();
        map.put("module", currentModule);
        map.put("cardsList", cardsList);

        Call<String> call;
        if(editMode) {
            call = myHttpService.getServerQuery().updateCards(
                    currentModule.getId().toString(),
                    map
            );
        }else{
            call = myHttpService.getServerQuery().createCards(map);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String resp = response.body();
                    if(!resp.isEmpty()){
                        Log.d(TAG, "Update module cards error. ErrorMessage:" + resp
                                + ", server code:" + response.code());
                    }
                }
                finishWithAnswer();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                finishWithAnswer();
            }
        });
    }

    private void finishWithAnswer() {
        if(editMode) {
            Intent answer = new Intent();
            answer.putExtra("currentModule", currentModule);
            setResult(EDIT_REQUEST_CODE, answer);
        }
        finish();
    }

    @Override
    public void deleteCard(int position) {
        cardsList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
