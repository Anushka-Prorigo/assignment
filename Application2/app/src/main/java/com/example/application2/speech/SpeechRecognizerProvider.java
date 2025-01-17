package com.example.application2.speech;

import android.content.Context;

import androidx.annotation.NonNull;

public final class SpeechRecognizerProvider {
    public static ISpeechRecognizer getRecognizer(@NonNull RecognizerType type,
                                                  @NonNull Context context) {
        switch (type) {
            case Native:
                return new NativeSpeechRecognizer(context);
        }
        return null;
    }
}
