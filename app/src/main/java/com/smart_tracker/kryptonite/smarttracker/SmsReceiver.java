package com.smart_tracker.kryptonite.smarttracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.StringTokenizer;


public class SmsReceiver extends BroadcastReceiver implements LocationListener {


    String final_address="";
    double   lat=0.0;
    double lon=0.0;



    @Override
    public void onReceive(Context context, Intent intent)
    {

        //if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {

            Toast.makeText(context, "-------"+intent.getAction(),
                    Toast.LENGTH_LONG).show();

            String track_switch = context.getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("track_switch", "off");
            //checking whether the service is on or not
            if(track_switch.toLowerCase().contains("on".toLowerCase()))
            {
                Bundle bundle = intent.getExtras();
                SmsMessage[] messages = null;
                String msgBody = "";
                String srcNumber = "";

                if (bundle != null)
                {
                    //getting message body and sim numbers
                    Object[] objs = (Object[]) bundle.get("pdus");
                    messages = new SmsMessage[objs.length];
                    for (int i = 0; i < objs.length; i++)
                    {
                        messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
                        msgBody += messages[i].getMessageBody();
                        srcNumber = messages[i].getOriginatingAddress();
                    }


                    String keyword = context.getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("keyword", "***");
                    String erase_key = context.getSharedPreferences("PATH", Context.MODE_PRIVATE).getString("erase_keyword", "***");

                    //check if message contain the keyword
                    if(msgBody.contains(keyword))
                    {

                        //getting location attribute lat,lon
                        LocationProvider location = new LocationProvider();
                        String s = location.getLocation(context);
                        StringTokenizer st = new StringTokenizer(s,"-");

                        int i=0;
                        String split[]=new String[10];
                       for(i=0;st.hasMoreTokens();i++)
                            split[i]=st.nextToken();

                       lat= Double.parseDouble(split[0]);
                        lon = Double.parseDouble(split[1]);

                        //getting location address
                        final_address= location.getAddress(context);




                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(srcNumber, null,
                                "lat:"+lat+"\nlong:"+ lon+"\nAddress:\n"+final_address, null, null);

                    }


                    //check if message contain the erase key
                    if(msgBody.contains(erase_key))
                    {

                        //erase phone data part


                    }

                }

            }
        }

    }



    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }




    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }




    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }




    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}

