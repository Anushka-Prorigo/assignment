package com.example.application2.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.application2.SpeechRecognitionClass;
import com.example.application2.speech.ISpeechRecognizer;
import com.example.application2.speech.NativeSpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;

import java.lang.ref.WeakReference;
import java.util.Locale;
public class NativeTextToVoiceRecognizer implements ITextToSpeech {
    private TextToSpeech textToSpeech;
    private final WeakReference<Context> contextRef;
    private WeakReference<ITextToSpeechListener> listenerRef;
    private NativeSpeechRecognizer nativeSpeechRecognizer;
    public NativeTextToVoiceRecognizer(@NonNull Context context) {
        contextRef = new WeakReference<>(context);
    }
    @Override
    public void speak(@NonNull String speakText) {
        textToSpeech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, speakText);
    }
    @Override
    public void addListener(ITextToSpeechListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }
    @Override
    public void startEngine() {
        Context context = contextRef.get();
        if (context == null) {
            Log.e("NativeTextToVoiceRecognizer", "Context is null");
            return;
        }

        if (textToSpeech != null) {
            Log.d("NativeTextToVoiceRecognizer", "Stopping and releasing old TextToSpeech instance...");
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        Log.d("NativeTextToVoiceRecognizer", "Initializing TextToSpeech...");

        Log.e("Tag","definate");
        textToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = textToSpeech.setLanguage(Locale.US);
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("NativeTextToVoiceRecognizer", "Language is not supported or missing data.");
                    return;
                }

                Log.d("NativeTextToVoiceRecognizer", "TextToSpeech initialized successfully.");
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.d("texttospeech", "TTS started speaking.");
                    }
                    @Override
                    public void onDone(String utteranceId) {
                        Log.d("texttospeech", "TTS finished speaking.");
                        listenerRef.get().onFinishedSpeaking(utteranceId);
                    }
                    @Override
                    public void onError(String utteranceId) {
                        Log.e("texttospeech", "Error occurred while speaking.");
                        listenerRef.get().onReceiveError(new Error(utteranceId));
                    }
                });
                listenerRef.get().onTTSEngineReady();
            } else {
                Log.e("NativeTextToVoiceRecognizer", "TextToSpeech initialization failed with status: " + status);
            }
        });
    }

    @Override
    public void stopEngine() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            Log.d("NativeTextToVoiceRecognizer", "TextToSpeech stopped and resources released.");
        }
    }
}
