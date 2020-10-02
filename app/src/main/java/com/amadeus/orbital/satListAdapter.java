package com.amadeus.orbital;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.common.collect.BiMap;

import java.util.ArrayList;

public class satListAdapter extends RecyclerView.Adapter<satListAdapter.ExampleVViewHolder> {


    private ArrayList<satClass> list;

    public static class ExampleVViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView name;
        public TextView desc;
        public ExampleVViewHolder(@NonNull View itemView) {
            super(itemView);
           image=itemView.findViewById(R.id.imageCard);
           name=itemView.findViewById(R.id.nameCard);
           desc=itemView.findViewById(R.id.descCard);
        }
    }

    public satListAdapter(ArrayList<satClass> list){
        this.list=list;
    }
    @NonNull
    @Override
    public ExampleVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sat,parent,false);
        ExampleVViewHolder evh =new ExampleVViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleVViewHolder holder, int position) {
        final satClass currentItem = list.get(position);
        Log.d("check", "onBindViewHolder: "+currentItem.getName());

        holder.name.setText(currentItem.getName());
        holder.desc.setText(currentItem.getDesc());
        String imageurl= currentItem.getImageres();
        Glide.with(holder.image.getContext())
                .load(imageurl)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(),satInfo.class);
                i.putExtra("satname",currentItem.getName());
                i.putExtra("satdesc",currentItem.getDesc());
                i.putExtra("satimage",currentItem.getImageres());
               v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("listsize", "getItemCount: "+list.size());
        return list.size();
    }

}
