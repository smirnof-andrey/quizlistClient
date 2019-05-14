package com.asmirnov.quizlistclient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.asmirnov.quizlistclient.fragments.AccountFragment;
import com.asmirnov.quizlistclient.fragments.MainFragment;
import com.asmirnov.quizlistclient.fragments.SearchFragment;
import com.asmirnov.quizlistclient.service.MyHttpService;

public class MainActivity extends AppCompatActivity{

    private final String SAVED_TEXT = "saved_URL";
    private final String SAVED_TOKEN = "saved_token";

    private MyHttpService myHttpService;

    public MyHttpService getMyHttpService(){
        return myHttpService;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment currentFragment = null;
            switch(menuItem.getItemId()){
                case R.id.navigation_home:
                    currentFragment = new MainFragment();
                   break;
                case R.id.navigation_search:
                    currentFragment = new SearchFragment();
                    break;
                case R.id.navigation_account:
                    currentFragment = new AccountFragment();
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,currentFragment)
                    .commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createMyHttpService();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,new MainFragment())
                .commit();
    }

    private void createMyHttpService() {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        String textURL = sPref.getString(SAVED_TEXT, "");

        String textToken = sPref.getString(SAVED_TOKEN, "");

        // block for check token

        myHttpService = new MyHttpService(textURL,textToken);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Add new module");
        menu.add("Delete module");
        menu.add("test: get module by id");
        menu.add("test: update module");
        menu.add("test: get user modules");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}
