package com.example.a100026051.customapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.a100026051.customapp.pojo.Need;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by swin on 21/11/2016.
 */

public class PublisherFeedbackDialogFragment extends DialogFragment {

    Button saveBtn;
    RatingBar ratingbar;
    EditText feedbackET;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback_dialog, container, false);
        getDialog().setTitle("Feedback Dialog");

        ratingbar = (RatingBar)rootView.findViewById(R.id.feedback_ratingbar);
        feedbackET = (EditText)rootView.findViewById(R.id.feedback_ET);

        saveBtn = (Button)rootView.findViewById(R.id.feedback_save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = feedbackET.getText().toString();
                double rating = ratingbar.getRating();

                String needJsonString = getArguments().getString("needJson");
                Gson gson = new Gson();
                Need need = gson.fromJson(needJsonString, Need.class);

                Toast.makeText(getContext(),"Need: "+ need,Toast.LENGTH_SHORT).show();

                Calendar currentCalendar = Calendar.getInstance();
                String fullFormat = "dd/MM/yyyy HH:mm:ss.SSS";
                SimpleDateFormat sdf = new SimpleDateFormat(fullFormat);
                String currentTime = sdf.format(currentCalendar.getTime());

                need.setRatingOfHelper(String.valueOf(rating));
                need.setFeedbackFromPublisher(feedback);
                need.setPublisherFeedbackTime(currentTime);
                need.setStatus("Publisher Feedback Submit");

                updateFirebaseNeed(need);

                // updateHelperInfo(need.getHelpedByUserFirebaseNodeId(),rating);
                // update rating, help +1

                // updatePublisherInfo();
                // helped +1

            }
        });
        return rootView;
    }

    private void updateFirebaseNeed(Need need){
        Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);
        // set value and add completion listener
        myFirebaseRef.child("needs").child("questions").child(need.getNeedFirebaseNodeId()).setValue(need,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(getContext(),"Need record failed to be updated " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Need record updated successfully.",Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


}
