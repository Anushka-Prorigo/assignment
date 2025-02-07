package com.example.application2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ShowEnteredData extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> datalist;
    private ArrayAdapter<String> adapter;
    private Button btn_Back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_entered_data);
        listView = findViewById(R.id.listView);
        btn_Back = findViewById(R.id.btn_Back);
        datalist = getIntent().getStringArrayListExtra("DataList");
        if (datalist != null) {
            Log.d("ShowEnteredData", "Received Data: " + datalist);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, datalist);
            listView.setAdapter(adapter);
        } else {
            Log.d("ShowEnteredData", "No data received");
        }
        btn_Back.setOnClickListener(View ->{
            Intent intent = new Intent(ShowEnteredData.this, DynamicLayout.class);
            startActivity(intent);
        });
    }
}