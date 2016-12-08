package com.example.a100026051.customapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 100026051 on 10/30/16.
 */
public class Tab4Fragment extends Fragment {

    ImageView imageView;

    View rootView;

    User currentUser;
    TextView usernameTV;

    RatingBar ratingHelp;
    RatingBar ratingHelped;

    TextView numOfHelpsTV;
    TextView numOfHelpedTV;

    private FloatingActionButton fab;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_tab4,container, false);

        imageView = (ImageView)rootView.findViewById(R.id.imgView);

        usernameTV = (TextView)rootView.findViewById(R.id.usernameTV);

        ratingHelp = (RatingBar) rootView.findViewById(R.id.ratingBarHelp);
        ratingHelped = (RatingBar) rootView.findViewById(R.id.ratingBarHelped);

        numOfHelpsTV = (TextView)rootView.findViewById(R.id.numOfHelpsTV);
        numOfHelpedTV = (TextView)rootView.findViewById(R.id.numOfHelpedTV);

        if(!isOnline()){
            Toast.makeText(getContext(),"Network is not connected...",Toast.LENGTH_LONG).show();
        }

        // load synchrous from shared preferences
        loadCurrentUser();

        displayRatings();

        setUserPhoto();

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(),SettingsMainActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * display rating and number of helps, helped
     */
    private void displayRatings(){
        numOfHelpsTV.setText(Integer.toString(currentUser.getNumOfHelp())+" Helps ("+currentUser.getRatingHelp()+")");
        numOfHelpedTV.setText(Integer.toString(currentUser.getNumOfHelped())+" Helped ("+currentUser.getRatingHelped()+")");
        ratingHelp.setRating((float) currentUser.getRatingHelp());
        ratingHelped.setRating((float) currentUser.getRatingHelped());
    }

    /**
     * load current user from shared preferences
     */
    private void loadCurrentUser(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        // get json string of user
        String json = sharedPref.getString("currentUserObject", "");
        Log.i("JSONUSER",json);
        // convert json to User object
        User currentUserObject = gson.fromJson(json, User.class);
        currentUser = new User();
        currentUser = currentUserObject;
        usernameTV.setText(currentUser.getUserid()+" "+currentUser.getUsername());
    }

    /**
     * set photo if current user has existing image, otherwise set default photo
     */
    private void setUserPhoto(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
        StorageReference islandRef = storageRef.child("images/profile/"+currentUser.getFireUserNodeId()+".jpg");
        Log.i("image path","images/profile/"+currentUser.getFireUserNodeId()+".jpg");
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
                Log.i("Failed","load profile image");
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        
        inflater.inflate(R.menu.help_fragment_toolbar_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myHelpingList:
                // settings();
                Toast.makeText(getContext(),"111",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

