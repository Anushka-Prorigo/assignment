package com.example.application2.voice;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.application2.SpeechRecognitionClass;
import com.example.application2.speech.NativeSpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class NativeTextToVoiceRecognizer implements IVoiceRecognizer {
    private TextToSpeech textToSpeech;
    private WeakReference<Context> contextRef;
    private SpeechRecognitionClass speechRecognitionClass;

    private NativeSpeechRecognizer nativeSpeechRecognizer;
    private SpeechRecognitionListener listener;

    private boolean isTTSInitialized = false;

    public NativeTextToVoiceRecognizer(SpeechRecognitionClass speechRecognitionClass) {
        this.speechRecognitionClass = speechRecognitionClass;
        this.contextRef = new WeakReference<>(speechRecognitionClass.getApplicationContext());
        nativeSpeechRecognizer = new NativeSpeechRecognizer(speechRecognitionClass.getApplicationContext());
    }
    public NativeTextToVoiceRecognizer(@NonNull Context context) {

        contextRef = new WeakReference<>(context);
        this.speechRecognitionClass = new SpeechRecognitionClass(context);
        nativeSpeechRecognizer = new NativeSpeechRecognizer(context);
    }
    @Override
    public void StartTextRecognition(String recognizedText) {
        Context context = contextRef.get();
        if (context == null) {
            Log.e("NativeTextToVoiceRecognizer", "Context is null");
            return;
        }
        initializeTextToSpeech(recognizedText);
    }
    public void initializeTextToSpeech(String recognizedText) {
        Context context = contextRef.get();
        if (context == null) {
            Log.e("NativeTextToVoiceRecognizer", "Context is null");
            return;
        }

        if (recognizedText == null || recognizedText.isEmpty()) {
            Log.e("NativeTextToVoiceRecognizer", "No recognized text to speak.");
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
                    isTTSInitialized = true;
                    speakText(recognizedText);
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

                        if (nativeSpeechRecognizer != null) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                SpeechRecognitionListener listener = speechRecognitionClass.getListener();
                                nativeSpeechRecognizer.setRecognitionListener(listener);
                                nativeSpeechRecognizer.startRecognition();
                                Log.d("speechrecognition", "Speech recognition started after TTS.");
                            });
                        } else {
                            Log.e("speechrecognition", "nativeSpeechRecognizer is null.");
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.e("texttospeech", "Error occurred while speaking.");
                        stopTextToSpeech();
                    }
                });
                speakText(recognizedText);
            } else {
                Log.e("NativeTextToVoiceRecognizer", "TextToSpeech initialization failed with status: " + status);
            }
        });
    }
    private void speakText(String recognizedText) {
        if (textToSpeech != null) {
            String utteranceId = String.valueOf(System.currentTimeMillis());
            textToSpeech.speak(recognizedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
    }
    @Override
    public void StopTextRecognition() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            Log.d("NativeTextToVoiceRecognizer", "TextToSpeech stopped.");
        } else {
            Log.e("NativeTextToVoiceRecognizer", "TextToSpeech is already null.");
        }
    }
    private void stopTextToSpeech() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            Log.d("NativeTextToVoiceRecognizer", "TextToSpeech stopped and resources released.");
        }
    }

}
