package com.example.application2.voice;

import androidx.annotation.NonNull;

public interface ITextToSpeech {
    void speak(@NonNull final String speakText);
    void addListener(ITextToSpeechListener listener);

    void startEngine();

    void stopEngine();
}
