package com.webatron.rakesh.assignsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rakesh on 14/3/18.
 */

public class FileAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<FileDetails> files;

    public FileAdaptor(Context context, List<FileDetails> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_file_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FileDetails file = files.get(position);
        ViewHolder hold = (ViewHolder)holder;
        hold.filename.setText(file.getName());
        Picasso.with(context).load(file.getUrl()).into(hold.fileimg);

    }

    @Override
    public int getItemCount() {

        Log.i("size",""+files.size());
        return files.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView filename;
        ImageView fileimg;
        public ViewHolder(View itemView) {
            super(itemView);
            fileimg = (ImageView) itemView.findViewById(R.id.fileimg);
            filename = (TextView)itemView.findViewById(R.id.filename);

        }
    }
}
