package com.asmirnov.quizlistclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.asmirnov.quizlistclient.UI.EditActivity;
import com.asmirnov.quizlistclient.service.DataAccessProvider;
import com.asmirnov.quizlistclient.UI.fragments.AccountFragment;
import com.asmirnov.quizlistclient.UI.fragments.MainFragment;
import com.asmirnov.quizlistclient.UI.fragments.SearchFragment;
import com.asmirnov.quizlistclient.service.MyHttpService;
import com.google.gson.Gson;

import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private final String SAVED_TEXT = "saved_URL";
    private final String SAVED_TOKEN = "saved_token";
    private final String LAST_CHECK_DATE = "last_Check_Date";
    private Fragment currentFragment;

    DataAccessProvider dataAccessProvider;

    private MyHttpService myHttpService;

    public MyHttpService getMyHttpService(){
        return myHttpService;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            currentFragment = null;
            boolean startActivity = false;
            switch(menuItem.getItemId()){
                case R.id.navigation_home:
                    currentFragment = new MainFragment();
                   break;
                case R.id.navigation_search:
                    currentFragment = new SearchFragment();
                    break;
                case R.id.navigation_edit:
                    startActivity = true;
                    break;
                case R.id.navigation_account:
                    currentFragment = new AccountFragment();
                    break;
            }
            if(currentFragment==null && startActivity){
                // !change selected menu item to previous one
                startEditActivity();
            }else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, currentFragment)
                        .commit();
            }
            return true;
        }
    };

    private void startEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("editMode", false);
        intent.putExtra("myHttpService", myHttpService);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataAccessProvider = new DataAccessProvider(this);
        createMyHttpService();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,new MainFragment())
                .commit();

        if(!dataAccessProvider.userIsLogged()){
            currentFragment = new AccountFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
        }

    }

    private void createMyHttpService() {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        String textURL = sPref.getString(SAVED_TEXT, "");
        String textToken = sPref.getString(SAVED_TOKEN, "");
        String textLastCheckDate = sPref.getString(LAST_CHECK_DATE, "");
        Gson gson = new Gson();
        Date lastCheckDate = gson.fromJson(textLastCheckDate, Date.class);

        // block for check token

        myHttpService = new MyHttpService(textURL,textToken,lastCheckDate);
//        myHttpService =  MyHttpService.getInstance();
//        myHttpService.setURL(textURL);
//        myHttpService.update(textToken,null,false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Add new module");
        menu.add("Get user modules");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().toString().equals("Add new module")){
            startEditActivity();
        }else if(item.getTitle().toString().equals("Get user modules")){
            currentFragment = new MainFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

}
