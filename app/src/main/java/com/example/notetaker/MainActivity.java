package com.example.notetaker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    //design component
    EditText mTitle, mDescription;
    Button mSave,mScan,mCamera;

    //progress dialog
    ProgressDialog pd;

    //firestore instance
    FirebaseFirestore db;

    String pId,pTitle,pDescription;

    //initializing image
    ImageView pImage;

    Uri image_rui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle=findViewById(R.id.title);
        mDescription=findViewById(R.id.description);
        mSave=findViewById(R.id.saveBtn);

        //intialize image
        pImage= findViewById(R.id.imageView);
        mCamera=findViewById(R.id.cameraBtn);


        //Camera permission
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA)==
                            PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                            PackageManager.PERMISSION_DENIED){
                        //request if permission not enable
                        String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show pop up to request permission
                        requestPermissions(permission,PERMISSION_CODE);
                    }
                    else{
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    //system os < marshmallow
                    openCamera();
                }
            }
        });


        //progress dialog
        pd=new ProgressDialog(this);
        //firestore
        db = FirebaseFirestore.getInstance();
        //update data using Bundle
        Bundle bundle1=getIntent().getExtras();
        if(bundle1!=null){
            mSave.setText("Update");
            pId=bundle1.getString("pId");
            pTitle=bundle1.getString("pTitle");
            pDescription=bundle1.getString("pDescription");

            mTitle.setText(pTitle);
            mDescription.setText(pDescription);
        }else {
            mSave.setText("Save");
        }

        //click save button to upload data
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bundle bundle1=getIntent().getExtras();
                if(bundle1!=null){
                    //update to cloudstore
                    String id=pId;
                    String title=mTitle.getText().toString().trim();
                    String description=mDescription.getText().toString().trim();
                    updateData(id,title,description);
                }else {
                    //input data
                    String title=mTitle.getText().toString().trim();
                    String description=mDescription.getText().toString().trim();
                    //fucntion to upload data
                    uploadData(title,description);

                }
            }
        });



        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        //home seleteced
        bottomNavigationView.setSelectedItemId(R.id.home);

        //perform itemselect listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.list:
                        startActivity(new Intent(getApplicationContext(), ListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        return true;
                }

                return false;
            }
        });
    }

    private void updateData(String id, String title, String description) {
        //set title progress bar
        pd.setTitle("Updating Data ");
        pd.show();

        db.collection("Documents").document(id)
                .update("title",title, "description",description)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        mTitle.setText("");
                        mDescription.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"from camera");
        image_rui=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        // Camera intent
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_rui);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }
    //handling permission codes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //method called when user click allow or deny permission request popup
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                }else {
                    //permsiion from popup was denied
                    Toast.makeText(this, "Permsiison Denied..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //called when image ws capture from camera
        if(resultCode==RESULT_OK){
            //set image caputure to view
            pImage.setImageURI(image_rui);
            BitmapDrawable bitmapD=(BitmapDrawable)pImage.getDrawable();
            //Bitmap bitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.quote);
            //
            Bitmap bitmap=bitmapD.getBitmap();
            //pImage.setImageBitmap(bitmap);
            TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
            if(textRecognizer.isOperational()) {
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    TextBlock item = items.valueAt(i);
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }

                mDescription.setText(stringBuilder.toString());

            }

        }

    }

    private void uploadData(String title, String description) {
        //set title progress bar
        pd.setTitle("Adding Data to Firestore");
        pd.show();
        String id= UUID.randomUUID().toString();
        Map<String,Object> doc=new HashMap<>();
        doc.put("id",id);
        doc.put("title",title);
        doc.put("description",description);

        //add data to firesotre
        db.collection( "Documents").document(id).set(doc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // this called when data is added successfully
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        mTitle.setText("");
                        mDescription.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}