package com.example.application2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
{

    private EditText txtusername,txtpassword;
    private Button btnlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtusername=findViewById(R.id.txtusername);
        txtpassword=findViewById(R.id.txtpassword);
        btnlogin=findViewById(R.id.btnlogin);

       btnlogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {

               String username = txtusername.getText().toString();
               String password = txtpassword.getText().toString();


               Intent intent = new Intent(MainActivity.this,WebViewActivity.class);
               intent.putExtra("username" , username);
               intent.putExtra("password" ,password);
               startActivity(intent);
           }
       });

    }
}