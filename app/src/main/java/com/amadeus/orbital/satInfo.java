package com.amadeus.orbital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class satInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sat_info);

        Intent i = getIntent();
        String name = i.getStringExtra("satname");
        String desc = i.getStringExtra("satdesc");
        String image = i.getStringExtra("satimage");

        TextView nametext= findViewById(R.id.nameSatInfo);
        TextView desctext=findViewById(R.id.descSatInfo);
        ImageView imageView=findViewById(R.id.imageSatInfo);

        nametext.setText(name);
        desctext.setText(desc);
        Glide.with(imageView.getContext())
                .load(image)
                .into(imageView);

    }
}