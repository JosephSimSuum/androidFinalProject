package com.example.notetaker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        TextView title, description;
        title=findViewById(R.id.title);
        description=findViewById(R.id.description);
        String pId,pTitle,pDescription;

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){

            pId=bundle.getString("pId");
            pTitle=bundle.getString("pTitle");
            pDescription=bundle.getString("pDescription");

            title.setText(pTitle);
            description.setText(pDescription);
        }else {
            title.setText("No Data");

        }


    }
}