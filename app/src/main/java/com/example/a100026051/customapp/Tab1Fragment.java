package com.example.a100026051.customapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by 100026051 on 10/30/16.
 */
public class Tab1Fragment extends Fragment{

    private FloatingActionButton fab;
    private ArrayList<Need> myNeedsList;

    Context context;
    private LayoutInflater mInflater;
    ListView myNeedLV;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_tab1,container, false);

        myNeedLV= (ListView)rootView.findViewById(android.R.id.list);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(),PublishNeedActivity.class);
                startActivity(intent);
            }
        });

        context = getActivity().getApplicationContext();

        mInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // checkNetworkState();

        if(!isOnline()){
            Toast.makeText(context,"Network is not connected...",Toast.LENGTH_LONG).show();
        }

        loadNeeds();

        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadNeeds(){
        // get current user from main activity
        User userFromActivity = ((MainActivity)this.getActivity()).getCurrentUser();
        final String userid = userFromActivity.getUserid();

        myNeedsList = new ArrayList<Need>();

        Firebase.setAndroidContext(this.getActivity());
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("needs").child("questions");

        // order by status - make picked at the top
        Query myQuestionNeeds = firebaseRef.orderByChild("status");

        myQuestionNeeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myNeedsList.clear();
                for (DataSnapshot need : dataSnapshot.getChildren()) {
                    if(userid.equals(need.getValue(Need.class).getPublishUserId())){
                        myNeedsList.add(need.getValue(Need.class));
                        Log.i("ON DATA CHANGE",need.getValue(Need.class).toString());
                    }
                }

                myNeedLV.setAdapter(new Tab1Fragment.MyNeedListAdapter(context, R.layout.list_row_my_need, R.id.row_userid, myNeedsList));
                myNeedLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TextView row2DescriptionTV = (TextView)view.findViewById(R.id.row2_description);
                        Button removeButton = (Button)view.findViewById(R.id.my_need_list_remove_btn);
                        Button thankButton = (Button)view.findViewById(R.id.my_need_list_thank_btn);

                        if ( row2DescriptionTV.getVisibility() == View.GONE)
                        {
                            //expandedChildList.set(arg2, true);
                            row2DescriptionTV.setVisibility(View.VISIBLE);
                            if("Picked".equals(myNeedsList.get(i).getStatus())){
                                thankButton.setVisibility(View.VISIBLE);
                            }else{
                                removeButton.setVisibility(View.VISIBLE);
                            }

                        }
                        else
                        {
                            //expandedChildList.set(arg2, false);
                            row2DescriptionTV.setVisibility(View.GONE);
                            removeButton.setVisibility(View.GONE);
                            thankButton.setVisibility(View.GONE);

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
    class MyNeedListAdapter extends ArrayAdapter<Need>
    {
        private ArrayList<Need> movies;
        public MyNeedListAdapter(Context c, int rowResourceId, int textViewResourceId,
                                     ArrayList<Need> items) {
            super(c, rowResourceId, textViewResourceId, items);
            movies  = items;
        }


        // View recycling
        public View getView(int pos, View convertView, ViewGroup parent)
        {
            Tab1Fragment.ViewHolder holder;
            if(convertView==null){	// create one if empty
                convertView = mInflater.inflate(R.layout.list_row_my_need, parent, false);
                Log.w("MVMVMVMVMVMV", "Creating row view at position "+pos);
                holder = new Tab1Fragment.ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.row_icon);
                holder.text = (TextView) convertView.findViewById(R.id.row_publish_time);
                holder.text2 = (TextView) convertView.findViewById(R.id.row_title);
                holder.statusText = (TextView) convertView.findViewById(R.id.row_status);
                holder.row2DescriptionTextView = (TextView) convertView.findViewById(R.id.row2_description);
                holder.removeBtn = (Button)convertView.findViewById(R.id.my_need_list_remove_btn);
                holder.thankBtn = (Button)convertView.findViewById(R.id.my_need_list_thank_btn);

                convertView.setTag(holder);
            }else{
                holder = (Tab1Fragment.ViewHolder)convertView.getTag();
            }

            Need currNeed = myNeedsList.get(pos);

            if (currNeed != null)
            {
                holder.text.setText(currNeed.getPublishTime());
                holder.text2.setText(currNeed.getTitle());
                holder.statusText.setText(currNeed.getStatus());
                if("Picked".equals(currNeed.getStatus())){
                    holder.statusText.setTextColor(getResources().getColor(R.color.light_blue));
                }
                if(currNeed.getType().equals("question")){
                    holder.icon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_question_mark));
                }
                holder.row2DescriptionTextView.setText(currNeed.getDescription());
                holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = myNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)myNeedLV.getItemAtPosition(position);

                        Toast.makeText(getActivity().getApplicationContext(),"Clicked on: "+clickedItem.getTitle(),Toast.LENGTH_SHORT).show();
                        // remove from firebase
                        removeNeedItem(clickedItem);
                    }
                });

                holder.thankBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get the button's parrent (listitem) view's position
                        int position = myNeedLV.getPositionForView((View)v.getParent());
                        // get item by position
                        Need clickedItem = (Need)myNeedLV.getItemAtPosition(position);

                        FragmentManager fm = getActivity().getSupportFragmentManager();

                        // convert need object to JSON and put into Bundle, send to diaplog fragment
                        Gson gson = new Gson();
                        String needJson = gson.toJson(clickedItem);
                        Bundle args = new Bundle();
                        args.putString("needJson", needJson);


                        DialogFragment dialog = new PublisherFeedbackDialogFragment();
                        dialog.setArguments(args);
                        dialog.show(fm, "dialog");
                    }
                });

                if("Picked".equals(currNeed.getStatus())) {
                    if(currNeed.getHelpedByUserFirebaseNodeId()!=null){
                        setHelperPhoto(currNeed.getHelpedByUserFirebaseNodeId(),holder);
                    }
                }

            }

            return convertView;
        }
    }


    // View holder, do not need to find view each time
    static class ViewHolder
    {
        TextView text;
        TextView text2;
        TextView row2DescriptionTextView;
        ImageView icon;
        TextView statusText;
        Button removeBtn;
        Button thankBtn;
    }

    private void removeNeedItem(Need needItem){

        Firebase.setAndroidContext(this.getActivity());
        Firebase firebaseRef = new Firebase(Config.FIREBASE_URL).child("needs").child("questions");

        Log.i("publishtime",needItem.getPublishTime());
        Query remoeQuery = firebaseRef.orderByChild("publishTime").equalTo(needItem.getPublishTime());

        remoeQuery.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot needSnapshot: dataSnapshot.getChildren()) {
                            // remove node value
                            needSnapshot.getRef().removeValue();
                        }
                        Toast.makeText(getActivity().getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.w("TodoApp", "getUser:onCancelled", firebaseError.toException());
                        Toast.makeText(context,"Failed. Please check your network state.",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void setHelperPhoto(String userFirebaseId, final Tab1Fragment.ViewHolder holder){
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
                Toast.makeText(context,"Failed. Please check your network state.",Toast.LENGTH_SHORT).show();

            }
        });
    }

}
