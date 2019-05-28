package com.asmirnov.quizlistclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.MyAdapterInterface;
import com.asmirnov.quizlistclient.service.MyCardListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements MyAdapterInterface {
    private static final String MODULE_NAME = "name";
    private static final String MODULE_INFO = "info";
    private static final String TAG = "quizlistLogs";

    private boolean editMode;

    private ArrayList<Card> cardsList;
    private MyCardListAdapter adapter;

    private Module currentModule;
    private MyHttpService myHttpService;

    private EditText moduleName;
    private EditText textInfo;

    private ListView listViewCards;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        moduleName = (EditText) findViewById(R.id.moduleName);
        textInfo = (EditText) findViewById(R.id.textInfo2);

        listViewCards = (ListView) findViewById(R.id.listCards);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsList.add(new Card(currentModule,"",""));
                adapter.notifyDataSetChanged();
            }
        });

        moduleName.setText("");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // intent
        Intent intent = getIntent();

        editMode = intent.getBooleanExtra("editMode",false);
        setTitle((editMode ? "Edit" : "Add new module"));

        try{
//            Log.d(TAG,"start getting currentModule from extra");
            currentModule = intent.getParcelableExtra("currentModule");
//            Log.d(TAG,"success in getting currentModule from extra");
        }catch (Exception e){
            Log.d(TAG,"fall in getting currentModule from extra");
        }

        if(currentModule==null){
            moduleName.setText("");
        }else{
            moduleName.setText(currentModule.getName());
            textInfo.setText(currentModule.getInfo());
        }

        try{
//            Log.d(TAG,"start getting myHttpService from extra");
            myHttpService = intent.getParcelableExtra("myHttpService");
//            Log.d(TAG,"success in getting myHttpService from extra");
        }catch (Exception e){
            Log.d(TAG,"fall in getting myHttpService from extra");
        }

        // card list
        if(editMode){
            try{
//                Log.d(TAG,"start getting cardsList from extra");
                cardsList = intent.getParcelableArrayListExtra("cardsList");
//                Log.d(TAG,"success in getting cardsList from extra");
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
        cardsList.add(new Card(currentModule,"",""));
        cardsList.add(new Card(currentModule,"",""));
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
                Toast.makeText(this, "add or update module and cards", Toast.LENGTH_SHORT).show();
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateCardList(int position, String text) {
        Card currentCard = cardsList.get(position);
        if(!currentCard.getTerm().equals(text)){
            cardsList.get(position).setTerm(text);
//            Log.d(TAG, "!updateEditText: position:"+position+", text:"+text);
        }
    }
}
