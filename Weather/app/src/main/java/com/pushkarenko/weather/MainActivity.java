package com.pushkarenko.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements CallBack {

    static final String POSITION_SELECTED = "position";
    private int position = -1; // nothing has been selected
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Set the ToolBar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setIcon(R.mipmap.ic_launcher);
            myActionBar.setTitle(R.string.app_name);
            myActionBar.setSubtitle(R.string.city);
        }

        manager = getSupportFragmentManager();

        // Here we have some logic to provide smooth navigation between multiple layouts
        if (savedInstanceState == null) { // Activity started first time
            Fragmentfirst mList = new Fragmentfirst();
            manager.beginTransaction()
                    .add(R.id.list_container, mList)
                    .commit();
        }
        // Check if in two-pane-mode
        if (isInLargeLandMode()) {
            Fragmentsecond mDetails = new Fragmentsecond();
            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION_SELECTED); // restore selected position
                if (position != -1) {
                    mDetails.setItemContent(position);  // set position to show
                    manager.beginTransaction()
                            .replace(R.id.details_container, mDetails)
                            .commit();  // add appropriate Details to its container
                }
            }
        } else {
            // Check if activity restored after reconfiguration
            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION_SELECTED); // restore selected position
            }
        }
    }

    @Override
    public void updateContent(int position) {

        Fragmentsecond newFragmentItem = new Fragmentsecond();
        newFragmentItem.setItemContent(position);
        this.position = position; // save position
        // Check if in landscape-mode
        if (!isInLargeLandMode()) {
            Intent intent = new Intent(this, Details.class);
            intent.putExtra(POSITION_SELECTED, position);
            startActivity(intent);
        } else {
            manager.beginTransaction()
                    .replace(R.id.details_container, newFragmentItem)
                    .commit();  // add appropriate Details to its container
        }
    }

    @Override // Here we save selected position
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(POSITION_SELECTED, position);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Returns TRUE if Device is in LAND-mode and has large screen
    private boolean isInLargeLandMode() {
        return getResources().getBoolean(R.bool.has_two_panes);
    }
}
