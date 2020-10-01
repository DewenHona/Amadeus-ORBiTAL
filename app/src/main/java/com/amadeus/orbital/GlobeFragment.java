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

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.globe.BasicElevationCoverage;
import gov.nasa.worldwind.layer.BackgroundLayer;
import gov.nasa.worldwind.layer.BlueMarbleLandsatLayer;

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
