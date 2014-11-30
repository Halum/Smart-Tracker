package com.smart_tracker.kryptonite.smarttracker;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;

import java.util.List;
import java.util.Locale;

/**
 * Created by MKS on 11/22/2014.
 */
public class LocationProvider implements LocationListener {

    private String provider;
    private LocationManager locationManager;
    String final_address="";
    Geocoder geocoder;
    Location location;
    static double   lat=1.0;
    static double lon=1.0;

    /**
     * Get the current latitude and longitude using location manager
     * @return a string contain location "lat-lon"
     */
    public String getLocation(Context context)
    {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);



        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        provider = locationManager.getBestProvider(criteria, false);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            provider = LocationManager.PASSIVE_PROVIDER;
            //Toast.makeText(ContextAsync, "Switch On Data Connection!!!!", Toast.LENGTH_LONG).show();
        }





        locationManager.requestLocationUpdates(provider, 0, 0, this);
        locationManager.requestLocationUpdates(provider, 0, 0, this);


        onLocationChanged(location);
        onLocationChanged(location);



        location = locationManager.getLastKnownLocation(provider);
        // Initialize the location fields


        if (location != null) {
            //  System.out.println("Provider " + provider + " has been selected.");
            lat = location.getLatitude();
            lon = location.getLongitude();

        } else { }

        locationManager.removeUpdates(this);

        return lat+"-"+lon;



    }


    /**
     * get a address using geocoder
     * @param context
     * @return address string
     */
    public String getAddress(Context context)
    {

        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String country = addresses.get(0).getCountryName();
            final_address = Html.fromHtml(
                    address + ", " + city + ",<br>" + country).toString();
        } catch (Exception e) {
            e.printStackTrace();
            final_address = "unable to get address";
        }


        return final_address;


    }


    @Override
    public void onLocationChanged(Location location) {
       // lat = location.getLatitude();
      //  lon = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
