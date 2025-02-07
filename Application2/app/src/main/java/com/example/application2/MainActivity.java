package com.example.application2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.application2.speech.NativeSpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;
import com.example.application2.voice.NativeTextToVoiceRecognizer;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity  {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private WeakReference<Context> contextRef;
    private NativeTextToVoiceRecognizer nativeTextToVoiceRecognizer;
    private SpeechRecognitionClass speechRecognitionClass;
    private TextToSpeech textToSpeech;
    private NativeSpeechRecognizer nativeSpeechRecognizer ;
    String recognizedText = "";
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private final SpeechRecognitionListener listener = new SpeechRecognitionListener() {
        @Override
        public void onReceiveSpeechRecognitionResult(@NonNull String speechResult) {
            //Check if speechResult is "Start" then launch next activity
            if (speechResult.equalsIgnoreCase("start")) {
                    Intent intent = new Intent(MainActivity.this, DynamicLayout.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
            }
        }
        @Override
        public void onReceiveError(@NonNull String error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        nativeSpeechRecognizer.setRecognitionListener(listener);
        nativeSpeechRecognizer.startRecognition();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeSpeechRecognizer();
        } else {
            initializeSpeechRecognizer();
              Toast.makeText(this, "Audio permission required for speech recognition", Toast.LENGTH_SHORT).show();
        }
    }
}
