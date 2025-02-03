package com.example.application2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application2.speech.ISpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;
import com.example.application2.voice.NativeTextToVoiceRecognizer;

import java.lang.ref.WeakReference;

public class DataCollectionPresenter implements ISpeechRecognizer, SpeechRecognitionListener {

    @Override
    public void startRecognition() {

    }

    @Override
    public void stopRecognition() {

    }

    @Override
    public void setRecognitionListener(@Nullable SpeechRecognitionListener listener) {

    }

    @Override
    public void onReceiveSpeechRecognitionResult(@NonNull String speechResult) {

    }

    @Override
    public void onReceiveError(@NonNull Error error) {

    }
}
