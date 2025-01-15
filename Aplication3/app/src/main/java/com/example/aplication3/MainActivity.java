package com.example.aplication3;

import static android.webkit.URLUtil.isValidUrl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private EditText txtinput;
    private ImageView mic;
    private WebView webview;
    private Button btnback;
    private Button btnforward;
    private SpeechRecognizer speechRecognizer;
    public static final Integer RECORD_AUDIO_REQUEST = 1;
    private int count = 0;
    private String recognizedText = "";

    private static final float RMS_THRESHOLD = 0.01f;
    private static final long SILENCE_DURATION = 1000;
    private long lastSpeechTime = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtinput = findViewById(R.id.txtinput);
        mic = findViewById(R.id.mic);
        webview = findViewById(R.id.webview);
        btnback = findViewById(R.id.btnback);
        btnforward = findViewById(R.id.btnforward);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("https://www.google.com");

        btnback.setOnClickListener(v -> {
            if (webview.canGoBack()) {
                webview.goBack();
            }
        });

        btnforward.setOnClickListener(v -> {
            if (webview.canGoForward()) {
                webview.goForward();
            }
        });

        txtinput.setOnEditorActionListener((v, actionId, event) -> {
            String url = txtinput.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(MainActivity.this, "Please enter a valid URL", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            webview.loadUrl(url);
            btnforward.setEnabled(true);

            return true;
        });

        txtinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String url = txtinput.getText().toString().trim();
                if (isValidUrl(url)) {

                    webview.loadUrl(url);
                    startListening();
                }
            }

            private boolean isValidUrl(String url) {
                if (url == null || url.isEmpty()) {
                    return false;
                }

                if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                    if (url.startsWith("www.")) {
                        url = "https://"+"www." + url;
                    } else {
                        url = "https://"+"www." + url;
                    }
                }
                return true;
            }

        });
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d("Tag", "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("Tag", "onBeginningOfSpeech");
                txtinput.setText("");
                txtinput.setVisibility(View.VISIBLE);
                mic.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d("Tag", "onRmsChanged");
                if (rmsdB < RMS_THRESHOLD) {
                    if (System.currentTimeMillis() - lastSpeechTime > SILENCE_DURATION) {
                        onEndOfSpeech();
                    }
                } else {
                    lastSpeechTime = System.currentTimeMillis();
                }
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d("Tag", "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("Tag", "onEndOfSpeech");
                txtinput.setVisibility(View.VISIBLE);
                mic.setVisibility(View.INVISIBLE);
                displayRecognizedText();

                new Handler().postDelayed(() -> {
                    txtinput.setText("");
                    mic.setVisibility(View.VISIBLE);
                    txtinput.setVisibility(View.VISIBLE);
                    startListening();
                }, 5000);
            }

            @Override
            public void onError(int error) {
                Log.d("Tag", "onError");
                onBeginningOfSpeech();
                mic.setVisibility(View.VISIBLE);
                txtinput.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResults(Bundle results) {
                Log.d("Tag", "onResults called");

                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    recognizedText = data.get(0);
                    runOnUiThread(() -> txtinput.setText(recognizedText));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    recognizedText = data.get(0);
                    runOnUiThread(() -> txtinput.setText(recognizedText));
                }
                Log.d("Tag", "onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d("Tag", "onEvent");
            }
        });

        mic.setOnClickListener(v -> {
            if (count == 0) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST);
                } else {
                    if (isNetworkConnected()) {
                        speechRecognizer.startListening(speechIntent);
                        mic.setVisibility(View.INVISIBLE);
                        txtinput.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, "Turn on internet or Wi-Fi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            count++;
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mic.setVisibility(View.INVISIBLE);
        speechRecognizer.startListening(intent);
    }

    private void displayRecognizedText() {
        if (recognizedText != null && !recognizedText.isEmpty()) {
            txtinput.setText(recognizedText);
        }
    }
}
