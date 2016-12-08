package com.example.a100026051.customapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 100026051 on 10/30/16.
 */
public class Tab3Fragment extends ListFragment {

    private FloatingActionButton fab;
    Context context;

    private ArrayList<Need> otherNeedsList;

    private ArrayList<Need> myHelpingList;

    private LayoutInflater mInflater;

    User currentUser;

    ListView helpingNeedLV;
    ListView otherNeedLV;

    Need selectedNeed;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        setHasOptionsMenu(true);

        Log.i("Fragment3","create view");
        View rootView = inflater.inflate(R.layout.fragment_tab3,container, false);

        helpingNeedLV= (ListView)rootView.findViewById(R.id.helpingNeedsLV);
        otherNeedLV= (ListView)rootView.findViewById(android.R.id.list);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(),"Loading Google Maps...",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(),HelpMapActivity.class);
                startActivity(intent);
            }
        });

        context = getActivity().getApplicationContext();

        mInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(!isOnline()){
            Toast.makeText(context,"Network is not connected...",Toast.LENGTH_LONG).show();
        }

        loadCurrentUser();

        loadNeeds();

        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

    }

    private void loadNeeds(){

        // get current user from main activity
        User userFromActivity = ((MainActivity)this.getActivity()).getCurrentUser();
        final String userid = userFromActivity.getUserid();

        myHelpingList = new ArrayList<Need>();
        otherNeedsList = new ArrayList<Need>();

        Firebase.setAndroidContext(this.getActivity());
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("needs").child("questions");


        Query myQuestionNeeds = firebaseRef.orderByChild("publishUserId");

        myQuestionNeeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myHelpingList.clear();
                otherNeedsList.clear();
                Need needItem;
                Log.i("add to list",userid);
                for (DataSnapshot need : dataSnapshot.getChildren()) {
                    needItem = need.getValue(Need.class);

                    if(!userid.equals(needItem.getPublishUserId())){
                        // only display the need with a status 'Published'
                        if("Published".equals(needItem.getStatus())){
                            otherNeedsList.add(needItem);
                            Log.i("add to list",needItem.getPublishedBy());
                        }else if ("Picked".equals(needItem.getStatus())){
                            if(needItem.getHelpedByUserId().equals(currentUser.getUserid())){
                                myHelpingList.add(needItem);
                                Log.i("HELPINGNEED",needItem.toString());
                            }

                        }
                    }

                    Log.i("ON DATA CHANGE",need.getValue(Need.class).toString());
                }
//                // hide text view if list size is 0
//                if(otherNeedsList.size()==0){
//                    Log.i("OtherList",String.valueOf(otherNeedsList.size()));
//                    TextView otherNeedsTV = (TextView)getActivity().findViewById(R.id.otherNeedsTV);
//                    otherNeedsTV.setVisibility(View.GONE);
//                }

//                // hide text view if list size is 0
//                if(myHelpingList.size()==0){
//                    Log.i("HelpingList",String.valueOf(myHelpingList.size()));
//                    TextView helpingNeedsTV = (TextView)getActivity().findViewById(R.id.helpingNeedsTV);
//                    helpingNeedsTV.setVisibility(View.GONE);
//                }

                // helpingNeedLV.setAdapter(new MyHelpingListAdapter(context, R.layout.list_row_others_need, R.id.row_userid, myHelpingList));


                otherNeedLV.setAdapter(new OthersNeedListAdapter(context, R.layout.list_row_others_need, R.id.row_userid, otherNeedsList));



                otherNeedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView row2DescriptionTV = (TextView)view.findViewById(R.id.row2_description);
                        RatingBar helpRatingBar = (RatingBar)view.findViewById(R.id.otherNeedListHelpRatingBar);
                        RatingBar helpedRatingBar = (RatingBar)view.findViewById(R.id.otherNeedListHelpedRatingBar);

                        TextView numOfHelpTV = (TextView)view.findViewById(R.id.row2_num_of_helps_tv);
                        TextView numOfHelpedTV = (TextView)view.findViewById(R.id.row2_num_of_helped_tv);

                        Button shareButton =(Button)view.findViewById(R.id.other_need_list_row2_share_button);
                        Button helpButton = (Button)view.findViewById(R.id.other_need_list_row2_help_button);
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
    class OthersNeedListAdapter extends ArrayAdapter<Need>
    {
        public OthersNeedListAdapter(Context c, int rowResourceId, int textViewResourceId,
                              ArrayList<Need> items) {
            super(c, rowResourceId, textViewResourceId, items);
        }


        // View recycling
        public View getView(int pos, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView==null){	// create one if empty
                convertView = mInflater.inflate(R.layout.list_row_others_need, parent, false);
                Log.w("MVMVMVMVMVMV", "Creating row view at position "+pos);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.row_icon);
                holder.text = (TextView) convertView.findViewById(R.id.row_userid);
                holder.text2 = (TextView) convertView.findViewById(R.id.row_title);
                holder.row2DescriptionTextView = (TextView) convertView.findViewById(R.id.row2_description);
                holder.shareBtn = (Button)convertView.findViewById(R.id.other_need_list_row2_share_button);
                holder.helpBtn = (Button)convertView.findViewById(R.id.other_need_list_row2_help_button);
                holder.row2NumOfHelpTV = (TextView)convertView.findViewById(R.id.row2_num_of_helps_tv);
                holder.row2NumOfHelpedTV  = (TextView)convertView.findViewById(R.id.row2_num_of_helped_tv);
                holder.row2HelpRating = (RatingBar) convertView.findViewById(R.id.otherNeedListHelpRatingBar);
                holder.row2HelpedRating = (RatingBar) convertView.findViewById(R.id.otherNeedListHelpedRatingBar);
                holder.callBtn = (Button) convertView.findViewById(R.id.other_need_list_row2_call_button);


                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }


            Need currNeed = otherNeedsList.get(pos);


            if (currNeed != null)
            {
                holder.text.setText(currNeed.getPublishedBy());
                holder.text2.setText(currNeed.getTitle());
                holder.row2DescriptionTextView.setText(currNeed.getDescription());
                holder.shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = otherNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)otherNeedLV.getItemAtPosition(position);


                        String sharedMessage = "This is a shared need from DailyHelps App. Need publisher '"+clickedItem.getPublishedBy()+
                                "' needs help. Title: "+clickedItem.getTitle()+"; Description: "+clickedItem.getDescription()+"; Expire time: "+
                                clickedItem.getExpireDate()+" "+clickedItem.getExpireTime()+"; shared by "+currentUser.getUsername()+".";

                        Toast.makeText(getActivity().getApplicationContext(),sharedMessage,Toast.LENGTH_LONG).show();
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
                        int position = otherNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)otherNeedLV.getItemAtPosition(position);
                        // set global needItem as clicked one for later use
                        selectedNeed = clickedItem;
                        // send email notification to publisher
                        sendEmailToPublisher(clickedItem);

                        // change status, pick time;
                        // add userid and username to need;
                        // add 1 to numOfHelp for helper, add needId to helper's helping list
                        // add 1 to numOfHelped for publisher, add needId to publisher's being helped list
                        updateNeed(clickedItem);

                    }
                });

                holder.callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = otherNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)otherNeedLV.getItemAtPosition(position);

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



    /**
     * send email to publisher
     * @param clickedItem
     */
    private void sendEmailToPublisher(Need clickedItem){
        // Async task - send email notification to publisher
        String subject = "Daily Helps";
        String message = "Dear " + clickedItem.getPublishedBy() + ","
                + "\n\n Your need has been picked up by " + currentUser.getUsername();
        (new SendMail(context,clickedItem.getPublishUserId(),subject,message)).execute();
    }

    /**
     * update need status
     * add helper id, name, help time
     * @param clickedItem
     */
    private void updateNeed(Need clickedItem){

        Calendar currentCalendar = Calendar.getInstance();
        String fullFormat = "dd/MM/yyyy HH:mm:ss.SSS";
        SimpleDateFormat sdf = new SimpleDateFormat(fullFormat);
        String currentTime = sdf.format(currentCalendar.getTime());

        Log.i("CurrentUserNodeID",currentUser.getFireUserNodeId());

        clickedItem.setStatus("Picked");
        clickedItem.setHelpedByUserId(currentUser.getUserid());
        clickedItem.setHelpedByUserName(currentUser.getUsername());
        clickedItem.setHelpedByUserFirebaseNodeId(currentUser.getFireUserNodeId());
        clickedItem.setHelpedTime(currentTime);

        Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);
        // set value and add completion listener
        myFirebaseRef.child("needs").child("questions").child(clickedItem.getNeedFirebaseNodeId()).setValue(clickedItem,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(context,"Need record failed to be updated " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"Need record updated successfully.",Toast.LENGTH_SHORT).show();

                    // add 1 to numOfHelp for helper, add needId to helper's helping list
                    readHelperDataFromFirebase(currentUser.getFireUserNodeId());

                    // add 1 to numOfHelped for publisher, add needId to publisher's being helped list
                    readPublisherDataFromFirebase(selectedNeed.getPublisherUserFirebaseId());

                }
            }
        });
    }

    /**
     * read helper data by firebase node id
     * @param firebaseNodeId
     */
    private void readPublisherDataFromFirebase(String firebaseNodeId){
        Firebase.setAndroidContext(context);
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("users");

        Query query = firebaseRef.orderByChild("fireUserNodeId").equalTo(firebaseNodeId);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User publisherUser;
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            publisherUser = userSnapshot.getValue(User.class);

                            // add 1 to numOfHelp for helper, add needId to helper's helping list
                            updatePublisherProperty(publisherUser);

                            return;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", firebaseError.toException());
                    }
                });
    }

    /**
     * add 1 to numOfHelp for helper, add needId to helper's helping list
     * @param publisherUser
     */
    private void updatePublisherProperty(User publisherUser){

        int numOfBeingHelped = publisherUser.getNumOfBeingHelped()+1;  // add 1 to being helped number
        ArrayList<String> beingHelpedNeedList = publisherUser.getBeingHelpedNeedList();

        if(beingHelpedNeedList!=null&&beingHelpedNeedList.size()>0){
            beingHelpedNeedList.add(selectedNeed.getNeedFirebaseNodeId());
        }else{
            beingHelpedNeedList = new ArrayList<String>();
            beingHelpedNeedList.add(selectedNeed.getNeedFirebaseNodeId());
        }

        publisherUser.setNumOfBeingHelped(numOfBeingHelped);
        publisherUser.setBeingHelpedNeedList(beingHelpedNeedList);

        // update firebase user data
        updatePublisherFirebase(publisherUser);
    }

    /**
     * update a helper user object in Firebase database by user firebase node id
     */
    private void updatePublisherFirebase(User publisherUser){
        Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);
        // set value and add completion listener
        myFirebaseRef.child("users").child(publisherUser.getFireUserNodeId()).setValue(publisherUser,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(context,"Publisher data could not be updated. " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"Publisher data updated successfully.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * read helper data by firebase node id
     * @param firebaseNodeId
     */
    private void readHelperDataFromFirebase(String firebaseNodeId){

        Firebase.setAndroidContext(context);

        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("users");
        Query query = firebaseRef.orderByChild("fireUserNodeId").equalTo(firebaseNodeId);

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            User userHelper = userSnapshot.getValue(User.class);

                            // add 1 to numOfHelp for helper, add needId to helper's helping list
                            updateHelperProperty(userHelper);

                            return;
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", firebaseError.toException());
                    }
                });
    }

    /**
     * add 1 to numOfHelp for helper, add needId to helper's helping list
     * @param userHelper
     */
    private void updateHelperProperty(User userHelper){

        int numOfHelping = userHelper.getNumOfHelping()+1;  // add 1 to helping number
        ArrayList<String> helpingNeedList = userHelper.getHelpingNeedList();

        if(helpingNeedList!=null&&helpingNeedList.size()>0){
            helpingNeedList.add(selectedNeed.getNeedFirebaseNodeId());
        }else{
            helpingNeedList = new ArrayList<String>();
            helpingNeedList.add(selectedNeed.getNeedFirebaseNodeId());
        }

        userHelper.setNumOfHelping(numOfHelping);
        userHelper.setHelpingNeedList(helpingNeedList);

        // update firebase user data
        updateHelperFirebase(userHelper);
    }

    /**
     * update a helper user object in Firebase database by user firebase node id
     */
    private void updateHelperFirebase(User userHelper){
        Firebase myFirebaseRef = new Firebase(Config.FIREBASE_URL);
        // set value and add completion listener
        myFirebaseRef.child("users").child(userHelper.getFireUserNodeId()).setValue(userHelper,new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(context,"Helper data could not be updated. " + firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"Helper data updated successfully.",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void sendNotification() throws IOException, JSONException {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            final String apiKey = "AIzaSyBv-IIackJ2whXi_7X5AgPDvqDnsUYpX1s";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=AIzaSyBv-IIackJ2whXi_7X5AgPDvqDnsUYpX1s");
            conn.setRequestMethod("GET");

            conn.setDoOutput(true);
            // get current token from Firebase
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("TOKEN", "Refreshed token: " + refreshedToken);


            // String input = "{\"notification\" : {\"title\" : \"Test\"}, \"to\":\"c_0ySS9Tzmk:APA91bEiSjZhdlVYoH-IsjcnHdhG120bO5n3y0wd_McaWs8jfc66Ft22osXhmUgAcckkE_O5Fg1SMpoYs93d8PkYI1y8k1Go_4HmeJ9lsqbk_eB6GZn90KRPhn7qjjj2gKorPRyQCKGi\"}";

            JSONObject msg=new JSONObject();
            msg.put("body","Your need has been picked up!");
            msg.put("title","Daily Helps");
            msg.put("icon","icon");



//            JSONObject msg=new JSONObject();
//            msg.put("message","Hello");



            JSONObject parent=new JSONObject();

            parent.put("to", refreshedToken);
            // parent.put("priority","high");
            //parent.put("content_available",true);
            parent.put("notification", msg);


            OutputStream os = conn.getOutputStream();
            os.write(parent.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + parent.toString());
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendNotification2() throws IOException {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            try {

                String url = "http://fcm.googleapis.com/fcm/send";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "key=AIzaSyBv-IIackJ2whXi_7X5AgPDvqDnsUYpX1s");
                con.setRequestMethod("POST");

                // get current token from Firebase
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                Log.d("TOKEN", "Refreshed token: " + refreshedToken);


                JSONObject msg=new JSONObject();
                msg.put("body","Your need has been picked up!");
                msg.put("title","Daily Helps");

                JSONObject parent=new JSONObject();

                parent.put("to", refreshedToken);
                parent.put("priority","high");
                parent.put("notification", msg);

                // String urlParameters = "registration_id="+firebaseIds.get(0);
                // Send post request
                con.setDoOutput(true);


                OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
                wr.write(parent.toString());

                // DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                // wr.writeBytes(urlParameters);
                // wr.flush();
                // wr.close();

                int responseCode = con.getResponseCode();
                System.out.println("Sending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + parent.toString());
                System.out.println("Response Code : " + responseCode+" "+con.getResponseMessage());



            } catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void setPublisherInfo(String userFirebaseId, final ViewHolder holder){

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

    private void setPublisherPhoto(String userFirebaseId, final ViewHolder holder){
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

    // View holder, do not need to find view each time
    static class ViewHolder
    {
        TextView text;                      // user name
        TextView text2;                     // title
        TextView row2DescriptionTextView;   // description
        ImageView icon;                     // publisher photo
        Button shareBtn;
        Button helpBtn;                     // help button
        Button callBtn;
        TextView row2NumOfHelpTV;           // number of helps
        TextView row2NumOfHelpedTV;         // number of helped
        RatingBar row2HelpRating;           // rating of help
        RatingBar row2HelpedRating;         // rating of helped
        Button thankBtn;                 // to leave feedback for publisher
    }


}
