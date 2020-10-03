package com.amadeus.orbital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class exploreGlobe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_globe);


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_explore_globe);


            WebView myWebView = (WebView) findViewById(R.id.exploreWebView);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);


            setContentView(myWebView);
            myWebView.loadUrl("http://192.168.1.107:1234");

    }
}