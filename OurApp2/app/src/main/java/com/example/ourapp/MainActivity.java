package com.example.ourapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private Button mBtn_3, mBtn_1, mBtn_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn_1 = findViewById(R.id.btn_1);
        mBtn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.ourapp.MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });

        mBtn_2 = findViewById(R.id.btn_2);
        mBtn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(com.example.ourapp.MainActivity.this, ChooseActivity.class);
                startActivity(intent);
            }
        });
    }

}