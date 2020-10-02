package com.amadeus.orbital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class satInfo extends AppCompatActivity {

    FirebaseFirestore db= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sat_info);

        Intent i = getIntent();

        String satDocId=i.getStringExtra("satDocId");


        db.collection("satedata").document(satDocId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name=documentSnapshot.get("OfficialNameofSatellite").toString();
                String purpose=documentSnapshot.get("Purpose").toString();
                String country=documentSnapshot.get("CountryofContractor").toString();
                String dateofLaunch=documentSnapshot.get("DateofLaunch").toString();

                TextView nametext= findViewById(R.id.nameSatInfo);
                TextView purposetext=findViewById(R.id.descSatInfo);
                ImageView imageView=findViewById(R.id.imageSatInfo);
                TextView countrytext =findViewById(R.id.countrySatInfo);
                TextView datetext =findViewById(R.id.dateSatInfo);

                nametext.setText(name);
                purposetext.setText(purpose);
                countrytext.setText(country);
                datetext.setText(dateofLaunch);

                if(purpose.equals("Earth Observation")){

                        imageView.setImageResource(R.drawable.earth_observation);
                }
                else if(purpose.equals("Communications")){
                    imageView.setImageResource(R.drawable.communications);
                }
                else if(purpose.equals("Technology Development")){
                    imageView.setImageResource(R.drawable.technology_development);
                }
                else if(purpose.equals("Communications/Technology Development")){
                    imageView.setImageResource(R.drawable.communicationsortechnology_development);
                }
                else if(purpose.equals("Communications/Maritime Tracking")){
                    imageView.setImageResource(R.drawable.communicationsormaritime_tracking);
                }
                else if(purpose.equals("Space Science")){
                    imageView.setImageResource(R.drawable.space_science);

                }
                else if(purpose.equals("Navigation/Global Positioning")){
                    imageView.setImageResource(R.drawable.navigationorglobal_positioning);

                }
                else if(purpose.equals("Earth Observation/Technology Development")){
                    imageView.setImageResource(R.drawable.earth_observationortechnology_development);
                }
                else if(purpose.equals("Earth Science")){
                    imageView.setImageResource(R.drawable.earth_science);
                }
                else if(purpose.equals("Earth Observation/Communications")){
                    imageView.setImageResource(R.drawable.earth_observationorcommunications);
                }
                else if(purpose.equals("Earth/Space Science")){
                    imageView.setImageResource(R.drawable.earthorspace_science);
                }
                else if(purpose.equals("Earth Observation/Research")){
                    imageView.setImageResource(R.drawable.earth_observationorresearch);
                }
                else if(purpose.equals("Communications/Navigation")){
                    imageView.setImageResource(R.drawable.communicationsornavigation);
                }
                else if(purpose.equals("Space Observation")){
                    imageView.setImageResource(R.drawable.space_observation);
                }
                else if(purpose.equals("Navigation/Regional Positioning")){
                    imageView.setImageResource(R.drawable.navigationorregional_positioning);
                }
                else if(purpose.equals("Technology Demo")){
                    imageView.setImageResource(R.drawable.technology_demo);
                }
                else {
                imageView.setImageResource(R.drawable.communications);}
            }
        });



    }
}