package com.amadeus.orbital;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LookAt;
import gov.nasa.worldwind.geom.Offset;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globe.BasicElevationCoverage;
import gov.nasa.worldwind.layer.BackgroundLayer;
import gov.nasa.worldwind.layer.BlueMarbleLandsatLayer;
import gov.nasa.worldwind.layer.RenderableLayer;
import gov.nasa.worldwind.render.Color;
import gov.nasa.worldwind.render.ImageSource;
import gov.nasa.worldwind.shape.Placemark;
import gov.nasa.worldwind.shape.PlacemarkAttributes;

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

        FloatingActionButton SateliteSearchbtn= rootView.findViewById(R.id.sateliteSearchBtn);
        SateliteSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getContext(),SateliteListActivity.class);
                startActivity(i);
            }
        });

        return rootView;
    }



    public WorldWindow createWorldWindow() {
        // Create the WorldWindow (a GLSurfaceView) which displays the globe.
        this.wwd = new WorldWindow(getContext());
        // Setup the WorldWindow's layers.
        this.wwd.getLayers().addLayer(new BackgroundLayer());
        this.wwd.getLayers().addLayer(new BlueMarbleLandsatLayer());
        // Setup the WorldWindow's elevation coverages.
        this.wwd.getGlobe().getElevationModel().addCoverage(new BasicElevationCoverage());


        // Create a RenderableLayer for placemarks and add it to the WorldWindow
        RenderableLayer placemarksLayer = new RenderableLayer("Placemarks");
        wwd.getLayers().addLayer(placemarksLayer);

        //////////////////////////////////////
        // Second, create some placemarks...
        /////////////////////////////////////

        // Create a simple placemark at downtown Ventura, CA. This placemark is a 20x20 cyan square centered on the
        // geographic position. This placemark demonstrates the creation with a convenient factory method.
        Placemark ventura = Placemark.createWithColorAndSize(Position.fromDegrees(34.281, -119.293, 20000), new Color(0, 1, 1, 1), 20);

        // Create an image-based placemark of an aircraft above the ground with a leader-line to the surface.
        // This placemark demonstrates creation via a constructor and a convenient PlacemarkAttributes factory method.
        // The image is scaled to 1.5 times its original size.
        Placemark airplane = new Placemark(
                Position.fromDegrees(34.260, -119.2, 5000),
                PlacemarkAttributes.createWithImageAndLeader(ImageSource.fromResource(R.drawable.sat2)).setImageScale(1.5));

        // Create an image-based placemark with a label at Oxnard Airport, CA. This placemark demonstrates creation
        // with a constructor and a convenient PlacemarkAttributes factory method. The image is scaled to 2x
        // its original size, with the bottom center of the image anchored at the geographic position.
        Placemark airport = new Placemark(
                Position.fromDegrees(34.200, -119.208, 0),
                PlacemarkAttributes.createWithImage(ImageSource.fromResource(R.drawable.sat2)).setImageOffset(Offset.bottomCenter()).setImageScale(2),
                "Oxnard Airport");

        // Create an image-based placemark from a bitmap. This placemark demonstrates creation with a
        // constructor and a convenient PlacemarkAttributes factory method. First, a 64x64 bitmap is loaded
        // and then it is passed into the placemark attributes. The the bottom center of the image anchored
        // at the geographic position.
        Bitmap bitmap = BitmapFactory.decodeResource(getWorldWindow().getResources(), R.drawable.sat2);
        Placemark wildfire = new Placemark(
                Position.fromDegrees(34.300, -119.25, 0),
                PlacemarkAttributes.createWithImage(ImageSource.fromBitmap(bitmap)).setImageOffset(Offset.bottomCenter()));

        /////////////////////////////////////////////////////
        // Third, add the placemarks to the renderable layer
        /////////////////////////////////////////////////////

        placemarksLayer.addRenderable(ventura);
        //placemarksLayer.addRenderable(airport);
        //placemarksLayer.addRenderable(airplane);
        //placemarksLayer.addRenderable(wildfire);


        // And finally, for this demo, position the viewer to look at the airport placemark
        // from a tilted perspective when this Android activity is created.
        Position pos = airport.getPosition();
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


