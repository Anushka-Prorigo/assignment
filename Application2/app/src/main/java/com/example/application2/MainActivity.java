package com.example.application2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.application2.DynamicLayout;
import com.example.application2.speech.NativeSpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;
import com.example.application2.voice.NativeTextToVoiceRecognizer;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private SpeechRecognitionClass speechRecognitionClass;
    private SpeechRecognitionListener listener;
    private NativeTextToVoiceRecognizer nativeTextToVoiceRecognizer;
    private NativeSpeechRecognizer nativeSpeechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speechRecognitionClass = new SpeechRecognitionClass(getApplicationContext());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initializeSpeechRecognizer();
        }

        ImageView mic = findViewById(R.id.mic);
        mic.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Mic clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, DynamicLayout.class);
            startActivity(intent);
        });
    }

    private void initializeSpeechRecognizer() {
        if (nativeSpeechRecognizer == null) {
            nativeSpeechRecognizer = new NativeSpeechRecognizer(getApplicationContext());
        }
        if (nativeSpeechRecognizer != null) {
            nativeSpeechRecognizer.startRecognition();
        } else {
            Log.e("MainActivity", "Failed to initialize speech recognizer.");
        }
       // nativeSpeechRecognizer.setRecognitionListener(listener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            speechRecognitionClass.initializeSpeechRecognizer();
        } else {
            initializeSpeechRecognizer();
            Toast.makeText(this, "Audio permission required for speech recognition", Toast.LENGTH_SHORT).show();
        }
    }
}
