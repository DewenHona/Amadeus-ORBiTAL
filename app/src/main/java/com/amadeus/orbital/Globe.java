package com.amadeus.orbital;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layer.BackgroundLayer;
import gov.nasa.worldwind.layer.BlueMarbleLandsatLayer;

public class Globe extends AppCompatActivity {
    private WorldWindow wwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_globe);
        FrameLayout globeLayout = (FrameLayout) findViewById(R.id.globe);

        // Add the WorldWindow view object to the layout that was reserved for the globe.
        globeLayout.addView(this.createWorldWindow());



    }
    public WorldWindow createWorldWindow() {
        // Create the WorldWindow (a GLSurfaceView) which displays the globe.
        this.wwd = new WorldWindow(Globe.this);
        // Setup the WorldWindow's layers.
        this.wwd.getLayers().addLayer(new BackgroundLayer());
        this.wwd.getLayers().addLayer(new BlueMarbleLandsatLayer());
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