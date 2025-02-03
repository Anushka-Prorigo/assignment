package com.example.application2.voice;

public interface IVoiceRecognizer {
    void StartTextRecognition(String recognizedText);
    void StopTextRecognition();
}
