package com.example.application2.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NativeSpeechRecognizer implements ISpeechRecognizer {
    private WeakReference<SpeechRecognitionListener> listenerRef;
    private WeakReference<Context> contextRef;

    private SpeechRecognizer speechRecognizer;

    public NativeSpeechRecognizer(@NonNull Context context) {
        contextRef = new WeakReference<>(context);
    }
    @Override
    public void startRecognition() {
        Context lContext = contextRef.get();
        if (lContext != null) {
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(lContext);

                final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int error) {

                    }

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (data == null || data.isEmpty())
                        {
                            listenerRef.get().onReceiveError(new Error(""));
                            return;
                        }

                        String recognizedText = data.get(0);
                        if (recognizedText == null || recognizedText.isEmpty()) {
                            listenerRef.get().onReceiveError(new Error(""));
                            return;
                        }

                        listenerRef.get().onReceiveSpeechRecognitionResult(recognizedText);
                        speechRecognizer.startListening(speechIntent);
                    }

                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                });

                speechRecognizer.startListening(speechIntent);
            }
        }
    }

    @Override
    public void stopRecognition() {
        speechRecognizer.stopListening();
        speechRecognizer.destroy();
        speechRecognizer = null;
    }

    @Override
    public void setRecognitionListener(@Nullable SpeechRecognitionListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }
}
