package com.example.a100026051.customapp;

/**
 * Created by 100026051 on 11/6/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.User;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetUsernameActivity extends AppCompatActivity {

    private EditText inputUsername;
    private Button btnSave;
    private ProgressBar progressBar;

    public SetUsernameActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_username);

        inputUsername = (EditText) findViewById(R.id.setusernameET);
        btnSave = (Button) findViewById(R.id.btn_save_username);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Firebase.setAndroidContext(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username= inputUsername.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplication(), "User name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);

                User user = new User();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                user.setUserid(firebaseUser.getEmail());
                user.setUsername(username);
                user.setPhoneNum("0451175166");

                Calendar currentCalendar = Calendar.getInstance();
                String fullFormat = "yyyyMMddHHmmssSSS";
                SimpleDateFormat sdf = new SimpleDateFormat(fullFormat);
                String currentTime = sdf.format(currentCalendar.getTime());
                user.setFireUserNodeId(currentTime);

                // set value and add completion listener
                myFirebaseRef.child("users").child(user.getFireUserNodeId()).setValue(user,new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(SetUsernameActivity.this,"Username could not be saved. " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SetUsernameActivity.this,"Username saved successfully.",Toast.LENGTH_SHORT).show();

                            // start main activity
                            Intent intent = new Intent(SetUsernameActivity.this, MainActivity.class);
                            intent.putExtra("usernameSet",username);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }

}
