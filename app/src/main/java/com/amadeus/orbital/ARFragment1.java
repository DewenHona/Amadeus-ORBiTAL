package com.amadeus.orbital;

import android.opengl.EGLConfig;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistryOwner;

import javax.microedition.khronos.opengles.GL10;

public interface ARFragment1 extends LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    @Nullable
    View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results);

    void onWindowFocusChanged(boolean hasFocus);

    void onSurfaceCreated(GL10 gl, EGLConfig config);

    void onSurfaceChanged(GL10 gl, int width, int height);

    void onDrawFrame(GL10 gl);
}
