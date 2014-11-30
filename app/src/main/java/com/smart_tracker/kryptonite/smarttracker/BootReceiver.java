package com.smart_tracker.kryptonite.smarttracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.StringTokenizer;

public class BootReceiver extends BroadcastReceiver {


    String final_address="";
    double   lat=0.0;
    double lon=0.0;



    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try
        {
            //checking if saved sim number matched after reboot
            TelephonyManager telephonyManager  =  (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            String NewMobileNumber = telephonyManager.getLine1Number();

            String PrevNumber = context.getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("device_no", "***");

            if(PrevNumber.equals(NewMobileNumber))
            {

                //Getting the location latitude & longitude
                LocationProvider location = new LocationProvider();
                String s = location.getLocation(context);
                StringTokenizer st = new StringTokenizer(s, "-");

                int i = 0;
                String split[] = new String[10];
                for (i = 0; st.hasMoreTokens(); i++)
                    split[i] = st.nextToken();

                lat = Double.parseDouble(split[0]);
                lon = Double.parseDouble(split[1]);

                //getting location address
                final_address = location.getAddress(context);


                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(PrevNumber, null,
                        "SIM Chnage Alert\nNew SIM NUMBER:" + NewMobileNumber + "\nlocation:\nlat:" + lat + "\nlong:" + lon + "\nAddress:\n" + final_address, null, null);

            }
        }
        catch (Exception e) {

            Toast.makeText(context, "SIM Change Alert not possible in this device.", Toast.LENGTH_LONG).show();
        }


    }
}
