package com.example.a100026051.customapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.a100026051.customapp.pojo.Need;
import com.example.a100026051.customapp.pojo.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

/**
 * Created by 100026051 on 11/2/16.
 */

public class MapNeedClickedActivity extends AppCompatActivity {

    Need clickedNeed;

    TextView typeDataTV;
    TextView titleDataTV;
    TextView descriptionDataTV;
    TextView expireTimeDataTV;
    TextView statusDataTV;

    RatingBar ratingHelp;
    RatingBar ratingHelped;

    TextView numOfHelpTV;
    TextView numOfHelpedTV;

    User currentUser;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_need_clicked);
        loadCurrentUser();
        initializeUI();
        setUserPhoto();
    }

    private void initializeUI(){
        // get need object from intent
        Intent i = getIntent();
        clickedNeed = (Need) i.getParcelableExtra("ClickedNeedOnMap");

        // set toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(clickedNeed.getPublishedBy()+"'s Need");
        setSupportActionBar(toolbar);

        // display need details
        if(clickedNeed!=null){
            displayNeedDetails();
        }

        imageView = (ImageView)findViewById(R.id.userImageView);

        // load user rating and num of help info
        loadPublisherInfo(clickedNeed.getPublishUserId());
    }

    /**
     * set photo if current user has existing image, otherwise set default photo
     */
    private void setUserPhoto(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);

        StorageReference islandRef = storageRef.child("images/profile/"+clickedNeed.getPublisherUserFirebaseId()+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // set loaded data to image view
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle any errors

            }
        });

    }

    // get the user object who needs help by userid
    private void loadPublisherInfo(String publisherUserId){

        Firebase.setAndroidContext(MapNeedClickedActivity.this);
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("users");

        // get data by a user id - email
        Query remoeQuery = firebaseRef.orderByChild("userid").equalTo(publisherUserId);

        remoeQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User userItem;
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            userItem = userSnapshot.getValue(User.class);

                            ratingHelp = (RatingBar) findViewById(R.id.ratingBar);
                            ratingHelped = (RatingBar) findViewById(R.id.ratingBar2);
                            numOfHelpTV = (TextView)findViewById(R.id.numOfHelpsTV);
                            numOfHelpedTV = (TextView)findViewById(R.id.numOfHelpedTV);

                            // set nums and rating
                            ratingHelp.setRating((float) userItem.getRatingHelp());
                            ratingHelped.setRating((float) userItem.getRatingHelped());
                            numOfHelpTV.setText(userItem.getNumOfHelp()+" Helps");
                            numOfHelpedTV.setText(userItem.getNumOfHelped()+" Helped");

                            return;
                        }
                        return;
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        Log.w("TodoApp", "getUser:onCancelled", firebaseError.toException());
                    }
                });
    }

    /**
     * load current user from shared preferences
     */
    private void loadCurrentUser(){
        SharedPreferences sharedPref = getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        // get json string of user
        String json = sharedPref.getString("currentUserObject", "");
        // convert json to User object
        User currentUserObject = gson.fromJson(json, User.class);
        currentUser = currentUserObject;
    }

    /**
     * to set textview values for need details
     */
    private void displayNeedDetails(){
        typeDataTV = (TextView)findViewById(R.id.typeDataTV);
        titleDataTV = (TextView)findViewById(R.id.titleDataTV);
        descriptionDataTV = (TextView)findViewById(R.id.descriptionDataTV);
        expireTimeDataTV = (TextView)findViewById(R.id.expireTimeDataTV);
        statusDataTV = (TextView)findViewById(R.id.statusDataTV);


        typeDataTV.setText(clickedNeed.getType());
        titleDataTV.setText(clickedNeed.getTitle());
        descriptionDataTV.setText(clickedNeed.getDescription());
        expireTimeDataTV.setText(clickedNeed.getExpireDate()+" "+clickedNeed.getExpireTime());
        statusDataTV.setText(clickedNeed.getStatus());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.need_detail_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.share:
                shareNeedDetails();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /*
    share need details
     */
    private void shareNeedDetails(){
        if(clickedNeed!=null){
            String message = "User: "+clickedNeed.getPublishedBy() + "; Title: " + clickedNeed.getTitle() +
                    "; Description: " + clickedNeed.getDescription() + "; Publish Time:" + clickedNeed.getPublishTime()+ ";";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share need details using"));

        }
    }

}
