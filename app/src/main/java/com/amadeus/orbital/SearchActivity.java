package com.amadeus.orbital;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ArrayList<satClass> satnamelist= new ArrayList<>();

        satnamelist.add(new satClass(R.drawable.thunder,"Swaraj","Hello"));
        satnamelist.add(new satClass(R.drawable.skull,"Atharva","Hi"));
        satnamelist.add(new satClass(R.drawable.thunder,"Swaraj","Hello"));
        satnamelist.add(new satClass(R.drawable.skull,"Atharva","Hi"));
        satnamelist.add(new satClass(R.drawable.thunder,"Swaraj","Hello"));
        satnamelist.add(new satClass(R.drawable.skull,"Atharva","Hi"));

        recyclerView=findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        mAdapter=new satListAdapter(satnamelist);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        //ggg
    }
}