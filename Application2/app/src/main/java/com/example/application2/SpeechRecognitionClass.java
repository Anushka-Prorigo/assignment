package com.example.application2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionBarPolicy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.application2.speech.NativeSpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;
import com.example.application2.speech.SpeechRecognizerProvider;
import com.example.application2.voice.IVoiceRecognizer;
import com.example.application2.voice.NativeTextToVoiceRecognizer;

import java.lang.ref.WeakReference;

public class SpeechRecognitionClass extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private Button next_button, startover_button;
    private IVoiceRecognizer iVoiceRecognizer;
    private SpeechRecognizerProvider speechRecognizerProvider;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private WeakReference<SpeechRecognitionListener> listenerRef;
    private NativeSpeechRecognizer nativeSpeechRecognizer;
    private NativeTextToVoiceRecognizer nativeTextToVoiceRecognizer ;
    private LinearLayout linearLayout;
    private Context context;
    private SpeechRecognitionClass speechRecognitionClass;
    String previouscmd = "";
    public SpeechRecognitionClass(Context context) {
        this.context = context;
    }
    public void setListener(SpeechRecognitionListener listener) {
        this.listener = listener;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public SpeechRecognitionListener getListener() {
        Log.d("Tag",""+listenerRef);
        return listener;
    }
    private SpeechRecognitionListener listener = new SpeechRecognitionListener() {
        @Override
        public void onReceiveSpeechRecognitionResult(@NonNull String speechResult) {
            Log.d("Tag", "recognized text: " + speechResult);

            String speechText = speechResult;
            previouscmd=speechText;
            if (nativeTextToVoiceRecognizer == null) {
                nativeTextToVoiceRecognizer = new NativeTextToVoiceRecognizer(getContext());
            }

            if (nativeTextToVoiceRecognizer != null && !speechText.equalsIgnoreCase("yes") && !speechText.equalsIgnoreCase("no")) {
                previouscmd = speechText;
                nativeTextToVoiceRecognizer.StartTextRecognition("you entered"+speechText);
            } else if (speechText != null && !speechText.isEmpty()) {
                if (speechText.equalsIgnoreCase("yes")) {
                    Log.d("SpeechRecognition", "previouscmd: " + previouscmd);
                    Intent intent = new Intent(getContext(), DynamicLayout.class);
                    intent.putExtra("speechResult", previouscmd);
                    getContext().startActivity(intent);
            } else if (speechText.equalsIgnoreCase("no")) {
                    nativeSpeechRecognizer.startRecognition();
                }
            } else {
                textToSpeech.speak("No input detected. Please try again.", TextToSpeech.QUEUE_FLUSH, null, null);
                initializeSpeechRecognizer();
            }

        }
        @Override
        public void onReceiveError(@NonNull Error error) {
            Log.e("SpeechRecognitionClass", "Error in recognition: " + error.getMessage());
        }
    };
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SpeechRecognition", "Permission not granted. Requesting...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            initializeSpeechRecognizer();
            Log.d("SpeechRecognition", "Permission granted. Initializing Speech Recognizer...");
        }
    }
    public void initializeSpeechRecognizer() {
        if (context == null) {
            Log.e("NativeTextToVoiceRecognizer", "Context is null, cannot initialize SpeechRecognizer.");
            nativeSpeechRecognizer = new NativeSpeechRecognizer(getApplicationContext());
            return;
        }

        Context appContext = context.getApplicationContext();
        if (!SpeechRecognizer.isRecognitionAvailable(appContext)) {
            Log.e("NativeTextToVoiceRecognizer", "Speech recognition is not available.");
            return;
        }
        if (nativeSpeechRecognizer == null) {
            nativeSpeechRecognizer = new NativeSpeechRecognizer(appContext);
        }
        if (nativeTextToVoiceRecognizer == null) {
            nativeTextToVoiceRecognizer = new NativeTextToVoiceRecognizer(appContext);
        }
        nativeSpeechRecognizer.setRecognitionListener(listener);
        nativeSpeechRecognizer.startRecognition();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer();
            } else {
                Toast.makeText(this, "Audio permission required for speech recognition", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
