package com.example.a100026051.customapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by 100026051 on 10/31/16.
 */
public class PublishNeedActivity extends AppCompatActivity {

    ListView typeListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_need);


        initializeActionBar();
        initializeTypeList();
    }

    private void initializeActionBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.publish_need_toolbar);
        toolbar.setTitle("Publish Need");
        setSupportActionBar(toolbar);
    }

    private void initializeTypeList(){
        // Get ListView object from xml
        typeListView = (ListView) findViewById(R.id.type_list);

        // Defined Array values to show in ListView
        String[] values = new String[] {
                "Answer",
                "Goods",
                "Volunteer",
                "Job",
                "Friend",
                "Photo",
                "Others"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        typeListView.setAdapter(adapter);

        // ListView Item Click Listener
        typeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item value
                String  itemValue    = (String) typeListView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(), "Clicked on Item : " +itemValue , Toast.LENGTH_SHORT).show();

                switch (itemValue){
                    case "Answer":
                        publishNeedAnswerStep2();
                        break;

                    case "Goods":
                        publishNeedGoodsStep2();
                        break;
                    default:
                        break;

                }

            }

        });
    }

    private void publishNeedAnswerStep2(){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(),PublishNeedAnswerStep2Activity.class);
        startActivity(intent);

    }

    private void publishNeedGoodsStep2(){

    }

}
