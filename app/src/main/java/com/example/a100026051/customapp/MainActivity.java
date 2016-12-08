package com.example.a100026051.customapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.a100026051.customapp.pojo.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseUser loginUser;
    private User currentUser;
    int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FIREBASE_MESSAGING2", "Refreshed token: " + refreshedToken);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();




        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Need"));
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Help"));
        tabLayout.addTab(tabLayout.newTab().setText("Me"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final MyPagerAdapter adaper = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adaper);

        // set default tab at position 1
        viewPager.setCurrentItem(1);
        tabLayout.getTabAt(1).select();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    currentTab = 1;
                    getSupportActionBar().setTitle("My Needs");
                }
                if(tab.getPosition()==1){
                    currentTab = 2;
                    getSupportActionBar().setTitle("Daily Helps");
                }
                if(tab.getPosition()==2){
                    currentTab = 3;
                    getSupportActionBar().setTitle("Needs Nearby");
                }
                if(tab.getPosition()==3){
                    currentTab = 4;
                    getSupportActionBar().setTitle("My Details");
                }
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // pause video if change to other tab
                if(tab.getPosition()==1){
                    ((VideoView)findViewById(R.id.videoView1)).pause();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // set current user info to shared preferences
        setCurrentUser();

    }

    /**
     * set current user info to shared preferences
     */
    private void setCurrentUser(){
        // get login user from firebase
        loginUser = FirebaseAuth.getInstance().getCurrentUser();

        currentUser = new User();
        currentUser.setUserid(loginUser.getEmail());

        // get current user data from firebase by email, store to shared preferences
        readUserDataFromFirebase(loginUser.getEmail());
    }

    private void readUserDataFromFirebase(String email){
        Firebase.setAndroidContext(MainActivity.this);
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("users");

        // get data by a user id
        Query remoeQuery = firebaseRef.orderByChild("userid").equalTo(email);

        remoeQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User userItem;
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            // remove node value
                            userItem = userSnapshot.getValue(User.class);
                            currentUser = userItem;
                            // save to shared preferences
                            SharedPreferences sharedPref = getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = sharedPref.edit();
                            Gson gson = new Gson();
                            // convert user object to json string
                            String currentUserJson = gson.toJson(currentUser);
                            prefsEditor.putString("currentUserObject", currentUserJson);
                            prefsEditor.commit();
                            return;
                        }
                        // if not exist, save a default user to shared preferences
                        SharedPreferences sharedPref = getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = sharedPref.edit();
                        Gson gson = new Gson();
                        currentUser.setUsername("");
                        // convert user object to json string
                        String currentUserJson = gson.toJson(currentUser);
                        prefsEditor.putString("currentUserObject", currentUserJson);
                        prefsEditor.commit();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        Log.w("TodoApp", "getUser:onCancelled", firebaseError.toException());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(currentTab == 3){
            getMenuInflater().inflate(R.menu.help_fragment_toolbar_menu, menu);
        }else {
            getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                settings();
                return true;

            case R.id.about:
                about();
                return true;

            case R.id.logout:
                logout();
                return true;

            case R.id.myHelpingList:
                myHelpingList();
                return true;

            case R.id.myHelpedList:
                myHelpedList();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void myHelpingList(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),MyHelpingListMainActivity.class);
        startActivity(intent);
    }

    private void myHelpedList(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),MyHelpedListMainActivity.class);
        startActivity(intent);
    }

    private void logout(){
        Log.i("logout","start");
        auth.signOut();

        FirebaseUser userAfterLogout = FirebaseAuth.getInstance().getCurrentUser();
        if(userAfterLogout==null){
            Toast.makeText(getApplicationContext(),"Logout successfully",Toast.LENGTH_SHORT).show();
        }

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * start settings main activity
     */
    private void settings(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),SettingsMainActivity.class);
        startActivity(intent);
    }

    private void about(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),AboutActivity.class);
        startActivity(intent);
    }

    public User getCurrentUser(){
        return this.currentUser;
    }


}
