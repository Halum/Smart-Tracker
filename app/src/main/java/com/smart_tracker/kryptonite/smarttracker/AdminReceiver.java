package com.smart_tracker.kryptonite.smarttracker;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Halum on 11/21/2014.
 */
public class AdminReceiver extends DeviceAdminReceiver {
    static final String TAG = "Admin: ";

    @Override
    public void onEnabled(Context context, Intent intent){
        super.onEnabled(context, intent);
        Log.d(TAG, "Enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent){
        super.onDisabled(context, intent);
        Log.d(TAG, "Disabled");
    }
}
