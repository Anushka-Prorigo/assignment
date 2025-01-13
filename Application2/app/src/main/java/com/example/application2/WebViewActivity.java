package com.example.application2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    private boolean isSignInPageLoaded = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webview = findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("TAG", "after gmail display");

                if(isSignInPageLoaded==false)
                {
                    isSignInPageLoaded=true;
                    Log.e("TAG", "Loading sign-in page");
                    loadSignInPage();
                }

                if (isSignInPageLoaded==true)
                {
                    Log.e("TAG", "Autofilling credentials");
                    isSignInPageLoaded = true;
                    autofillCredentials();
                }
            }
        });

        Log.e("TAG", "gmail display");
        webview.loadUrl("https://mail.google.com");
    }

    private void loadSignInPage()
    {
        Log.e("TAG", "Navigating to sign-in page");
        webview.loadUrl("https://accounts.google.com/ServiceLogin?service=mail");
    }

    private void autofillCredentials()
    {
        String email = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        webview.evaluateJavascript("javascript:(function() {" +
                "document.querySelector('input[type=\"email\"]').value = '" + email + "';" +
                "document.querySelector('input[type=\"email\"]').dispatchEvent(new Event('input'));" +
                "document.querySelector('input[type=\"password\"]').value = '" + password + "';" +
                "document.querySelector('input[type=\"password\"]').dispatchEvent(new Event('input'));" +
                "document.querySelector('button[jsname=\"LgbsSe\"]').click();" +
                "})()", null);
    }
}
