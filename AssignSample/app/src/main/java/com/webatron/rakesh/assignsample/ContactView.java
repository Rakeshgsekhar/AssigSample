package com.webatron.rakesh.assignsample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactView extends AppCompatActivity {

    TextView tittle;
    ImageView tittleimg;
    SharedPreferences userdetasils;
    String usernumber,name,url;
    List<FileDetails> files;
    RecyclerView Contactimagelist;
    FileAdaptor adaptor;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);
        tittle =(TextView)findViewById(R.id.toolbarname);
        tittleimg = (ImageView) findViewById(R.id.toolimg);

        progressDialog = new ProgressDialog(ContactView.this);
        Contactimagelist = (RecyclerView)findViewById(R.id.contactfiles);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(ContactView.this);
        Contactimagelist.setLayoutManager(manager);
        userdetasils = getSharedPreferences("USER",MODE_PRIVATE);
        Intent intent = getIntent();
        usernumber = intent.getStringExtra("mobile");
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        files = new ArrayList<>();
        tittle.setText(usernumber);
        Picasso.with(ContactView.this).load(url).into(tittleimg);


        new Thread(new Runnable() {
            @Override
            public void run() {


                getImages(usernumber);
            }
        }).start();


            adaptor = new FileAdaptor(ContactView.this, files);
            Contactimagelist.setAdapter(adaptor);
    }

    private void getImages(String usernumber) {

        //progressDialog.setMessage("Loading");
       // progressDialog.show();
        OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("mobile",usernumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mobile",usernumber)
                .build();


        final Request request = new Request.Builder()
                .url("http://museon.net/testapp/mapp/api/profile")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Failed",e.getMessage().toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //Log.i("Response: ",response.body().string());

                String mMsg = response.body().string();
                Log.i("Response: ",mMsg);
                if(response.isSuccessful()){
                    try {
                        JSONObject json =new JSONObject(mMsg);
                        JSONArray jsonObject = json.getJSONArray("profile");
                        JSONObject job = jsonObject.getJSONObject(0);

                        if(!(json.getString("uploaded_images").equals("null"))){
                            JSONArray jimage = json.getJSONArray("uploaded_images");

                            for(int i =0;i<jimage.length();i++){
                                String url = jimage.getJSONObject(i).getString("image");
                                String name = url.substring(url.lastIndexOf('/')+1);
                                Log.i("filename ",name);
                                FileDetails file = new FileDetails(name,url);
                                files.add(file);
                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });



        //progressDialog.dismiss();

    }


}
