package com.smart_tracker.kryptonite.smarttracker;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class Menu extends Activity implements CompoundButton.OnCheckedChangeListener,OnClickListener {
    Switch location_track;
    Button set_keyword;
    Switch sim_change;
    Switch data_erase;
    Button set_erase_key;
    Button submit;
    TextView learn_tracking;
    TextView learn_sim_change;

    ImageView info_tracking;
    ImageView info_sim_change;
    ImageView info_erase;

    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.makeFullScreen();

        setContentView(R.layout.layout_menu);

        location_track = (Switch) findViewById(R.id.switch_location_track);
        set_keyword = (Button)findViewById(R.id.button_set_keyword);
        set_keyword.setOnClickListener(this);

        data_erase = (Switch) findViewById(R.id.switch_data_erase);
        set_erase_key = (Button)findViewById(R.id.button_erase_key);
        set_erase_key.setOnClickListener(this);

        sim_change = (Switch) findViewById(R.id.switch_sim_change);
        submit = (Button)findViewById(R.id.button_submit);
        submit.setOnClickListener(this);
        learn_tracking= (TextView) findViewById(R.id.text_learn_tracking);
        learn_tracking.setOnClickListener(this);
        learn_sim_change= (TextView) findViewById(R.id.text_learn_sim_change);
        learn_sim_change.setOnClickListener(this);

        info_sim_change= (ImageView) findViewById(R.id.info_sim_change);
        info_sim_change.setOnClickListener(this);
        info_tracking= (ImageView) findViewById(R.id.info_tracking);
        info_tracking.setOnClickListener(this);
        info_erase= (ImageView) findViewById(R.id.info_erase);
        info_erase.setOnClickListener(this);

        //checking the status of tracking switch
        String track_switch_status = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("track_switch", "off");
        if(track_switch_status.toLowerCase().contains("on".toLowerCase()))
            location_track.setChecked(true);
        else
            location_track.setChecked(false);
        if (location_track != null) {
            location_track.setOnCheckedChangeListener(this);
        }


        //checking the status of sim change alert switch
        String sim_change_switch_status = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("sim_change_switch", "off");
        if(sim_change_switch_status.toLowerCase().contains("on".toLowerCase()))
            sim_change.setChecked(true);
        else
            sim_change.setChecked(false);
        if (sim_change != null) {
            sim_change.setOnCheckedChangeListener(this);
        }


        //checking the status of data erase service switch
        String data_erase_switch_status = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("data_erase_switch", "off");
        if(data_erase_switch_status.toLowerCase().contains("on".toLowerCase()))
            data_erase.setChecked(true);
        else
            data_erase.setChecked(false);
        if (data_erase != null) {
            data_erase.setOnCheckedChangeListener(this);
        }



        //starting sms receiver and boot receiver service

        SmsReceiver receiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(receiver, filter);


        BootReceiver receiver2 = new BootReceiver();
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        registerReceiver(receiver2, filter2);

        // requesting admin permission for app
        if(!AdminManager.isAdmin(this))
            AdminManager.askForAdminPermission(this);
    }


    private void makeFullScreen(){
        // Erase the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Make it full Screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //Checking if any service switch is turning on/off
        if(isChecked){

            switch (buttonView.getId())
            {
                case R.id.switch_location_track:

                    String keyword = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("keyword", "***");

                    //If keyword not set already then show a dialog to get a keyword before turn on the service
                    if(keyword.toLowerCase().contains("***".toLowerCase()))
                    {
                        setKeyword();
                    }
                    else{
                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit().putString("track_switch", "on")
                                .commit();
                    }
                    break;



                case R.id.switch_sim_change:

                    try {
                        TelephonyManager tm = (TelephonyManager)this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        String myPhoneNumber =  tm.getLine1Number();


                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit().putString("device_no", myPhoneNumber)
                                .commit();

                        String num1 = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("num1", "***");
                        String num2 = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("num2", "***");

                        //If emergency numbers are not set already then show a dialog to get the numbers before turn on the service
                        if(num1.toLowerCase().contains("***".toLowerCase()) && num2.toLowerCase().contains("***".toLowerCase()))
                        {
                            setNumbers("","");

                        }
                        else
                        {
                            getSharedPreferences("PATH", MODE_PRIVATE)
                                    .edit().putString("sim_change_switch", "on")
                                    .commit();
                        }

                    }
                    catch (Exception e) {
                        sim_change.setChecked(false);

                        Toast.makeText(getApplicationContext(), "SIM Change Alert not possible in this device.", Toast.LENGTH_LONG).show();
                    }



                    break;

                case R.id.switch_data_erase:

                    String erase_keyword = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("erase_keyword", "***");

                    //If erase key not set already then show a dialog to get the key before turn on the service
                    if(erase_keyword.toLowerCase().contains("***".toLowerCase()))
                    {
                        setEraseKey();

                    }
                    else
                    {
                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit().putString("data_erase_switch", "on")
                                .commit();
                    }
                    break;
            }


        }
        else
        {
            //turn off the specific services
            switch (buttonView.getId())
            {
                case R.id.switch_location_track:
                    getSharedPreferences("PATH", MODE_PRIVATE)
                            .edit()
                            .putString("track_switch", "off")
                            .commit();
                    break;
                case R.id.switch_sim_change:
                    getSharedPreferences("PATH", MODE_PRIVATE)
                            .edit()
                            .putString("sim_change_switch", "off")
                            .commit();
                    break;
                case R.id.switch_data_erase:
                    getSharedPreferences("PATH", MODE_PRIVATE)
                            .edit()
                            .putString("data_erase_switch", "off")
                            .commit();
                    break;
            }

        }


    }



    @Override
    public void onClick(View v1) {

        switch (v1.getId()) {

            case R.id.button_set_keyword:

                final String keyword = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("keyword", "***");

                //If keyword not set then show a dialog to get one
                //else show a dialog to change the keyword
                if(keyword.toLowerCase().contains("***".toLowerCase()))
                {
                    setKeyword();

                }
                else
                {
                    changeKeyword(keyword);

                }
                break;

            case R.id.button_erase_key:

                final String erase_keyword = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("erase_keyword", "***");

                //If Erase key not set then show a dialog to get one
                //else show a dialog to change the Erase key
                if(erase_keyword.toLowerCase().contains("***".toLowerCase()))
                {
                    setEraseKey();

                }
                else
                {
                    changeEraseKey(erase_keyword);

                }
                break;


            case R.id.button_submit:

                String num1 = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("num1", "***");
                String num2 = getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("num2", "***");

                //If numbers not set then show a dialog to get the numbers
                //else show a dialog to change the numbers
                if(num1.toLowerCase().contains("***".toLowerCase()) && num2.toLowerCase().contains("***".toLowerCase()))
                {

                   setNumbers("","");
                }
                else
                {
                    setNumbers(num1,num2);

                }
                break;

            case R.id.info_tracking:
                new AlertDialog.Builder(this)
                        .setTitle("Location Tracking Service")
                        .setMessage("1. First set a Tracking key. \n\n2. Turn on the tracking switch ON.\n\n" +
                                "3. To track your phone, just send an SMS from any phone to your phone containing the tracking key and your device will automatically reply its location info via SMS.\n\n4. Make sure your device's location access permission setting is enabled.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.info_sim_change:
                new AlertDialog.Builder(this)
                        .setTitle("SIM Change Alert")
                        .setMessage("1. First set two emergency contact numbers.\n\n2. Turn ON the alert switch.\n\n" +
                                "3. When anyone changes the sim card, a SMS containing the new sim number and location will send to the emergency numbers.\n\n4. Make sure your device's location access permission setting is enabled.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;

            case R.id.info_erase:
                new AlertDialog.Builder(this)
                        .setTitle("Erase Data Service")
                        .setMessage("1. First set a Erase key. \n\n2. Turn on the Erase Service switch ON.\n\n" +
                                "3. To Erase your phone data from distance, just send an SMS from any phone to your phone containing the Erase key and all data from the device will erased.\n")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;

        }
    }

    /**
     * show a dialog to get two phone numbers
     */
    public void setNumbers(String num1, String num2)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialouge_for_set_numbers);
        dialog.setTitle("Insert Emergency Contacts");


        final EditText new_num1 = (EditText) dialog.findViewById(R.id.text_number1);
        final EditText new_num2 = (EditText) dialog.findViewById(R.id.text_number2);

        new_num1.setText(num1);
        new_num2.setText(num2);

        dialog.show();
        new_num1.requestFocus();

        Button submit = (Button) dialog.findViewById(R.id.button_submit);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(new_num1.getText().length()>6 && new_num2.getText().length()>6)
                {
                    if(new_num1.getText().length()>6 )
                    {
                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit()
                                .putString("num1", new_num1.getText().toString())
                                .commit();
                    }

                    if(new_num2.getText().length()>6 )
                    {
                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit()
                                .putString("num2", new_num2.getText().toString())
                                .commit();
                    }

                    getSharedPreferences("PATH", MODE_PRIVATE)
                            .edit()
                            .putString("sim_change_switch", "on")
                            .commit();


                    dialog.dismiss();

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Enter 2 valid numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    /**
     * show a dialog to set a keyword
     */
    public void setKeyword()
    {


            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialouge_for_set_keyword);
            dialog.setTitle("SET YOUR KEY");


            final EditText new_keyword = (EditText) dialog.findViewById(R.id.text_keyword);
            final EditText match_keyword = (EditText) dialog.findViewById(R.id.text_match_keyword);

            dialog.show();

            new_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    new_keyword.setError(null);
                    match_keyword.setError(null);


                }
            });

            match_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub
                    new_keyword.setError(null);
                    match_keyword.setError(null);


                }
            });

            new_keyword.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence location_track, int start, int before, int count) {

                }
                @Override
                public void beforeTextChanged(CharSequence location_track, int start, int count,
                                              int after) {
                    new_keyword.setError(null);
                    match_keyword.setError(null);

                }

                @Override
                public void afterTextChanged(Editable location_track) {

                }
            });

            match_keyword.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence location_track, int start, int before, int count) {

                }
                @Override
                public void beforeTextChanged(CharSequence location_track, int start, int count,
                                              int after) {
                    new_keyword.setError(null);
                    match_keyword.setError(null);

                }

                @Override
                public void afterTextChanged(Editable location_track) {

                }
            });



            Button submit = (Button) dialog.findViewById(R.id.button_submit);

            submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(new_keyword.getText().length()<4)
                    {
                        new_keyword.setError("please input at least 4 character");
                        new_keyword.requestFocus();
                    }
                    else if(new_keyword.getText().toString().equals(match_keyword.getText().toString()))
                    {
                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit()
                                .putString("keyword", new_keyword.getText().toString())
                                .commit();

                        getSharedPreferences("PATH", MODE_PRIVATE)
                                .edit()
                                .putString("track_switch", "on")
                                .commit();

                        location_track.setChecked(true);
                        dialog.dismiss();

                    }
                    else
                    {
                        match_keyword.setError("Key not mathched");
                        match_keyword.requestFocus();

                    }

                }
            });


    }
    /**
     * show a dialog to set a Erase key
     */

    public void setEraseKey()
    {


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialouge_for_set_erase_key);
        dialog.setTitle("SET YOUR ERASE KEY");


        final EditText new_keyword = (EditText) dialog.findViewById(R.id.text_keyword);
        final EditText match_keyword = (EditText) dialog.findViewById(R.id.text_match_keyword);

        dialog.show();

        new_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                new_keyword.setError(null);
                match_keyword.setError(null);


            }
        });

        match_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                new_keyword.setError(null);
                match_keyword.setError(null);


            }
        });

        new_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                new_keyword.setError(null);
                match_keyword.setError(null);

            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });

        match_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                new_keyword.setError(null);
                match_keyword.setError(null);

            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });



        Button submit = (Button) dialog.findViewById(R.id.button_submit);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(new_keyword.getText().length()<4)
                {
                    new_keyword.setError("please input at least 4 character");
                    new_keyword.requestFocus();
                }
                else if(new_keyword.getText().toString().equals(match_keyword.getText().toString()))
                {
                    getSharedPreferences("PATH", MODE_PRIVATE)
                            .edit()
                            .putString("erase_keyword", new_keyword.getText().toString())
                            .commit();

                    getSharedPreferences("PATH", MODE_PRIVATE)
                            .edit()
                            .putString("data_erase_switch", "on")
                            .commit();

                    location_track.setChecked(true);
                    dialog.dismiss();

                }
                else
                {
                    match_keyword.setError("Key not mathched");
                    match_keyword.requestFocus();

                }

            }
        });


    }
    /**
     * show a dialog to change a keyword
     */

    public void changeKeyword(final String keyword)
    {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialouge_for_change_keyword);
        dialog.setTitle("CHANGE YOUR KEY");


        final EditText prev_keyword = (EditText) dialog.findViewById(R.id.text_keyword);
        final EditText new_keyword = (EditText) dialog.findViewById(R.id.text_new_keyword);
        final EditText match_keyword = (EditText) dialog.findViewById(R.id.text_match_keyword);
        dialog.show();


        prev_keyword.requestFocus();
        Button submit = (Button) dialog.findViewById(R.id.button_submit);

        prev_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });

        new_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });

        match_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });


        prev_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);

            }
        });

        new_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);

            }
        });

        match_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);

            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(prev_keyword.getText().toString().equals(keyword))
                {
                    if(new_keyword.getText().length()==0)
                    {
                        new_keyword.setError("enter a key");
                        new_keyword.requestFocus();

                    }
                    else if(new_keyword.getText().length()<4)
                    {
                        new_keyword.setError("too short");
                        new_keyword.requestFocus();

                    }

                    else
                    {
                        if(new_keyword.getText().toString().equals(match_keyword.getText().toString()))
                        {
                            getSharedPreferences("PATH", MODE_PRIVATE)
                                    .edit()
                                    .putString("keyword", new_keyword.getText().toString())
                                    .commit();
                            dialog.dismiss();

                        }
                        else
                        {
                            match_keyword.setError("Key Not Matched");
                            match_keyword.requestFocus();
                        }
                    }

                }
                else
                {
                    prev_keyword.setError("Invalid Key");
                    prev_keyword.requestFocus();

                }


                //
            }
        });


    }

    /**
     * show a dialog to change a erase key
     */

    public void changeEraseKey(final String keyword)
    {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialouge_for_change_keyword);
        dialog.setTitle("CHANGE ERASE KEY");


        final EditText prev_keyword = (EditText) dialog.findViewById(R.id.text_keyword);
        final EditText new_keyword = (EditText) dialog.findViewById(R.id.text_new_keyword);
        final EditText match_keyword = (EditText) dialog.findViewById(R.id.text_match_keyword);
        dialog.show();


        prev_keyword.requestFocus();
        Button submit = (Button) dialog.findViewById(R.id.button_submit);

        prev_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });

        new_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });

        match_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence location_track, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence location_track, int start, int count,
                                          int after) {
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable location_track) {

            }
        });


        prev_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);

            }
        });

        new_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);

            }
        });

        match_keyword.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                prev_keyword.setError(null);
                new_keyword.setError(null);
                match_keyword.setError(null);

            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(prev_keyword.getText().toString().equals(keyword))
                {
                    if(new_keyword.getText().length()==0)
                    {
                        new_keyword.setError("enter a key");
                        new_keyword.requestFocus();

                    }
                    else if(new_keyword.getText().length()<4)
                    {
                        new_keyword.setError("too short");
                        new_keyword.requestFocus();

                    }

                    else
                    {
                        if(new_keyword.getText().toString().equals(match_keyword.getText().toString()))
                        {
                            getSharedPreferences("PATH", MODE_PRIVATE)
                                    .edit()
                                    .putString("eras_keyword", new_keyword.getText().toString())
                                    .commit();
                            dialog.dismiss();

                        }
                        else
                        {
                            match_keyword.setError("Key Not Matched");
                            match_keyword.requestFocus();
                        }
                    }
                }
                else
                {
                    prev_keyword.setError("Invalid Key");
                    prev_keyword.requestFocus();

                }
            }
        });
    }

    @Override
    protected void onResume(){
        if(this.isPaused) {
            Intent nextActivity = new Intent(this, Security.class);
            nextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(nextActivity);
        }
        super.onResume();
    }

    @Override protected void onPause(){
        this.isPaused = true;
        super.onPause();
    }
}