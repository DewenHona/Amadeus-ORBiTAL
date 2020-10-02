package com.amadeus.orbital;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<satClass> satnamelist=new ArrayList<satClass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);




        CollectionReference dbref = db.collection("satlist");
        dbref.addSnapshotListener(this, new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                satnamelist.clear();

                for(QueryDocumentSnapshot documentSnapshot: value){
                    if(documentSnapshot.exists()){

                        String satname= documentSnapshot.get("name").toString();
                        String satdesc= documentSnapshot.get("desc").toString();
                        String image= documentSnapshot.get("image").toString();
                        Log.d("YO", "onEvent: "+ satname);
                        satnamelist.add(new satClass(image,satname,satdesc));
                    }
                }
                recyclerView=findViewById(R.id.recylerView);
                recyclerView.setHasFixedSize(true);
                layoutManager=new LinearLayoutManager(SearchActivity.this);
                mAdapter=new satListAdapter(satnamelist);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);
            }
        });

//        satnamelist.add(new satClass(R.drawable.thunder,"Swaraj","Hello"));
//        satnamelist.add(new satClass(R.drawable.skull,"Atharva","Hi"));
//        satnamelist.add(new satClass(R.drawable.thunder,"Swaraj","Hello"));
//        satnamelist.add(new satClass(R.drawable.skull,"Atharva","Hi"));
//        satnamelist.add(new satClass(R.drawable.thunder,"Swaraj","Hello"));
//        satnamelist.add(new satClass(R.drawable.skull,"Atharva","Hi"));


        //ggg
    }
}