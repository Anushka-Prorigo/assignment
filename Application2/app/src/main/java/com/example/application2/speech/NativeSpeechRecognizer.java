package com.example.application2.speech;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import com.example.application2.DynamicLayout;
import com.example.application2.SpeechRecognitionClass;
import com.example.application2.voice.NativeTextToVoiceRecognizer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
public class NativeSpeechRecognizer implements ISpeechRecognizer {
    private WeakReference<SpeechRecognitionListener> listenerRef;
    public SpeechRecognizer speechRecognizer ;
    private WeakReference<Context> contextRef;
    private NativeTextToVoiceRecognizer nativeTextToVoiceRecognizer;
    private SpeechRecognitionClass speechRecognitionClass ;
    private NativeSpeechRecognizer nativeSpeechRecognize;
    private TextToSpeech textToSpeech;
    String recognizedText = "";
    private DynamicLayout dynamicLayout;
    private long lastInputTime;
    public NativeSpeechRecognizer(Context context) {
        if (context != null) {
            this.contextRef = new WeakReference<>(context);
        } else {
            Log.e("NativeSpeechRecognizer", "Received null context.");
        }
    }
    public void startRecognition() {
        Context lContext = contextRef.get();
        if (lContext != null && speechRecognizer == null) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.postDelayed((new Runnable() {
                @Override
                public void run() {
                    if (speechRecognizer == null) {
                        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(lContext);
                        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


                        SpeechRecognitionListener listener = listenerRef != null ? listenerRef.get() : null;
                        if (listener == null) {
                            Log.e("NativeSpeechRecognizer", "Listener is null when starting recognition.");
                        } else {
                            Log.d("NativeSpeechRecognizer", "Listener is not null when starting recognition.");
                        }

                        final Handler timeoutHandler = new Handler();
                        final Runnable timeoutRunnable = new Runnable() {
                            @Override
                            public void run() {
                                Log.e("NativeSpeechRecognizer", "Speech recognition timed out.");
                                if (listener != null) {
                                    listener.onReceiveError("Speech recognition timed out.");
                                }
                            }
                        };

                        speechRecognizer.setRecognitionListener(new RecognitionListener() {
                            @Override
                            public void onReadyForSpeech(Bundle params) {
                                Log.d("NativeSpeechRecognizer", "Ready for speech.");
                                timeoutHandler.postDelayed(timeoutRunnable, 10000);
                            }

                            @Override
                            public void onBeginningOfSpeech() {
                                Log.d("NativeSpeechRecognizer", "Speech has begun.");
                            }

                            @Override
                            public void onRmsChanged(float rmsdB) {
                                Log.d("NativeSpeechRecognizer", "onRmsChanged");
                                timeoutHandler.postDelayed(timeoutRunnable, 10000);
                            }
                            @Override
                            public void onBufferReceived(byte[] buffer) {
                            }

                            @Override
                            public void onEndOfSpeech() {
                                Log.d("NativeSpeechRecognizer", "Speech has ended.");
                                //timeoutHandler.removeCallbacks(timeoutRunnable);
                            }
                            @Override
                            public void onError(int error) {
                                Log.e("NativeSpeechRecognizer", "Error during speech recognition: " + error);
                                String errorMessage = "";
                                switch (error) {
                                    case SpeechRecognizer.ERROR_AUDIO:
                                        errorMessage = "Audio recording error. Please check the microphone.";
                                        break;
                                    case SpeechRecognizer.ERROR_CLIENT:
                                        errorMessage = "Client-side error.";
                                        break;
                                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                                        errorMessage = "Insufficient permissions. Please grant microphone access.";
                                        break;
                                    case SpeechRecognizer.ERROR_NETWORK:
                                        errorMessage = "Network error.";
                                        break;
                                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                                        errorMessage = "Speech recognizer is busy.";
                                        break;
                                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                                    case SpeechRecognizer.ERROR_NO_MATCH:
                                        errorMessage = "No speech detected or recognition timed out.";
                                        break;
                                    default:
                                        errorMessage = "Unknown error occurred.";
                                }
                                if (listener != null) {
                                    listener.onReceiveError(errorMessage);
                                }
                                timeoutHandler.removeCallbacks(timeoutRunnable);
                            }

                            @Override
                            public void onResults(Bundle results) {
                                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                                if (data != null && !data.isEmpty()) {
                                    String recognizedText = data.get(0);
                                    Log.d("NativeSpeechRecognizer", "Recognized text: " + recognizedText);
                                    if (listenerRef != null) {
                                        listenerRef.get().onReceiveSpeechRecognitionResult(recognizedText);
                                    } else {
                                        Log.e("Tag", "Listener is null");
                                    }
                                }
                                timeoutHandler.removeCallbacks(timeoutRunnable);
                            }

                            @Override
                            public void onPartialResults(Bundle partialResults) {
                            }

                            @Override
                            public void onEvent(int eventType, Bundle params) {
                            }
                        });

                        Log.d("NativeSpeechRecognizer", "Starting speech recognition.");
                        speechRecognizer.startListening(speechIntent);
                    }
                }
            }), 1000);
        }}
    @Override
    public void stopRecognition() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
    @Override
    public void setRecognitionListener(SpeechRecognitionListener listener) {
        this.listenerRef = new WeakReference<>(listener);
        if (listener == null) {
            Log.e("NativeSpeechRecognizer", "Listener is null when setting it.");
        } else {
            Log.d("NativeSpeechRecognizer", "Listener successfully set.");
        }
    }
}
