package com.webatron.rakesh.assignsample;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contact extends Fragment {


    String[] mob;
    String[] resultmob;
    int count,i=0;
    String id,name,mobile;
    ContactAdaptor adaptor;
    List<UserData> users;
    RecyclerView contactlist;
    public Contact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.fragment_contact, container, false);

        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        contactlist =(RecyclerView)view.findViewById(R.id.contacts);
        count = cursor.getCount();
        mob = new String[count];

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        contactlist.setLayoutManager(manager);
        users = new ArrayList<>();

        if((cursor !=null ? cursor.getCount() :0)>0){

            while (cursor !=null && cursor.moveToNext()){
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))>0){

                    Cursor phoneCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" =?",new String[]{id},null);
                    while(phoneCur.moveToNext()){
                        mobile = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //mobile;

                        mob[i]=mobile;
                        Log.i("Name: ",name);
                        //  Log.i("Mobile: ",mobile);

                    }
                }

                i++;
            }
        }

        if(cursor !=null){
            cursor.close();
        }

        new getContacts().execute();

        return view;
    }

    public class getContacts extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            String result="Result";
            try {
                URL url = new URL("http://museon.net/testapp/mapp/api/profile");
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(connection.getInputStream());

                result = inputStreamtoString(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("Result",s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("list");

                //resultmob = new String[jsonArray.length()];
                Log.i("FIST VALUE",jsonArray.getJSONObject(0).getString("mobile"));
                for(int i=0;i<jsonArray.length();i++){
                    for(int j=0;j<mob.length;j++){
                        if((jsonArray.getJSONObject(i).getString("mobile")).equals(mob[j])){
                            String name=jsonArray.getJSONObject(i).getString("name");
                            String mobile =jsonArray.getJSONObject(i).getString("mobile");
                            String url = jsonArray.getJSONObject(i).getString("profile_pic");
                            Log.i("Json Number : ",mobile);
                            UserData data = new UserData(name,mobile,url);
                            users.add(data);
                        }
                    }

                    adaptor = new ContactAdaptor(getContext(),users);
                    contactlist.setAdapter(adaptor);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String inputStreamtoString(InputStream si){
        String rLine="";
        StringBuilder finalstring = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(si);
        BufferedReader read = new BufferedReader(reader);

        try{
            while ((rLine = read.readLine())!=null){
                finalstring.append(rLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalstring.toString();
    }


}
