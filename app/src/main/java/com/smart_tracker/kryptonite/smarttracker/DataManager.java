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

        return database.getString(PASSWORD_KEY, null) != null;
    }

    public static void setPassword(Activity context, String pass){
        try {
            database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
            databaseEditor = database.edit();
            pass = Encryptor.encrypt(ENCRYPTION_KEY, pass);
            databaseEditor.putString(PASSWORD_KEY, pass);
            databaseEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void storeData(Activity context, String key, String value){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
        databaseEditor = database.edit();

        try {
            key = Encryptor.encrypt(ENCRYPTION_KEY, key);
            value = Encryptor.encrypt(ENCRYPTION_KEY, value);
            databaseEditor.putString(key, value);
            databaseEditor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String retriveData(Activity context, String key){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
        String value = null;

        try {
            key = Encryptor.encrypt(ENCRYPTION_KEY, key);
            value = database.getString(key, null);
            value = Encryptor.decrypt(ENCRYPTION_KEY, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public static void removePassword(Activity context){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
        databaseEditor = database.edit();
        databaseEditor.remove(PASSWORD_KEY);
        databaseEditor.apply();
    }

    public static boolean isDataExists(Activity context, String key){
        database = context.getSharedPreferences(APP_DATABASE, MODE_PRIVATE);
        String value;
        try {
            key = Encryptor.encrypt(ENCRYPTION_KEY, key);
            value = database.getString(key, null);
            return value != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
