package com.example.application2.speech;

import androidx.annotation.NonNull;

public interface SpeechRecognitionListener {

    void onReceiveSpeechRecognitionResult(@NonNull String speechResult);

    void onReceiveError(@NonNull String error);
}
