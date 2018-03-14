package com.webatron.rakesh.assignsample;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    int flag =0;
    SharedPreferences userdetasils;
    String usernumber,prof,proname,pronum,resourcepath;
    TextView name,number;
    Uri imageuri;
    List<FileDetails>files;
    File file;
    ImageView propic,upload;
    RecyclerView imagelist;
    FileAdaptor adaptor;
    Thread t1;
    int progressflag = 0;
    ProgressDialog progressDialog;
    public static  int GALLERQ1=1;
    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_profile, container, false);

        name = (TextView) view.findViewById(R.id.profilename);
        number = (TextView) view.findViewById(R.id.profilenumber);
        propic = (ImageView) view.findViewById(R.id.profilepic);
        upload = (ImageView)view.findViewById(R.id.upload);
        progressDialog = new ProgressDialog(getContext());
        userdetasils = this.getContext().getSharedPreferences("USER",MODE_PRIVATE);
        usernumber = userdetasils.getString("Mobile",null);

        imagelist = (RecyclerView)view.findViewById(R.id.downloadview);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        imagelist.setLayoutManager(manager);

        files = new ArrayList<>();

        name.setText(userdetasils.getString("Name",null));
        number.setText(usernumber);
        Picasso.with(getContext()).load(userdetasils.getString("profile",null)).into(propic);

        progressDialog.setMessage("Uploading. . .. .");

       // getProfiledetails(usernumber);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                getProfiledetails(usernumber);
            }
        });

        /*      new Thread(new Runnable() {
            @Override
            public void run() {



                // adaptor.notifyDataSetChanged();
            }
        }).start();*/

        /*adaptor = new FileAdaptor(getContext(),files);
        imagelist.setAdapter(adaptor);
*/

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageurl();
                // progressDialog.show()

            }
        });






        return view;
    }



    private void uploadRequest(){

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mobile",usernumber)
                .addFormDataPart("userfile",file.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"),file))
                .build();

        Request request = new Request.Builder()
                .url("http://museon.net/testapp/mapp/api/upload")
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Log.i("Response :",response.body().string());
            if(response.isSuccessful()){
                //progressDialog.dismiss();
                Log.i("Json Response :","Successful");

                progressflag =1;
                //t1.start();
                //getProfiledetails(usernumber);
                displ();

                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getProfiledetails(usernumber);
                    }
                });*/

                /*
                new Thread(new Runnable() {
                    @Override
                    public void run() {




                        // adaptor.notifyDataSetChanged();
                    }
                }).start();*/

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void getImageurl(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,GALLERQ1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERQ1 && resultCode == RESULT_OK){
            imageuri = data.getData();
            Toast.makeText(getContext(),"Image Loaded Sucessfully",Toast.LENGTH_SHORT).show();
            Log.i("ImageUri : ",""+imageuri);
            resourcepath = getPath(imageuri);
            file = new File(resourcepath);
            Log.i("File Name :",""+file.getName());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    uploadRequest();
                }
            }).start();


            adaptor.notifyDataSetChanged();

        }else {
            Toast.makeText(getContext(),"Image Loading failed. .!",Toast.LENGTH_SHORT).show();
        }
    }


    public String getPath(Uri img){

        String path;
        Cursor cursor = null;
        try{
            String [] pro = {MediaStore.Images.Media.DATA};

            cursor = getContext().getContentResolver().query(img,pro,null,null,null);
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

    private void getProfiledetails(String usernumber) {


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
                            Log.i("Uploads",json.getString("uploaded_images"));
                            JSONArray jimage = json.getJSONArray("uploaded_images");

                            for(int i =0;i<jimage.length();i++){
                                String url = jimage.getJSONObject(i).getString("image");
                                String name = url.substring(url.lastIndexOf('/')+1);
                                Log.i("filename ",name);
                                FileDetails file = new FileDetails(name,url);
                                Log.i("filename ",file.getName());
                                files.add(file);

                            }

                        }
                        adaptor = new FileAdaptor(getContext(),files);
                        imagelist.setAdapter(adaptor);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    public void displ(){
        if(flag==1){

            Log.i("Rex","called");

        }
    }


}
