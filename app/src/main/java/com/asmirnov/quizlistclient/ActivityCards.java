package com.asmirnov.quizlistclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asmirnov.quizlistclient.model.Module;
import com.asmirnov.quizlistclient.service.CardListAdapter;

import java.util.ArrayList;

public class ActivityCards extends AppCompatActivity {

    private static final String MODULE_NAME = "name";
    private static final String MODULE_INFO = "info";

    private ArrayList<Module> modulesList;
    private CardListAdapter adapter;

    private ListView listViewCards;

    private TextView moduleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        moduleName = (TextView) findViewById(R.id.moduleName);
        listViewCards = (ListView) findViewById(R.id.listCards);

        // intent
        Intent intent = getIntent();
        String mName = intent.getStringExtra("currentModuleName");
        moduleName.setText(mName);

        // module list
        modulesList = new ArrayList<>();
        modulesList.add(new Module("no cards","no cards info"));
        adapter = new CardListAdapter(this, modulesList);
        listViewCards.setAdapter(adapter);

        refreshMyListByTestValues();

        listViewCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                try{
                    //currentModule = modulesList.get((int)id);
                    CharSequence tMessage = "id="+id;
                    Toast.makeText(getApplicationContext(),tMessage,Toast.LENGTH_LONG).show();
                }catch (Exception e){

                }
                //startActivity(new Intent(this, ActivityCards.class));
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
        modulesList.clear();
        for (int i = 0; i < 30;) {
            modulesList.add(new Module("test card "+ ++i,"!test card info"+i));
        }
        adapter.notifyDataSetChanged();
    }
}
