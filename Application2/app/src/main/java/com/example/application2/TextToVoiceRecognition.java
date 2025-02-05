package com.example.application2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.application2.speech.ISpeechRecognizer;
import com.example.application2.speech.RecognizerType;
import com.example.application2.speech.SpeechRecognizerProvider;

import java.util.Locale;

public class TextToVoiceRecognition extends AppCompatActivity {
    private  TextToSpeech textToSpeech;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private ISpeechRecognizer recognizer = SpeechRecognizerProvider.getRecognizer(RecognizerType.Native,this);

    public TextToVoiceRecognition(SpeechRecognitionClass speechRecognitionClass) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void initializeVoiceCommand() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d("texttospeech", "TTS started speaking.");
            }

            @Override
            public void onDone(String utteranceId) {

                Log.d("texttospeech", "TTS finished speaking.");
            }

            @Override
            public void onError(String utteranceId) {
                Log.e("texttospeech", "Error occurred while speaking.");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.stop();
        textToSpeech.shutdown();
    }

}