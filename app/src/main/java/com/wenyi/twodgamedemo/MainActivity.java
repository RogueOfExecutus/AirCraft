package com.wenyi.twodgamedemo;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private GameSurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new GameSurfaceView(this);
        surfaceView.setZOrderOnTop(true);
        setContentView(surfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        surfaceView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        surfaceView.onDestroy();
    }
}
