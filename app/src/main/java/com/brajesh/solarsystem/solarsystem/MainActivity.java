package com.brajesh.solarsystem.solarsystem;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    OpenGLView GLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            GLView = new OpenGLView(this);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

            GLView = (OpenGLView) findViewById(R.id.glview);
            setContentView(R.layout.activity_main);
        }
}

