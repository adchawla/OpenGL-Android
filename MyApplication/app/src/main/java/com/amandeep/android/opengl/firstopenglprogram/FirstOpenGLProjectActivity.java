package com.amandeep.android.opengl.firstopenglprogram;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class FirstOpenGLProjectActivity extends AppCompatActivity {

    // 1. Add two variables to the class
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. comment the setContentView and initialize our glSurfaceView.
        //setContentView(R.layout.activity_first_open_glproject);
        glSurfaceView = new GLSurfaceView(this);

        //3 . Check for the availability of OpenGLES 2.0 on the device
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        boolean supportES2 = configurationInfo.reqGlEsVersion >= 0x20000;

        //4. to make it work on Emulator
        if (!supportES2) {
            supportES2 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                    && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));
        }

        //5. Configure the Surface for OpenGL ES 2.0
        if (supportES2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.

            glSurfaceView.setRenderer(new FirstOpenGLProjectRenderer());
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 6. set the content view with our glSurfaceView
        setContentView(glSurfaceView);
    }

    // To make sure our app does not crash we need to stop the GL Rendering when our activity is
    // not active and restart once the activity is active again.

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }
}
