package com.example.a100026051.customapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import static android.R.attr.defaultValue;
import static android.app.Activity.RESULT_OK;

/**
 * Created by 100026051 on 10/30/16.
 */
public class SettingsTab1Fragment extends Fragment {


    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    Button setPhotoBtn;
    Button uploadPhotoBtn;

    ImageView imageView;

    View rootView;

    User currentUser;
    TextView userEmailTV;
    EditText userNameEV;

    RatingBar ratingHelp;
    RatingBar ratingHelped;

    TextView numOfHelpsTV;
    TextView numOfHelpedTV;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_settings_tab1,container, false);

        imageView = (ImageView)rootView.findViewById(R.id.imgView);

        setPhotoBtn = (Button)rootView.findViewById(R.id.set_photo_btn);
        setPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageFromGallery(view);
            }
        });

        userEmailTV = (TextView)rootView.findViewById(R.id.userEmailTV);
        userNameEV = (EditText)rootView.findViewById(R.id.usernameET);


        ratingHelp = (RatingBar) rootView.findViewById(R.id.ratingBarHelp);
        ratingHelped = (RatingBar) rootView.findViewById(R.id.ratingBarHelped);


        numOfHelpsTV = (TextView)rootView.findViewById(R.id.numOfHelpsTV);
        numOfHelpedTV = (TextView)rootView.findViewById(R.id.numOfHelpedTV);

        // load synchrous from shared preferences
        loadCurrentUser();

        displayRatings();

        setUserPhoto();

        return rootView;
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
        Log.i("CurrentUserFireID",currentUser.getFireUserNodeId());
        userEmailTV.setText("Account: "+currentUser.getUserid());
        userNameEV.setText(currentUser.getUsername());

        // set focus to the end
        userNameEV.setSelection(userNameEV.getText().length());

    }

    /**
     * set photo if current user has existing image, otherwise set default photo
     */
    private void setUserPhoto(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
        StorageReference islandRef = storageRef.child("images/profile/"+currentUser.getFireUserNodeId()+".jpg");

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
                Log.i("Fail","load profile image");
            }
        });

    }

    /**
     * select photo from gallery
     * @param view
     */
    private void loadImageFromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView)rootView.findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this.getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }


}

