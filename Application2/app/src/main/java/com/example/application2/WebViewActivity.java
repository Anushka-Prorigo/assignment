package com.example.application2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.application2.speech.ISpeechRecognizer;
import com.example.application2.speech.RecognizerType;
import com.example.application2.speech.SpeechRecognitionListener;
import com.example.application2.speech.SpeechRecognizerProvider;

import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private ISpeechRecognizer speechRecognizer;
    private WebView webview;
    private TextView txtinput;
    private String previouscmd = "";

    private boolean isSignInPageLoaded = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initializeSpeechRecognizer();
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        txtinput = findViewById(R.id.txtinput);
        webview = findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("TAG", "after gmail display: " + url);

                if (!isSignInPageLoaded) {
                    isSignInPageLoaded = true;
                    Log.e("TAG", "Loading sign-in page");
                    initializeSpeechRecognizer();
                }

                if (isSignInPageLoaded) {
                    Log.e("TAG", "Autofilling credentials");
                    autofillCredentials();
                }

                initializeSpeechRecognizer();
            }
        });

        Log.e("TAG", "gmail display");
        webview.loadUrl("https://mail.google.com");
    }

    private void initializeSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizerProvider.getRecognizer(RecognizerType.Native, this);
            if (speechRecognizer != null) {
                speechRecognizer.setRecognitionListener(new SpeechRecognitionListener() {
                    @Override
                    public void onReceiveSpeechRecognitionResult(@NonNull final String speechResult) {
                        String recognizedText = speechResult;
                        Log.d("SpeechRecognition", "Recognized Text: " + recognizedText);
                        if (recognizedText.contains("sign")) {
                            Log.d("SpeechRecognition", "Redirecting to sign-in page...");
                            webview.loadUrl("https://accounts.google.com/ServiceLogin");
                        } else if (recognizedText.contains("back")) {
                            if (webview.canGoBack()) {
                                webview.goBack();
                                Log.e("TAG", "redirecting previous page");
                            }
                        } else if (recognizedText.contains("next")) {
                            if (webview != null && webview.canGoForward()) {
                                webview.goForward();
                                Log.e("TAG", "redirecting next page");
                            }
                        } else if (previouscmd != null && previouscmd.contains("enter URL")) {
                            txtinput.requestFocus();
                            if (recognizedText != null && !recognizedText.isEmpty()) {
                                String url = recognizedText;
                                txtinput.setText(url);
                                Log.e("recognised url", recognizedText);
                                previouscmd = null;

                                txtinput.setVisibility(View.VISIBLE);
                                if (TextUtils.isEmpty(url)) {
                                    Toast.makeText(WebViewActivity.this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    url = "https://www." + url;
                                }
                                webview.loadUrl(url);
                            }
                            txtinput.setText("");
                        } else if (recognizedText.contains("enter URL")) {
                            previouscmd = recognizedText;
                            txtinput.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(WebViewActivity.this, "No relevant command found " + recognizedText, Toast.LENGTH_SHORT).show();
                            Log.d("Tag", "no relevant command");
                            initializeSpeechRecognizer();
                        }
                    }

                    @Override
                    public void onReceiveError(@NonNull Error error) {

                    }
                });
                speechRecognizer.startRecognition();
            }
        }
    }

    private void autofillCredentials() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.stopRecognition();
            speechRecognizer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeSpeechRecognizer();
        } else {
            Toast.makeText(this, "Audio permission required for speech recognition", Toast.LENGTH_SHORT).show();
        }
    }
}
