package com.example.ourapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ChooseActivity extends AppCompatActivity {

    static public String ChosenKind;
    private Button mBtn_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        mBtn_5 = findViewById(R.id.btn_5);
        mBtn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChosenKind = mBtn_5.getText().toString();
                Toast.makeText(ChooseActivity.this,"已选择打码物品：人物",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(com.example.ourapp.ChooseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}