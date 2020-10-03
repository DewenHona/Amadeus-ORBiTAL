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

import org.w3c.dom.Text;

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

                String name = documentSnapshot.get("OfficialNameofSatellite").toString();
                String purpose = documentSnapshot.get("Purpose").toString();
                if(purpose.equals("")) purpose="N/A";
                String country = documentSnapshot.get("CountryofContractor").toString();
                if(country.equals("")) country="N/A";
                String operator = documentSnapshot.get("OperatorOROwner").toString();
                if(operator.equals("")) operator="N/A";
                String longitude = documentSnapshot.get("LongitudeofGeosynchronousOrbitDegrees").toString();
                if(longitude.equals("")) longitude="N/A";
                String perigee = documentSnapshot.get("PerigeeKilometers").toString();
                if(perigee.equals("")) perigee="N/A";
                String apogee = documentSnapshot.get("ApogeeKilometers").toString();
                if(apogee.equals("")) apogee="N/A";
                String eccentricity = documentSnapshot.get("Eccentricity").toString();
                if(eccentricity.equals("")) eccentricity="N/A";
                String ClassofOrbit=documentSnapshot.get("ClassofOrbit").toString();
                if(ClassofOrbit.equals("")) ClassofOrbit="N/A";
                String TypeofOrbit=documentSnapshot.get("TypeofOrbit").toString();
                if(TypeofOrbit.equals("")) TypeofOrbit="N/A";
                String period=documentSnapshot.get("PeriodMinutes").toString();
                if(period.equals("")) period="N/A";
                String launchmass=documentSnapshot.get("LaunchMass_Kilograms").toString();
                if(launchmass.equals("")) launchmass="N/A";
                String dateofLaunch=documentSnapshot.get("DateofLaunch").toString();
                if(dateofLaunch.equals("")) dateofLaunch="N/A";
                String launchSite=documentSnapshot.get("LaunchSite").toString();
                if(launchSite.equals("")) launchSite="N/A";
                String launchvehicle=documentSnapshot.get("LaunchVehicle").toString();
                if(launchvehicle.equals("")) launchvehicle="N/A";
                String COSPAR=documentSnapshot.get("COSPARNumber").toString();
                if(COSPAR.equals("")) COSPAR="N/A";
                String NORAD=documentSnapshot.get("NORADNumber").toString();
                if(NORAD.equals("")) NORAD="N/A";
                //asdsa

                TextView nametext= findViewById(R.id.nameSatInfo);
                TextView purposetext=findViewById(R.id.descSatInfo);
                ImageView imageView=findViewById(R.id.imageSatInfo);
                TextView countrytext =findViewById(R.id.countrySatInfo);
                TextView operatortext = findViewById(R.id.operatorSatInfo);
                TextView longitudetext = findViewById(R.id.longitudeSatInfo);
                TextView preigreetext=findViewById(R.id.perigeeSatInfo);
                TextView apogeetext = findViewById(R.id.apogeeSatInfo);
                TextView eccentricitytext = findViewById(R.id.eccentricitySatInfo);
                TextView ClassofOrbittext = findViewById(R.id.ClassofOrbitSatInfo);
                TextView TypeofOrbittext = findViewById(R.id.TypeofOrbitSatInfo);
                TextView periodtext=findViewById(R.id.periodSatInfo);
                TextView launchmasstext=findViewById(R.id.launchmassSatInfo);
                TextView datetext =findViewById(R.id.dateSatInfo);
                TextView launchSitetext = findViewById(R.id.launchSiteSatInfo);
                TextView launchvehicletext=findViewById(R.id.launchVehicleSatInfo);
                TextView COSPARtext =findViewById(R.id.COSPARSatInfo);
                TextView NORADtext =findViewById(R.id.NORADSatInfo);

                nametext.setText(name);
                purposetext.setText(purpose);
                countrytext.setText(country);
                operatortext.setText(operator);
                longitudetext.setText(longitude);
                preigreetext.setText(perigee);
                apogeetext.setText(apogee);
                eccentricitytext.setText(eccentricity);
                ClassofOrbittext.setText(ClassofOrbit);
                TypeofOrbittext.setText(TypeofOrbit);
                periodtext.setText(period);
                launchmasstext.setText(launchmass);
                datetext.setText(dateofLaunch);
                launchSitetext.setText(launchSite);
                launchvehicletext.setText(launchvehicle);
                COSPARtext.setText(COSPAR);
                NORADtext.setText(NORAD);

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
                    imageView.setImageResource(R.drawable.technology_demo);}
            }
        });



    }
}