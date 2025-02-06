package com.example.application2;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.application2.speech.NativeSpeechRecognizer;
import com.example.application2.speech.SpeechRecognitionListener;
import com.example.application2.speech.SpeechRecognizerProvider;
import com.example.application2.voice.ITextToSpeechListener;
import com.example.application2.voice.NativeTextToVoiceRecognizer;

public class DynamicLayout extends AppCompatActivity {
    private LinearLayout linearLayout;
    private NativeTextToVoiceRecognizer nativeTextToVoiceRecognizer;
    private Button save_button;
    private String previousText = "";
    private SpeechRecognitionListener listenerRef;
    private NativeSpeechRecognizer nativeSpeechRecognizer = new NativeSpeechRecognizer(this);
    private EditText editText;
    private int currentStep = 0;
    public SpeechRecognizer speechRecognizer;
    public void setListener(SpeechRecognitionListener listener) {
        this.speechRecognitionListener = listener;
    }
    public SpeechRecognitionListener getListener() {
        Log.d("Tag","" +speechRecognitionListener);
        return speechRecognitionListener;
    }
    private ITextToSpeechListener listener = new ITextToSpeechListener() {
        @Override
        public void onFinishedSpeaking(@NonNull String speakText) {
            Log.d("Tag","onFinishedSpeaking");
            nativeSpeechRecognizer.setRecognitionListener(speechRecognitionListener);
            nativeSpeechRecognizer.startRecognition();
        }

        @Override
        public void onReceiveError(@NonNull Error error) {
            Log.e("Tag", "Something wen wrong ..");
        }
        @Override
        public void onTTSEngineReady() {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                String currentText = stepLabels[currentStep];
                nativeTextToVoiceRecognizer.speak(currentText);
            });
        }
    };
    private SpeechRecognitionListener speechRecognitionListener = new SpeechRecognitionListener() {
        @Override
        public void onReceiveSpeechRecognitionResult(@NonNull String speechResult) {
            String speechText = speechResult;
            if ( !speechText.equalsIgnoreCase("yes") && !speechText.equalsIgnoreCase("no")) {
                previousText = speechText;
                nativeTextToVoiceRecognizer.speak("you entered" +previousText);
            }
            else if (speechText != null && !speechText.isEmpty()) {
                if (speechText.equalsIgnoreCase("yes")) {
                    EditText editText1 = (EditText) linearLayout.findViewWithTag(currentStep);
                    Log.d("SpeechRecognition", "previouscmd: " + previousText);
                    editText1.setText(previousText);
                    currentStep++;
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(()-> {
                        addStep(currentStep);
                    });
                } else if (speechText.equalsIgnoreCase("no")) {
                    nativeSpeechRecognizer.startRecognition();
                } else if (speechText.equalsIgnoreCase("save")) {
                    Intent intent = new Intent(DynamicLayout.this,MainActivity.class);
                    startActivity(intent);
                }
            } else {
               Log.d("Tag","not getting recognized text");
            }
        };

        @Override
        public void onReceiveError(@NonNull Error error) {

        }
    };
    private String[] stepLabels = {
            "Enter C R A ID",
            "Enter CG Number",
            "Enter Line Number",
            "Enter Core Level",
            "Enter Dealer Quantity",
            "Enter Received Quantity",
            "Enter Inspection Quantity"
    };
    private String[] enteredData = new String[stepLabels.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_layout2);
        save_button=findViewById(R.id.save_button);
        linearLayout = findViewById(R.id.dynamic_layout);
        nativeTextToVoiceRecognizer = new NativeTextToVoiceRecognizer(this);

        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("currentStep", 0);
            currentStep++;
        }
        addStep(currentStep);
        nativeTextToVoiceRecognizer.addListener(listener);
        nativeTextToVoiceRecognizer.startEngine();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentStep", currentStep);
    }
    public void addStep(int stepIndex) {
             Handler mainHandler = new Handler(Looper.getMainLooper());
             mainHandler.post(()->{
            if (stepIndex >= stepLabels.length) {
                afterfillEditText();
            }
            TextView textView = new TextView(this);
            textView.setText(stepLabels[stepIndex]);
            linearLayout.addView(textView);

            EditText editText = new EditText(this);
            editText.setText("");
            editText.setTextSize(16);
            linearLayout.addView(editText);
            editText.setTag(stepIndex);
            nativeTextToVoiceRecognizer.startEngine();
             });
    }
    public void afterfillEditText()
    {
        Button save_button = new Button(this);
        save_button.setText("SAVE");
        linearLayout.addView(save_button);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nativeTextToVoiceRecognizer != null) {
            nativeTextToVoiceRecognizer.stopEngine();
        }
    }
}
