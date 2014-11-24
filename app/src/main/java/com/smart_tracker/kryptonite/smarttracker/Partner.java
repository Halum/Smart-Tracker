package com.smart_tracker.kryptonite.smarttracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;


public class Partner extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_partner);
        if(AdminManager.isAdmin(this) == false)
            AdminManager.askForAdminPermission(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }
}
