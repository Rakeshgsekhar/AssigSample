package com.webatron.rakesh.assignsample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by rakesh on 13/3/18.
 */

public class ContactAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<UserData> users;

    public ContactAdaptor(Context context, List<UserData> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflator.inflate(R.layout.list_contact,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserData data = users.get(position);

        ViewHolder hold =(ViewHolder)holder;
        hold.name.setText(data.getName());
        hold.mobile.setText(data.getMob());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name,mobile;
        public ViewHolder(View itemView) {
            super(itemView);

            name =(TextView) itemView.findViewById(R.id.listname);
            mobile =(TextView)itemView.findViewById(R.id.listnumber);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            UserData user = users.get(getPosition());
            Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context,ContactView.class);
            intent.putExtra("name",user.getName());
            intent.putExtra("mobile",user.getMob());
            intent.putExtra("url",user.getUrl());

            context.startActivity(intent);
        }
    }
}
