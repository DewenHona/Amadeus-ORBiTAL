package com.amadeus.orbital;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.amadeus.orbital.helloar.PolyApi;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
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

public class ARFragment extends Fragment {
    private View v;
    private static final String TAG = ARFragment.class.getSimpleName();

    // The asset ID to download and display.
    private static final String ASSET_ID = "1C3zb8Q9USk";

    // Scale factor to apply to asset when displaying.
    private static final float ASSET_SCALE = 0.02f;

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;

    private boolean installRequested;

    // Create the session.
    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private TapHelper tapHelper;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];

    private final ArrayList<Anchor> anchors = new ArrayList<>();

    private ObjectRenderer virtualObject = null;

    // Our background thread, which does all of the heavy lifting so we don't block the main thread.
    private HandlerThread mBackgroundThread;

    // Handler for the background thread, to which we post background thread tasks.
    private Handler mBackgroundThreadHandler;

    // The AsyncFileDownloader responsible for downloading a set of data files from Poly.
    private AsyncFileDownloader mFileDownloader;

    // When we're finished downloading the asset files, we flip getActivity() boolean to true to
    // indicate to the GL thread that it can import and load the model.
    private volatile boolean mReadyToImport;

    // Attributions text to display for the object (title and author).
    private String mAttributionText = "";

    // Have we already shown the attribution toast?
    private boolean mShowedAttributionToast;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.frag_ar,container,false);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        surfaceView = view.findViewById(R.id.surfaceview);
        displayRotationHelper = new DisplayRotationHelper(v.getContext());

        // Set up tap listener.
        tapHelper = new TapHelper( v.getContext());
        surfaceView.setOnTouchListener(tapHelper);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer((GLSurfaceView.Renderer) getActivity());
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;

        // Create a background thread, where we will do the heavy lifting.
        mBackgroundThread = new HandlerThread("Worker");
        mBackgroundThread.start();
        mBackgroundThreadHandler = new Handler(mBackgroundThread.getLooper());

        // Request the asset from the Poly API.
        Log.d(TAG, "Requesting asset "+ ASSET_ID);
        PolyApi.GetAsset(ASSET_ID, mBackgroundThreadHandler, new AsyncHttpRequest.CompletionListener() {
            @Override
            public void onHttpRequestSuccess(byte[] responseBody) {
                // Successfully fetched asset information. getActivity() does NOT include the model's geometry,
                // it's just the metadata. Let's parse it.
                parseAsset(responseBody);
            }
            @Override
            public void onHttpRequestFailure(int statusCode, String message, Exception exception) {
                // Something went wrong with the request.
                handleRequestFailure(statusCode, message, exception);
            }
        });

    }

    public ARFragment() {
        session = new Session(v.getContext());
    }
    @Override
    public void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(getActivity(), !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
                    CameraPermissionHelper.requestCameraPermission(getActivity());
                    return;
                }

                session = new Session(getActivity());

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update getActivity() app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "getActivity() device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }

            if (message != null) {
                messageSnackbarHelper.showError(getActivity(), message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle getActivity() properly by showing a message and recreate the
            // session at the next iteration.
            messageSnackbarHelper.showError(getActivity(), "Camera not available. Please restart the app.");
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();

        messageSnackbarHelper.showMessage(getActivity(), "Searching for surfaces...");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(getActivity())) {
            Toast.makeText(getContext(), "Camera permission is needed to run getActivity() application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(getActivity())) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(getActivity());
            }
            getActivity().finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(getActivity(), hasFocus);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. getActivity() involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(getActivity());
            planeRenderer.createOnGlThread(/*context=*/ getActivity(), "models/trigrid.png");
            pointCloudRenderer.createOnGlThread(/*context=*/ getActivity());
        } catch (IOException e) {
            Log.e(TAG, "Failed to read plane texture", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // If we are ready to import the object and haven't done so yet, do it now.
        if (mReadyToImport && virtualObject == null) {
            importDownloadedObject();
        }

        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), getActivity() will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            // Handle one tap per frame.
            handleTap(frame, camera);

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            final float[] colorCorrectionRgba = new float[4];
            frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

            // Visualize tracked points.
            PointCloud pointCloud = frame.acquirePointCloud();
            pointCloudRenderer.update(pointCloud);
            pointCloudRenderer.draw(viewmtx, projmtx);

            // Application is responsible for releasing the point cloud resources after
            // using it.
            pointCloud.release();

            // Check if we detected at least one plane. If so, hide the loading message.
            if (messageSnackbarHelper.isShowing()) {
                for (Plane plane : session.getAllTrackables(Plane.class)) {
                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                        messageSnackbarHelper.hide(getActivity());
                        break;
                    }
                }
            }

            // Visualize planes.
            planeRenderer.drawPlanes(
                    session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);

            // Visualize anchors created by touch.
            float scaleFactor = 1.0f;
            for (Anchor anchor : anchors) {
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                anchor.getPose().toMatrix(anchorMatrix, 0);

                // Update and draw the model.
                if (virtualObject != null) {
                    virtualObject.updateModelMatrix(anchorMatrix, ASSET_SCALE * scaleFactor);
                    virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba);

                    // If we haven't yet showing the attribution toast, do it now.
                    if (!mShowedAttributionToast) {
                        showAttributionToast();
                    }
                }
            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    // Handle only one tap per frame, as taps are usually low frequency compared to frame rate.
    private void handleTap(Frame frame, Camera camera) {
        MotionEvent tap = tapHelper.poll();
        if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                // Check if any plane was hit, and if it was hit inside the plane polygon
                Trackable trackable = hit.getTrackable();
                // Creates an anchor if a plane or an oriented point was hit.
                if ((trackable instanceof Plane
                        && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())
                        && (PlaneRenderer.calculateDistanceToPlane(hit.getHitPose(), camera.getPose()) > 0))
                        || (trackable instanceof Point
                        && ((Point) trackable).getOrientationMode()
                        == OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
                    // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.
                    // Cap the number of objects created. getActivity() avoids overloading both the
                    // rendering system and ARCore.
                    if (anchors.size() >= 20) {
                        anchors.get(0).detach();
                        anchors.remove(0);
                    }

                    // Adding an Anchor tells ARCore that it should track getActivity() position in
                    // space. getActivity() anchor is created on the Plane to place the 3D model
                    // in the correct position relative both to the world and to the plane.
                    anchors.add(hit.createAnchor());
                    break;
                }
            }
        }
    }

    private void importDownloadedObject() {
        try {
            virtualObject = new ObjectRenderer();

            byte[] objBytes = null;
            byte[] textureBytes = null;
            for (int i = 0; i < mFileDownloader.getEntryCount(); i++) {
                AsyncFileDownloader.Entry getActivity()Entry = mFileDownloader.getEntry(i);
                if (getActivity()Entry.fileName.toLowerCase().endsWith(".obj")) {
                    objBytes = getActivity()Entry.contents;
                } else if (getActivity()Entry.fileName.toLowerCase().endsWith(".png")) {
                    textureBytes = getActivity()Entry.contents;
                }
            }

            if (objBytes == null || textureBytes == null) {
                Log.e(TAG, "Downloaded asset doesn't have OBJ data and a PNG texture.");
                return;
            }
            Log.d(TAG, "Importing OBJ.");

            virtualObject.createOnGlThread(v.getContext(), objBytes, textureBytes);
            virtualObject.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read or parse obj file");
        }
    }

    // NOTE: getActivity() runs on the background thread.
    private void parseAsset(byte[] assetData) {
        Log.d(TAG, "Got asset response (" + assetData.length + " bytes). Parsing.");
        String assetBody = new String(assetData, Charset.forName("UTF-8"));
        Log.d(TAG, assetBody);
        try {
            JSONObject response = new JSONObject(assetBody);
            String displayName = response.getString("displayName");
            String authorName = response.getString("authorName");
            Log.d(TAG, "Display name: " + displayName);
            Log.d(TAG, "Author name: " + authorName);
            mAttributionText = displayName + " by " + authorName;

            // The asset may have several formats (OBJ, GLTF, FBX, etc). We will look for the OBJ format.
            JSONArray formats = response.getJSONArray("formats");
            boolean foundObjFormat = false;
            for (int i = 0; i < formats.length(); i++) {
                JSONObject format = formats.getJSONObject(i);
                if (format.getString("formatType").equals("OBJ")) {
                    // Found the OBJ format. The format gives us the URL of the data files that we should
                    // download (which include the OBJ file, the MTL file and the textures). We will now
                    // request those files.
                    requestDataFiles(format);
                    foundObjFormat = true;
                    break;
                }
            }
            if (!foundObjFormat) {
                // If getActivity() happens, it's because the asset doesn't have a representation in the OBJ
                // format. Since getActivity() simple sample code can only parse OBJ, we can't proceed.
                // But other formats might be available, so if your client supports multiple formats,
                // you could still try a different format instead.
                Log.e(TAG, "Could not find OBJ format in asset.");
                return;
            }
        } catch (JSONException jsonException) {
            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
            jsonException.printStackTrace();
        }
    }

    // Requests the data files for the OBJ format.
    // NOTE: getActivity() runs on the background thread.
    private void requestDataFiles(JSONObject objFormat) throws JSONException {
        // objFormat has the list of data files for the OBJ format (OBJ file, MTL file, textures).
        // We will use a AsyncFileDownloader to download all those files.
        mFileDownloader = new AsyncFileDownloader();

        // The "root file" is the OBJ.
        JSONObject rootFile = objFormat.getJSONObject("root");
        mFileDownloader.add(rootFile.getString("relativePath"), rootFile.getString("url"));

        // The "resource files" are the MTL file and textures.
        JSONArray resources = objFormat.getJSONArray("resources");
        for (int i = 0; i < resources.length(); i++) {
            JSONObject resourceFile = resources.getJSONObject(i);
            String path = resourceFile.getString("relativePath");
            String url = resourceFile.getString("url");
            // For getActivity() example, we only care about OBJ and PNG files.
            if (path.toLowerCase().endsWith(".obj") || path.toLowerCase().endsWith(".png")) {
                mFileDownloader.add(path, url);
            }
        }

        // Now start downloading the data files. When getActivity() is done, the callback will call
        // processDataFiles().
        Log.d(TAG, "Starting to download data files, # files: " + mFileDownloader.getEntryCount());
        mFileDownloader.start(mBackgroundThreadHandler, new AsyncFileDownloader.CompletionListener() {
            @Override
            public void onPolyDownloadFinished(AsyncFileDownloader downloader) {
                if (downloader.isError()) {
                    Log.e(TAG, "Failed to download data files for asset.");
                    return;
                }
                // Signal to the GL thread that download is complete, so it can go ahead and
                // import the model.
                Log.d(TAG, "Download complete, ready to import model.");
                mReadyToImport = true;
            }
        });
    }

    // NOTE: getActivity() runs on the background thread.
    private void handleRequestFailure(int statusCode, String message, Exception exception) {
        // NOTE: because getActivity() is a simple sample, we don't have any real error handling logic
        // other than just printing the error. In an actual app, getActivity() is where you would take
        // appropriate action according to your app's use case. You could, for example, surface
        // the error to the user or retry the request later.
        Log.e(TAG, "Request failed. Status code " + statusCode + ", message: " + message +
                ((exception != null) ? ", exception: " + exception : ""));
        if (exception != null) exception.printStackTrace();
    }

    private void showAttributionToast() {
        mShowedAttributionToast = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // NOTE: we use a toast for showing attribution in getActivity() sample because it's the
                // simplest way to accomplish getActivity(). In your app, you are not required to use
                // a toast. You can display getActivity() attribution information in the most appropriate
                // way for your application.
                Toast.makeText(v.getContext(), mAttributionText, Toast.LENGTH_LONG).show();
            }
        });
    }
}


