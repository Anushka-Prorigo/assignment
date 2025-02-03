package com.example.application2.speech;

import androidx.annotation.Nullable;

public interface ISpeechRecognizer {

    void startRecognition();

    void stopRecognition();

    void setRecognitionListener(@Nullable SpeechRecognitionListener listener);
}
