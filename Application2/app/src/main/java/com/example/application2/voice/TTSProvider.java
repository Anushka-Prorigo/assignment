package com.example.application2.voice;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class TTSProvider {
    static final Map<String, ITextToSpeech> ttsInstanceMap = new HashMap<>();
    public static synchronized ITextToSpeech get(@NonNull final Context context) {
        synchronized (ttsInstanceMap) {
            ITextToSpeech ttsEngine = ttsInstanceMap.get("TTS");
            if (ttsEngine == null) {
                ttsEngine = new NativeTextToVoiceRecognizer(context);
                ttsInstanceMap.put("TTS", ttsEngine);
            }
            return ttsEngine;
        }
    }
}
