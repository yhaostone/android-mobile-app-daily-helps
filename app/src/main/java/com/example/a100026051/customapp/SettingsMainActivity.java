package com.example.a100026051.customapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

/**
 * Created by 100026051 on 11/2/16.
 */

public class SettingsMainActivity extends AppCompatActivity {

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);

        initializeActionBar();

        TabLayout tabLayout = (TabLayout)findViewById(R.id.settings_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.addTab(tabLayout.newTab().setText("System"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.settings_pager);
        final SettingsPagerAdapter adaper = new SettingsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adaper);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initializeActionBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.settings_main_toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_save:
                saveSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * save settings
     * save profile photo, details
     */
    private void saveSettings(){
        // get current user from shared preferences
        currentUser = getUserFromSharedPreferences();

        final User newUser = currentUser;
        Log.i("currentUser name",currentUser.getUsername());
        String username = ((EditText)findViewById(R.id.usernameET)).getText().toString();

        if("".equals(username)||username==null){
            Toast.makeText(getApplicationContext(),"Failed to save, user name cannot be empty",Toast.LENGTH_LONG).show();
            return;
        }

        newUser.setUsername(username);

        // update user data in firebase
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);

        // set value and add completion listener
        myFirebaseRef.child("users").child(newUser.getFireUserNodeId()).setValue(newUser,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {

                    Toast.makeText(getApplicationContext(),"Data could not be saved. " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    // if user info saved successfully, then save user image
                    uploadPhoto();
                    Toast.makeText(getApplicationContext(),"User data saved successfully.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * upload image from imageview to Firebase storage
     * @author Yan
     *
     */
    private void uploadPhoto() {
        // Get the data from an ImageView as bytes
        ImageView imageView = (ImageView)findViewById(R.id.imgView);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);

        // Create a reference to 'images/profile/userNodeId.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/profile/"+currentUser.getFireUserNodeId()+".jpg");    // set image name as userid.jpg which is unique

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(),"Photo upload failed!",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(getApplicationContext(),"Photo uploaded successfully!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCurrentUser(User user){

    }

    public User getUserFromSharedPreferences(){
        User user = new User();
        SharedPreferences sharedPref = getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        // get json string of user
        String json = sharedPref.getString("currentUserObject", "");
        Log.i("JSONUSER",json);
        // convert json to User object
        user = gson.fromJson(json, User.class);
        return user;
    }
}
