package com.example.a100026051.customapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.Need;
import com.example.a100026051.customapp.pojo.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Yan on 10/31/16.
 */
public class PublishNeedAnswerStep2Activity extends AppCompatActivity {

    EditText titleEditText;
    EditText descriptionEditText;
    EditText expireDateEditText;
    EditText editTextFromTime;
    EditText addressEditText;
    Calendar myCalendar = Calendar.getInstance();

    User currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_need_answer_step2);
        initializeActionBar();
        initializeUI();
        loadCurrentUser();  // load current user from shared preferences
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
        currentUser = currentUserObject;
    }

    private void initializeActionBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.publish_need_answer_step2_toolbar);
        toolbar.setTitle("Publish Need");
        setSupportActionBar(toolbar);
    }

    private void initializeUI(){

        // set listener for date dialog
        expireDateEditText = (EditText)findViewById(R.id.expireDateET);
        expireDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(PublishNeedAnswerStep2Activity.this, expireDateListenr, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editTextFromTime = (EditText) findViewById(R.id.expireTimeET);
        SetTime expireTime = new SetTime(editTextFromTime, this);
    }

    // data picker listener
    DatePickerDialog.OnDateSetListener expireDateListenr = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateExpireDateET();
        }
    };

    /**
     * update edittext value for expire date
     */
    private void updateExpireDateET() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        expireDateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.publish_need_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveQuestionNeed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * used to save question data to firebase
     */
    private void saveQuestionNeed(){

        titleEditText = (EditText)findViewById(R.id.titleET);
        descriptionEditText = (EditText)findViewById(R.id.descriptionET);
        addressEditText =(EditText)findViewById(R.id.addressET);

        Calendar currentCalendar = Calendar.getInstance();
        String fullFormat = "dd/MM/yyyy HH:mm:ss.SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(fullFormat);
        String currentTime = sdf.format(currentCalendar.getTime());


        String format2 = "yyyyMMddHHmmssSSS";
        SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
        String currentTimeString = sdf2.format(currentCalendar.getTime());



        String needId = currentUser.getUserid()+";"+currentTime;
        //Getting values to store
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String expireDate = expireDateEditText.getText().toString().trim();
        String expireTime = editTextFromTime.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // create pojo, set values
        Need questionRecord = new Need();
        questionRecord.setNeedId(needId);
        questionRecord.setTitle(title);
        questionRecord.setDescription(description);
        questionRecord.setExpireDate(expireDate);
        questionRecord.setExpireTime(expireTime);
        questionRecord.setPublishedBy(currentUser.getUsername());
        questionRecord.setStatus("Published");
        questionRecord.setType("question");
        questionRecord.setAddress(address);
        questionRecord.setPublishTime(currentTime);
        questionRecord.setPublishUserId(currentUser.getUserid());
        questionRecord.setPublisherPhone(currentUser.getPhoneNum());
        questionRecord.setPublisherUserFirebaseId(currentUser.getFireUserNodeId());
        questionRecord.setNeedFirebaseNodeId(currentTimeString);    // set unique node id by current time string


        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);

        // set value and add completion listener
        myFirebaseRef.child("needs").child("questions").child(questionRecord.getNeedFirebaseNodeId()).setValue(questionRecord,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(PublishNeedAnswerStep2Activity.this,"Data could not be saved. " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PublishNeedAnswerStep2Activity.this,"Data saved successfully.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
