package com.smart_tracker.kryptonite.smarttracker;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Halum on 11/22/2014.
 */
public class DataManager extends Activity {
    private static final String APP_DATABASE = "SMART_TRACKER";
    private static final String PASSWORD_KEY = "password";
    private static final String ENCRYPTION_KEY = "the key is hard to get";

    private static SharedPreferences database;
    private static SharedPreferences.Editor databaseEditor;

    public static boolean isPasswordProtected(Activity context){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);

        return database.getString(PASSWORD_KEY, null)==null ? false : true;
    }

    public static void setPassword(Activity context, String pass){
        try {
            database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
            databaseEditor = database.edit();
            pass = Encryptor.encrypt(ENCRYPTION_KEY, pass);
            databaseEditor.putString(PASSWORD_KEY, pass);
            databaseEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePassword(Activity context){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
        databaseEditor = database.edit();
        databaseEditor.remove(PASSWORD_KEY);
        databaseEditor.commit();
    }

    public static boolean doesPasswordMatch(Activity context, String givenPass){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
        String savedPass = database.getString(PASSWORD_KEY, null);
        try {
            givenPass = Encryptor.encrypt(ENCRYPTION_KEY, givenPass);
            return savedPass.equals(givenPass);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
