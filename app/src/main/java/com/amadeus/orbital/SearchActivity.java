package com.amadeus.orbital;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private satListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<satClass> satnamelist=new ArrayList<satClass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        CollectionReference dbref = db.collection("satedata");
        dbref.addSnapshotListener(this, new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                satnamelist.clear();

                for(QueryDocumentSnapshot documentSnapshot: value){
                    if(documentSnapshot.exists()){

                        String satname= documentSnapshot.get("OfficialNameofSatellite").toString();
                        String satdesc= documentSnapshot.get("Purpose").toString();
                        String satdocID= documentSnapshot.getId();

                        //String image= documentSnapshot.get("").toString();
                        Log.d("YO", "onEvent: "+ satname);
                        satnamelist.add(new satClass(satdocID,satname,satdesc));
                    }
                }
                recyclerView=findViewById(R.id.recylerView);
                recyclerView.setHasFixedSize(true);
                layoutManager=new LinearLayoutManager(SearchActivity.this);
                mAdapter=new satListAdapter(satnamelist);
                adapter=new satListAdapter(satnamelist);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);

            }
        });


    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem searchitem =menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) searchitem.getActionView();

         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String query) {
                 return false;
             }

             @Override
             public boolean onQueryTextChange(String newText) {
                 adapter.getFilter().filter(newText);
                 return false;
             }
         });
        return true;
    }
}