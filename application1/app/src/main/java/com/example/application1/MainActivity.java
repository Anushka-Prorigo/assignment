package com.example.application1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    private WebView webview;
    private EditText url_input;
    private Button btnback;
    private Button btnforward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        webview = findViewById(R.id.webview);
        url_input = findViewById(R.id.url_input);
        btnback = findViewById(R.id.btnback);
        btnforward = findViewById(R.id.btnforward);

        webview.getSettings().setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                updateNavigationButtons();
            }
        });


        webview.loadUrl("https://www.google.com");

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                }
                updateNavigationButtons();
            }
        });

        btnforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoForward()) {
                    webview.goForward();
                }
                updateNavigationButtons();
            }
        });

        url_input.setOnEditorActionListener((v, actionId, event) -> {
            String url = url_input.getText().toString().trim();

            if (TextUtils.isEmpty(url)) {
                Toast.makeText(MainActivity.this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (!url.startsWith("http://") && !url.startsWith("https://"))
            {
                url = "https://" + url;
            }

            webview.loadUrl(url);

            btnforward.setEnabled(true);

            updateNavigationButtons();

            return true;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webview.canGoBack()) {
                    webview.goBack();
                    updateNavigationButtons();
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        updateNavigationButtons();
    }

    private void updateNavigationButtons()
    {
        if (webview.canGoBack())
        {
            btnback.setEnabled(true);
        } else
        {
            btnback.setEnabled(false);
        }

        if (webview.canGoForward())
        {
            btnforward.setEnabled(true);
        } else
        {
            btnforward.setEnabled(false);
        }
    }
}
