package com.smart_tracker.kryptonite.smarttracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class Security extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(DataManager.isPasswordProtected(this))
            setContentView(R.layout.layout_login);
        else
            setContentView(R.layout.layout_signup);
    }

    public void signUp(View v){
        EditText newPasswordField = (EditText) findViewById(R.id.newPassword);
        EditText repeatedPasswordField = (EditText) findViewById(R.id.repeatNewPassword);

        String newPassword = newPasswordField.getText().toString();
        String repeatedPassword = repeatedPasswordField.getText().toString();

        if(newPassword.length()==0){
            this.alert("Please enter a password");
        }else if(newPassword.equals(repeatedPassword)==false){
            this.alert("Password doesn't match");
        }else{
            DataManager.setPassword(this, newPassword);
            if(DataManager.isPasswordProtected(this)) {
                this.loadNextActivity();
            }else{
                this.alert("Internal error, try again");
            }
        }
    }

    public void logIn(View v){
        EditText passwordField = (EditText) findViewById(R.id.password);
        String givenPassword = passwordField.getText().toString();

        if(givenPassword.length()==0){
            alert("Please enter password");
        }else if(DataManager.doesPasswordMatch(this, givenPassword)==false){
            alert("Password doesn't match");
        }else{
            this.loadNextActivity();
        }
    }

    private void loadNextActivity(){
        Intent nextActivity = new Intent(this, Menu.class);
        nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextActivity);
    }

    private void alert(String val){
        Toast.makeText(this, val, Toast.LENGTH_SHORT).show();
    }
}
