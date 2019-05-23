package com.asmirnov.quizlistclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.asmirnov.quizlistclient.service.CardListAdapter;
import com.asmirnov.quizlistclient.service.MyHttpService;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    private static final String MODULE_NAME = "name";
    private static final String MODULE_INFO = "info";
    private static final String LOG_TAG = "quizlistLogs";

    private ArrayList<Card> cardsList;
    private CardListAdapter adapter;

    private Module currentModule;
    private MyHttpService myHttpService;

    private EditText moduleName;
    private EditText textInfo;

    private ListView listViewCards;

    private FloatingActionButton fab;

    private boolean editMode;

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

        listViewCards.addHeaderView(createHeader("Cards"));

        // intent
        Intent intent = getIntent();

        editMode = intent.getBooleanExtra("editMode",false);
        setTitle((editMode ? "Edit" : "Add module"));

        try{
            Log.d(LOG_TAG,"start getting currentModule from extra");
            currentModule = intent.getParcelableExtra("currentModule");
            Log.d(LOG_TAG,"success in getting currentModule from extra");
        }catch (Exception e){
            Log.d(LOG_TAG,"fall in getting currentModule from extra");
        }

        if(currentModule==null){
            moduleName.setText("");
        }else{
            moduleName.setText(currentModule.getName());
            textInfo.setText(currentModule.getInfo());
        }

        try{
            Log.d(LOG_TAG,"start getting myHttpService from extra");
            myHttpService = intent.getParcelableExtra("myHttpService");
            Log.d(LOG_TAG,"success in getting myHttpService from extra");
        }catch (Exception e){
            Log.d(LOG_TAG,"fall in getting myHttpService from extra");
        }

        // card list
        cardsList = new ArrayList<>();
        if(editMode){
            cardsList.add(new Card(currentModule,"module's card","module's card info"));
        }else{
            cardsList.add(new Card(currentModule,"",""));
        }
        adapter = new CardListAdapter(this, cardsList);
        listViewCards.setAdapter(adapter);

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
        if(item.getItemId() == R.id.menu_ok){
            // add or update module and cards
            Toast.makeText(this, "add or update module and cards", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
