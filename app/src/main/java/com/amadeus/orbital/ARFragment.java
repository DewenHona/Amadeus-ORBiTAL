package com.amadeus.orbital;
import android.content.Intent;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amadeus.orbital.common.helpers.CameraPermissionHelper;
import com.amadeus.orbital.common.helpers.DisplayRotationHelper;
import com.amadeus.orbital.common.helpers.FullScreenHelper;
import com.amadeus.orbital.common.helpers.SnackbarHelper;
import com.amadeus.orbital.common.helpers.TapHelper;
import com.amadeus.orbital.common.rendering.BackgroundRenderer;
import com.amadeus.orbital.common.rendering.ObjectRenderer;
import com.amadeus.orbital.common.rendering.PlaneRenderer;
import com.amadeus.orbital.common.rendering.PointCloudRenderer;
import com.amadeus.orbital.helloar.AsyncFileDownloader;
import com.amadeus.orbital.helloar.AsyncHttpRequest;
import com.amadeus.orbital.helloar.HelloArActivity;
import com.amadeus.orbital.helloar.PolyApi;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.zip.Inflater;

import javax.microedition.khronos.opengles.GL10;

public class ARFragment extends Fragment  {
 private View rootView;
//a
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.frag_ar,container,false);

        ExtendedFloatingActionButton openarbtn= rootView.findViewById(R.id.openArBtn);
        openarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getContext(), HelloArActivity.class);
                startActivity(i);
            }
        });//a//a

        return rootView;//aa
    }//aa
}


