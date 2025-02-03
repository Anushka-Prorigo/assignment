package com.example.application2.voice;

import androidx.annotation.NonNull;

public interface IVoiceRecognitionListener {
    void onReceiveTextToSpeechRecognitionResult(@NonNull String textResult);
    void onReceiveError(@NonNull Error error);
}
