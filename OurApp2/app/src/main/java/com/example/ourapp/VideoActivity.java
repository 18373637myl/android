package com.example.ourapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private Button playBtn, stopBtn, confirmBtn;
    MediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.test;
        mVideoView = new VideoView(this);
        mVideoView = (VideoView) findViewById(R.id.video);
        mMediaController = new MediaController(this);
        playBtn = (Button) findViewById(R.id.playbutton);
        stopBtn = (Button) findViewById(R.id.stopbutton);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.setVideoURI(Uri.parse(uri));
                mMediaController.setMediaPlayer(mVideoView);
                mVideoView.setMediaController(mMediaController);
                mVideoView.start();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.setVideoURI(Uri.parse(uri));
                mMediaController.setMediaPlayer(mVideoView);
                mVideoView.setMediaController(mMediaController);
                mVideoView.stopPlayback();
            }
        });

        confirmBtn = findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoActivity.this,WaitingActivity.class);
                startActivity(intent);
            }
        });

    }
}