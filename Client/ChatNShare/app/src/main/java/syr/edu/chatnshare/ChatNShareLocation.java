/**
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 15-Apr-15.
 *
 * File: ChatNShareLocation.java
 * Functionalities:
 * Loads google map and marks current location of the user.
 */
package syr.edu.chatnshare;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Address;
import android.location.LocationProvider;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChatNShareLocation extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_chat_share);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        //mMap.getMyLocation().toString();
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));


        String user_name="";
        Bundle userName = getIntent().getExtras();
        if (userName!=null)
        {
            user_name=userName.getString("Username");
        }
        //Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Intent.putExtra("name",str); //here you will add the data into intent to pass bw activites
        //v.getContext().startActivity(intent);
//        Bundle bundle=new Bundle();
//        bundle.putString("Location",mMap.getMyLocation().toString());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //, 0, 0, (android.location.LocationListener) this.);
//        Location loc=mMap.getMyLocation();

        Intent myIntent=new Intent(ChatNShareLocation.this,ChatNShareMainActivity.class);
        String lat=String.valueOf(location.getLatitude());
        String lon=String.valueOf(location.getLongitude());
        myIntent.putExtra("Login_name",user_name);
        myIntent.putExtra("Lat",lat);
        myIntent.putExtra("Long",lon);
       // mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLatitude())).title("Marker"));
        this.startActivity(myIntent);
    }
}
