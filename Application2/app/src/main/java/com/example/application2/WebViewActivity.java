package com.example.application2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    private boolean isCredentialsInjected = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webview = findViewById(R.id.webview);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                inputCredentials();

            }
        });

        webview.loadUrl("https://accounts.google.com/v3/signin/identifier?continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&ddm=1&flowEntry=ServiceLogin&flowName=GlifWebSignIn&ifkv=AeZLP98eKWHhnox5mQNsMytHCHTHOLfs71EI3g1INvL-XR0b4KhtNio16MHgX_m-z8UHpL0ZBLKZKA&rip=1&service=mail&dsh=S1058792472%3A1736512733583558");
    }
    private void inputCredentials()
    {
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        String jsCode = "javascript:(function() {" +
                "document.querySelector('input[type=email]').value = '" + username + "';" +
                "document.querySelector('input[type=password]').value = '" + password + "';" +

                "})()";

        webview.evaluateJavascript(jsCode, null);
    }
}