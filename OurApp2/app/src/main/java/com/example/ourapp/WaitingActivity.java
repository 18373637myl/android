package com.example.ourapp;
import org.bytedeco.javacv.*;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

public class WaitingActivity extends AppCompatActivity {

    static String uri = "android.resource://" + "com.example.ourapp" + "/" + R.raw.test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        VideoController.init(
                uri,
                "android.resource://com.example.ourapp"+R.raw.test,
                "android.resource://"+getPackageName()+"/res/raw/center.mp4",
                "android.resource://"+getPackageName()+"/res/raw/result.mp4");
        try{
            VideoController.fetchPic();
            System.out.println(VideoController.bmplist.get(0));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}