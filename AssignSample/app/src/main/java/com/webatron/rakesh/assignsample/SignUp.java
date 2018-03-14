package com.webatron.rakesh.assignsample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    private ImageView profile;
    private EditText name,mobile;
    private Button start;
    int MY_PERMISSION=0;
    private Uri imageuri;
    private File file;
    private static final int GALLERQ=1;
    private String resourcepath,Name,Mobile;
    SharedPreferences userdetails;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        checkPermission();

        userdetails = getSharedPreferences("USER",MODE_PRIVATE);
        editor = userdetails.edit();
        profile = (ImageView) findViewById(R.id.profileimg);
        name = (EditText)findViewById(R.id.username);
        mobile = (EditText) findViewById(R.id.usermobile);

        start = (Button) findViewById(R.id.start);

        progressDialog = new ProgressDialog(SignUp.this);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Registering. . .");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postRequest();
                    }
                }).start();
            }
        });

    }

    private void postRequest(){


        Name = name.getText().toString();
        Mobile = mobile.getText().toString();

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name",Name)
                .addFormDataPart("mobile",Mobile)
                .addFormDataPart("userfile",file.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"),file))
                .build();

        Request request = new Request.Builder()
                .url("http://museon.net/testapp/mapp/api/reg")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();

            Log.i("Response :",response.body().string());

            if(response.isSuccessful()){
                progressDialog.dismiss();
                editor.putString("Name",Name);
                editor.putString("Mobile",Mobile);
                editor.putString("profile",imageuri.toString());
                editor.apply();
                Log.i("Json Response :","Successful");

                Intent intent = new Intent(SignUp.this,Home.class);
                startActivity(intent);
                finish();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void checkPermission(){

        if(ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SignUp.this,new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },MY_PERMISSION);
        }

    }


    public void getImage(){

        Intent galleryInten = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryInten,GALLERQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERQ && resultCode == RESULT_OK){
            imageuri = data.getData();
            Picasso.with(SignUp.this).load(imageuri).into(profile);
            Toast.makeText(SignUp.this,"Image Loaded Sucessfully",Toast.LENGTH_SHORT).show();
            Log.i("ImageUri : ",""+imageuri);
            resourcepath = getPath(imageuri);
            file = new File(resourcepath);
            Log.i("File Name :",""+file.getName());

        }else {
            Toast.makeText(SignUp.this,"Image Loading failed. .!",Toast.LENGTH_SHORT).show();
        }
    }


    public String getPath(Uri img){

        String path;
        Cursor cursor = null;
        try{
            String [] pro = {MediaStore.Images.Media.DATA};

            cursor = this.getContentResolver().query(img,pro,null,null,null);
            int coloumidex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(coloumidex);

        }finally {

            if(cursor !=null){
                cursor.close();
            }
        }
        Log.i("Path",": "+path);

        return path;
    }
}
