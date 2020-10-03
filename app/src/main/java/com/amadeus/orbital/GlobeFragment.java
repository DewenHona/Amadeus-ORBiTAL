package com.amadeus.orbital;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LookAt;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globe.BasicElevationCoverage;
import gov.nasa.worldwind.layer.BackgroundLayer;
import gov.nasa.worldwind.layer.BlueMarbleLandsatLayer;
import gov.nasa.worldwind.layer.RenderableLayer;
import gov.nasa.worldwind.render.Color;
import gov.nasa.worldwind.shape.Placemark;
class satPos{
    private float lat;
    private float lng;
    private float h;
    satPos(float lat,float lng,float h){
        this.lat=lat;
        this.lng=lng;
        this.h=h;
    }

    public float getH() {
        return h;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }
}
public class GlobeFragment extends Fragment {
    View rootView;
    private WorldWindow wwd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.frag_globe,container,false);
        FrameLayout globeLayout = (FrameLayout) rootView.findViewById(R.id.globe);

        // Add the WorldWindow view object to the layout that was reserved for the globe.
        globeLayout.addView(this.createWorldWindow());

        FloatingActionButton Searchbtn= rootView.findViewById(R.id.SearchBtn);
        Searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getContext(),SearchActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }
     private Placemark plotpoints(satPos s){
        float h=s.getH();
        float lng=s.getLng();
        float lat=s.getLat();
         Placemark ventura = Placemark.createWithColorAndSize(Position.fromDegrees(lat, lng, h), new Color(1, 1, 1, 1), 20);
         return ventura;
     }


    public WorldWindow createWorldWindow() {
        // Create the WorldWindow (a GLSurfaceView) which displays the globe.
        this.wwd = new WorldWindow(getContext());
        // Setup the WorldWindow's layers.
        this.wwd.getLayers().addLayer(new BackgroundLayer());
        this.wwd.getLayers().addLayer(new BlueMarbleLandsatLayer());
        //this.wwd.getLayers().addLayer(new AtmosphereLayer());
        // Setup the WorldWindow's elevation coverages.
        this.wwd.getGlobe().getElevationModel().addCoverage(new BasicElevationCoverage());

            RenderableLayer placemarksLayer = new RenderableLayer("Placemarks");
            wwd.getLayers().addLayer(placemarksLayer);

        ArrayList<satPos> positionList=new ArrayList<>();

        positionList.add(new satPos(350,199,100000));
        positionList.add(new satPos(370,200,100000));
        positionList.add(new satPos(390,300,100000));
        positionList.add(new satPos(400,50,100000));
        positionList.add(new satPos(550,309,100000));
        ArrayList<Placemark> ps= new ArrayList<>();
        for(satPos s:positionList){

           Placemark p=plotpoints(s);
           ps.add(p);

        }
        for(Placemark p:ps){
            placemarksLayer.addRenderable(p);
        }
            Position pos =ps.get(0).getPosition();
            LookAt lookAt = new LookAt().set(pos.latitude, pos.longitude, pos.altitude, WorldWind.ABSOLUTE,
                    1e5 /*range*/, 0 /*heading*/, 80 /*tilt*/, 0 /*roll*/);
            wwd.getNavigator().setAsLookAt(wwd.getGlobe(), lookAt);

        return this.wwd;
    }
    public WorldWindow getWorldWindow() {
        return this.wwd;
    }
    @Override
    public void onResume() {
        super.onResume();
        this.wwd.onResume(); // resumes a paused rendering thread
    }
    @Override
    public void onPause() {
        super.onPause();
        this.wwd.onPause(); // pauses the rendering thread
    }
}
