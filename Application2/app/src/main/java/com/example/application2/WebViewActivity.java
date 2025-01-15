package com.example.application2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class WebViewActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private WebView webview;
    private boolean isSignInPageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initializeSpeechRecognizer();
        }

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
            }
        });

        Log.e("TAG", "gmail display");
        webview.loadUrl("https://mail.google.com");
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizer.setRecognitionListener(new RecognitionListener()
        {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("SpeechRecognition", "Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("SpeechRecognition", "Speech started");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d("SpeechRecognition", "RMS changed: " + rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("SpeechRecognition", "Buffer received");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("SpeechRecognition", "Speech ended");
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognition", "Error: " + error);
            }

            @Override
            public void onResults(Bundle results) {
                Log.d("SpeechRecognition", "Results received");

                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    String recognizedText = data.get(0);
                    Log.d("SpeechRecognition", "Recognized Text: " + recognizedText);
                    if (recognizedText.toLowerCase().contains("sign")) {
                        Log.d("SpeechRecognition", "Redirecting to sign-in page...");
                        webview.loadUrl("https://accounts.google.com/ServiceLogin");
                    } else {
                        Toast.makeText(WebViewActivity.this, "No relevant command found: " + recognizedText, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d("SpeechRecognition", "Partial results received");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("SpeechRecognition", "Event received");
            }
        });

        Log.d("SpeechRecognition", "Starting speech recognition...");
        speechRecognizer.startListening(speechIntent);
    }

    private void startListeningForSpeech() {
        if (speechRecognizer != null) {
            final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            Log.d("SpeechRecognition", "Starting speech recognition...");
            speechRecognizer.startListening(speechIntent);
        } else {
            Log.e("SpeechRecognition", "SpeechRecognizer is not initialized!");
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
            speechRecognizer.destroy();
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
