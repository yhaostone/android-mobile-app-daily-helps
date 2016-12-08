package com.example.a100026051.customapp;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by 100026051 on 10/30/16.
 */
public class Tab2Fragment extends Fragment {

    Button buttonPlayVideo;
    VideoView mVideoView;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootview = inflater.inflate(R.layout.fragment_tab2,container, false);

        buttonPlayVideo = (Button)rootview.findViewById(R.id.playbutton);
        mVideoView = (VideoView) rootview.findViewById(R.id.videoView1);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });

        getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
        buttonPlayVideo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)getActivity().findViewById(R.id.imageView);
                imageView.setVisibility(View.GONE);
                mVideoView.setVisibility(View.VISIBLE);

                String path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.video_kindness_boomerang_one_day;
                mVideoView.setVideoURI(Uri.parse(path));
                MediaController mc = new MediaController(getContext());

                // set controller
                mVideoView.setMediaController(mc);
                // set controller in videoview
                mc.setAnchorView(mVideoView);
                mVideoView.start();
            }
        });

        // load video from firebase
        // loadVideo();
        return rootview;
    }

    private void loadVideo(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
        StorageReference islandRef = storageRef.child("video/kindness boomerang one day.mp4");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed


                Toast.makeText(getContext(),"Video downloaded successfully",Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle any errors
                Toast.makeText(getContext(),"Video downloaded failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
