package com.example.a100026051.customapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.Need;
import com.example.a100026051.customapp.pojo.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 100026051 on 11/1/16.
 */

public class HelpMapActivity extends Activity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private ArrayList<Need> othersNeedList;
    private User currentUser;
    private HashMap<Marker,Need> markersHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_map);


        // get current user from main activity
        loadCurrentUser();


        markersHashMap = new HashMap<Marker,Need>();

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // load others need and display on map
        loadOthersNeed();

    }

    /**
     * load current user from shared preferences
     */
    private void loadCurrentUser(){
        SharedPreferences sharedPref = getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        // get json string of user
        String json = sharedPref.getString("currentUserObject", "");
        Log.i("JSONUSER",json);
        // convert json to User object
        User currentUserObject = gson.fromJson(json, User.class);
        currentUser = new User();
        currentUser = currentUserObject;
    }

    /**
     * load others need and display on map
     */
    private void loadOthersNeed(){

        othersNeedList = new ArrayList<Need>();
        Firebase.setAndroidContext(this);
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("needs").child("questions");

        Query myQuestionNeeds = firebaseRef.orderByChild("publishedBy");

        myQuestionNeeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get current user id
                String currentUserId = currentUser.getUserid();

                othersNeedList.clear();
                Need needItem;
                for (DataSnapshot need : dataSnapshot.getChildren()) {
                    needItem = need.getValue(Need.class);
                    if(!currentUserId.equals(needItem.getPublishUserId())){
                        othersNeedList.add(needItem);
                        AddNeedToMap(needItem);
                    }
                    Log.i("ON DATA CHANGE",needItem.toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * add need info to Map, display username and need title.
     * @param needItem
     */
    private void AddNeedToMap(Need needItem){
        String address = needItem.getAddress();
        String username = needItem.getPublishedBy();
        String info = needItem.getTitle();

        Log.i("Need",needItem.getAddress());
        LatLng ll = getLocationFromAddress(HelpMapActivity.this, address);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(ll)
                .title(username)
                .snippet(info));

        markersHashMap.put(marker,needItem);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(markersHashMap.size()>0){
                    Need needClicked = markersHashMap.get(marker);
                    Intent i = new Intent();
                    i.putExtra("ClickedNeedOnMap", needClicked);
                    i.setClass(getApplicationContext(),MapNeedClickedActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(getApplicationContext(),"No data",Toast.LENGTH_LONG).show();
                }

            }
        });


        marker.showInfoWindow();
    }

    /**
     * function to load map
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        setUpMap();
    }

    public void setUpMap(){
        // current location
        String currentAddress = "Swinburne University";
        LatLng currentll = getLocationFromAddress(this,currentAddress); // current point

        // Animate a camera with new latlng center and required zoom.
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentll, 13));

    }

    /**
     * get LatLng by address
     * @param context
     * @param strAddress
     * @return
     */
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }
}
