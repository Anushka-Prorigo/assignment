package com.example.application2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.application2.voice.NativeTextToVoiceRecognizer;

public class DynamicLayout extends AppCompatActivity {
    private LinearLayout linearLayout;
    private NativeTextToVoiceRecognizer nativeTextToVoiceRecognizer;
    private int currentStep = 0;
    private String[] stepLabels = {
            "Enter CRA ID",
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

        linearLayout = findViewById(R.id.dynamic_layout);
        nativeTextToVoiceRecognizer = new NativeTextToVoiceRecognizer(this);

        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("currentStep", 0);
        }

        addStep(currentStep);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentStep", currentStep);
    }

    private void addStep(int stepIndex) {
        if (stepIndex >= stepLabels.length) {
            return;
        }
        TextView textView = new TextView(this);
        textView.setText(stepLabels[stepIndex]);
        linearLayout.addView(textView);

        EditText editText = new EditText(this);
        editText.setText("");
        editText.setTextSize(16);
        linearLayout.addView(editText);
        String currentText = stepLabels[stepIndex];
        nativeTextToVoiceRecognizer.StartTextRecognition(currentText);

        Intent intent = getIntent();
        if (intent != null) {
            String speechResult = intent.getStringExtra("speechResult");
            Log.d("DynamicLayout", "Received speechResult: " + speechResult);

            if (speechResult != null && !speechResult.trim().isEmpty()) {
                editText.setText(speechResult);
                intent.putExtra("speechResult", ""); // Clear the speechResult

                currentStep++;
                addStep(currentStep);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nativeTextToVoiceRecognizer != null) {
            nativeTextToVoiceRecognizer.StopTextRecognition();
        }
    }
}
