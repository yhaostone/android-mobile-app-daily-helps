package com.example.a100026051.customapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

/**
 * Created by swin on 11/19/16.
 */

public class MyHelpingListMainActivity extends AppCompatActivity {

    ListView helpingNeedLV;

    User currentUser;

    Context context;

    private ArrayList<Need> myHelpingList;

    Need selectedNeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_helping_list_main);


        initializeActionBar();

        helpingNeedLV = (ListView) findViewById(R.id.myHelpingNeedsLV);

        context = getApplicationContext();

        loadCurrentUser();


        loadNeeds();
    }

    private void initializeActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_helping_list_toolbar);
        toolbar.setTitle("My Helping List");
        setSupportActionBar(toolbar);
    }

    /**
     * load current user from shared preferences
     */
    private void loadCurrentUser() {
        SharedPreferences sharedPref = getSharedPreferences(Config.CURRENT_USER_PREFERENCES, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        // get json string of user
        String json = sharedPref.getString("currentUserObject", "");
        Log.i("JSONUSER", json);
        // convert json to User object
        User currentUserObject = gson.fromJson(json, User.class);
        currentUser = new User();
        currentUser = currentUserObject;

    }

    private void loadNeeds(){

        // get current user from main activity
        // User userFromActivity = ((MainActivity)this.getActivity()).getCurrentUser();
        // final String userid = currentUser.getUserid();

        myHelpingList = new ArrayList<Need>();

        Firebase.setAndroidContext(context);
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("needs").child("questions");


        Query myQuestionNeeds = firebaseRef.orderByChild("publishUserId");

        myQuestionNeeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myHelpingList.clear();

                Need needItem;
                Log.i("add to list",currentUser.getUserid());
                for (DataSnapshot need : dataSnapshot.getChildren()) {
                    needItem = need.getValue(Need.class);

                    if(!(currentUser.getUserid()).equals(needItem.getPublishUserId())){
                        // only display the need with a status 'Published'
                        if ("Picked".equals(needItem.getStatus())){
                            if(needItem.getHelpedByUserId().equals(currentUser.getUserid())){
                                myHelpingList.add(needItem);
                                Log.i("HELPINGNEED",needItem.toString());
                            }
                        }
                    }

                    Log.i("ON DATA CHANGE",need.getValue(Need.class).toString());
                }

                helpingNeedLV.setAdapter(new MyHelpingListAdapter(context, R.layout.list_row_others_need, R.id.row_userid, myHelpingList));



                helpingNeedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView row2DescriptionTV = (TextView)view.findViewById(R.id.row2_description);
                        RatingBar helpRatingBar = (RatingBar)view.findViewById(R.id.otherNeedListHelpRatingBar);
                        RatingBar helpedRatingBar = (RatingBar)view.findViewById(R.id.otherNeedListHelpedRatingBar);

                        TextView numOfHelpTV = (TextView)view.findViewById(R.id.row2_num_of_helps_tv);
                        TextView numOfHelpedTV = (TextView)view.findViewById(R.id.row2_num_of_helped_tv);

                        Button shareButton =(Button)view.findViewById(R.id.other_need_list_row2_share_button);
                        Button helpButton = (Button)view.findViewById(R.id.my_helping_list_close_btn);
                        Button callButton = (Button)view.findViewById(R.id.other_need_list_row2_call_button);

                        if ( row2DescriptionTV.getVisibility() == View.GONE)
                        {
                            //expandedChildList.set(arg2, true);
                            row2DescriptionTV.setVisibility(View.VISIBLE);
                            helpRatingBar.setVisibility(View.VISIBLE);
                            helpedRatingBar.setVisibility(View.VISIBLE);
                            numOfHelpTV.setVisibility(View.VISIBLE);
                            numOfHelpedTV.setVisibility(View.VISIBLE);
                            shareButton.setVisibility(View.VISIBLE);
                            helpButton.setVisibility(View.VISIBLE);
                            callButton.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            //expandedChildList.set(arg2, false);
                            row2DescriptionTV.setVisibility(View.GONE);
                            helpRatingBar.setVisibility(View.GONE);
                            helpedRatingBar.setVisibility(View.GONE);
                            numOfHelpTV.setVisibility(View.GONE);
                            numOfHelpedTV.setVisibility(View.GONE);
                            shareButton.setVisibility(View.GONE);
                            helpButton.setVisibility(View.GONE);
                            callButton.setVisibility(View.GONE);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    /** Custom row adatper -- that displays an icon next to the movie name */
    class MyHelpingListAdapter extends ArrayAdapter<Need>
    {
        public MyHelpingListAdapter(Context c, int rowResourceId, int textViewResourceId,
                                    ArrayList<Need> items) {
            super(c, rowResourceId, textViewResourceId, items);
        }


        // View recycling
        public View getView(int pos, View convertView, ViewGroup parent)
        {
            Tab3Fragment.ViewHolder holder;
            if(convertView==null){	// create one if empty
                convertView = getLayoutInflater().inflate(R.layout.list_row_my_helping, parent, false);
                Log.w("MVMVMVMVMVMV", "Creating row view at position "+pos);
                holder = new Tab3Fragment.ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.row_icon);
                holder.text = (TextView) convertView.findViewById(R.id.row_userid);
                holder.text2 = (TextView) convertView.findViewById(R.id.row_title);
                holder.row2DescriptionTextView = (TextView) convertView.findViewById(R.id.row2_description);
                holder.shareBtn = (Button)convertView.findViewById(R.id.other_need_list_row2_share_button);
                holder.helpBtn = (Button)convertView.findViewById(R.id.my_helping_list_close_btn);
                holder.row2NumOfHelpTV = (TextView)convertView.findViewById(R.id.row2_num_of_helps_tv);
                holder.row2NumOfHelpedTV  = (TextView)convertView.findViewById(R.id.row2_num_of_helped_tv);
                holder.row2HelpRating = (RatingBar) convertView.findViewById(R.id.otherNeedListHelpRatingBar);
                holder.row2HelpedRating = (RatingBar) convertView.findViewById(R.id.otherNeedListHelpedRatingBar);
                holder.callBtn = (Button) convertView.findViewById(R.id.other_need_list_row2_call_button);


                convertView.setTag(holder);
            }else{
                holder = (Tab3Fragment.ViewHolder)convertView.getTag();
            }



            Need currNeed = myHelpingList.get(pos);


            if (currNeed != null)
            {
                holder.text.setText(currNeed.getPublishedBy());
                holder.text2.setText(currNeed.getTitle());
                holder.row2DescriptionTextView.setText(currNeed.getDescription());
                holder.shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = helpingNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)helpingNeedLV.getItemAtPosition(position);


                        String sharedMessage = "This is a shared need from DailyHelps App. Need publisher '"+clickedItem.getPublishedBy()+
                                "' needs help. Title: "+clickedItem.getTitle()+"; Description: "+clickedItem.getDescription()+"; Expire time: "+
                                clickedItem.getExpireDate()+" "+clickedItem.getExpireTime()+"; shared by "+currentUser.getUsername()+".";

                        Toast.makeText(getApplicationContext(),sharedMessage,Toast.LENGTH_LONG).show();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedMessage);
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share data using"));
                    }
                });
                holder.helpBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = helpingNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)helpingNeedLV.getItemAtPosition(position);
                        // set global needItem as clicked one for later use
                        selectedNeed = clickedItem;
                        // send email notification to publisher
                        // sendEmailToPublisher(clickedItem);

                        // change status, pick time;
                        // add userid and username to need;
                        // add 1 to numOfHelp for helper, add needId to helper's helping list
                        // add 1 to numOfHelped for publisher, add needId to publisher's being helped list

                        // updateNeed(clickedItem);

                    }
                });

                holder.callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = helpingNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)helpingNeedLV.getItemAtPosition(position);

                        // make phone call
                        String phone = clickedItem.getPublisherPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });

                String userFirebaseId = currNeed.getPublisherUserFirebaseId();
                // get user data from firebase and display
                setPublisherInfo(userFirebaseId,holder);

            }

            return convertView;
        }

    }

    private void setPublisherInfo(String userFirebaseId, final Tab3Fragment.ViewHolder holder){

        Firebase.setAndroidContext(context);
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("users");
        Query getPublisherQuery = firebaseRef.orderByChild("fireUserNodeId").equalTo(userFirebaseId);
        getPublisherQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User userItem;
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {

                            userItem = userSnapshot.getValue(User.class);
                            if(userItem!=null){
                                Log.i("get user",userItem.toString());
                                holder.row2NumOfHelpTV.setText(userItem.getNumOfHelp()+" helps");
                                holder.row2NumOfHelpedTV.setText(userItem.getNumOfHelped()+" helped");
                                holder.row2HelpRating.setRating((float) userItem.getRatingHelp());
                                holder.row2HelpedRating.setRating((float) userItem.getRatingHelped());
                                // load publisher photo by firebase user id
                                setPublisherPhoto(userItem.getFireUserNodeId(),holder);
                            }
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        Log.w("PublisherInfoFailed", "getUser:onCancelled", firebaseError.toException());
                    }
                });

    }

    private void setPublisherPhoto(String userFirebaseId, final Tab3Fragment.ViewHolder holder){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(Config.FIREBASE_STORAGE_URL);
        StorageReference islandRef = storageRef.child("images/profile/"+userFirebaseId+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // set loaded data to image view
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.icon.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle any errors
                Log.i("Fail","load profile image");
            }
        });
    }

}
