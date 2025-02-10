package com.example.application2.voice;

import androidx.annotation.NonNull;

public interface ITextToSpeechListener {
    void onFinishedSpeaking(@NonNull String speakText);
    void onReceiveError(@NonNull Error error);
    void onTTSEngineReady();
}
