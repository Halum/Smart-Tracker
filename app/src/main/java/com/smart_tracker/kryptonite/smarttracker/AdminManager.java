package com.smart_tracker.kryptonite.smarttracker;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Halum on 11/24/2014.
 */
public class AdminManager {
    private static DevicePolicyManager policyManager;
    private static ComponentName deviceAdmin;

    private static void initForAdminAccess(Activity context){
            policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            deviceAdmin = new ComponentName(context, AdminReceiver.class);
    }

    public static void askForAdminPermission(Activity context){
        initForAdminAccess(context);
        Intent adminRequest = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        adminRequest.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
        adminRequest.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This is required");
        context.startActivity(adminRequest);
    }

    public static boolean isAdmin(Activity context){
        initForAdminAccess(context);
        return policyManager.isAdminActive(deviceAdmin);
    }
}
