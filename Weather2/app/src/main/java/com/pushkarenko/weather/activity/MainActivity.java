package com.pushkarenko.weather.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.pushkarenko.weather.callbacknterface.CallBackInterface;
import com.pushkarenko.weather.fragment.DetailsFragment;
import com.pushkarenko.weather.fragment.List_Fragment;
import com.pushkarenko.weather.R;
import com.pushkarenko.weather.service.ServiceNotification;

public class MainActivity extends AppCompatActivity implements CallBackInterface
{
    static final String POSITION_SELECTED = "position";
    private int position = -1;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (ServiceNotification.state) {
            startService(new Intent(this, ServiceNotification.class));
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(myToolbar);
        android.support.v7.app.ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setTitle(R.string.app_name);

        }
        manager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            List_Fragment mList = new List_Fragment();
            manager.beginTransaction()
                    .add(R.id.list_container, mList)
                    .commit();
        }


        if (withDetails()) {
            DetailsFragment mDetails = new  DetailsFragment();
            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION_SELECTED);
                if (position != -1) {
                    mDetails.setItemContent(position);
                    manager.beginTransaction()
                            .replace(R.id.details_container, mDetails)
                            .commit();
                }
            }
        } else {
            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(POSITION_SELECTED);
            }
        }
    }

    @Override
    public void updata(int position) {

        DetailsFragment newFragmentItem = new DetailsFragment();
        newFragmentItem.setItemContent(position);
        this.position = position;
        if (withDetails())
        {
            manager.beginTransaction()
                    .replace(R.id.details_container, newFragmentItem)
                    .commit();
        } else {
            Intent intent = new Intent(this, Details_Activity.class);
            intent.putExtra(POSITION_SELECTED, position);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(POSITION_SELECTED, position);
        super.onSaveInstanceState(savedInstanceState);
    }

    private boolean withDetails() {
        boolean with_Details = true;
        with_Details = (findViewById(R.id.details_container) != null);
        return   (with_Details);
    }

}