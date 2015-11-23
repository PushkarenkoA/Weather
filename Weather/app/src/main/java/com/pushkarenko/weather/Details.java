package com.pushkarenko.weather;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class Details extends AppCompatActivity {

    static final String POSITION_SELECTED = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);



        // Set the ToolBar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_hw06_toolbar_b);
        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setDisplayHomeAsUpEnabled(true);
            myActionBar.setTitle(R.string.city);
            myActionBar.setSubtitle(R.string.weather_details);
        }

        int position = getIntent().getExtras().getInt(POSITION_SELECTED);  // get position from Intent
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            Fragmentsecond newFragmentItem = new Fragmentsecond();
            newFragmentItem.setItemContent(position);
            manager.beginTransaction()
                    .add(R.id.hw06_fragment_item_b, newFragmentItem)
                    .commit();  // add Item-Fragment to its container
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_main, menu); // Inflate the menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}