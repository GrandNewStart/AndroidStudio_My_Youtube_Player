package com.example.videoplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;
    Button play, quit;
    int time, volume;
    String video;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("LOG ", "onCreate");

        videoView = (VideoView) findViewById(R.id.videoView);
        play = (Button) findViewById(R.id.button);
        quit = (Button) findViewById(R.id.button_quit);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2;

        video = "https://www.radiantmediaplayer.com/media/bbb-360p.mp4";
        Uri uri = Uri.parse(video);
        videoView.setVideoURI(uri);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()){
                    videoView.pause();
                }
                else{
                    videoView.start();
                }
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopUp.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("LOG ", "onStart");
        Toast.makeText(this, video + " is loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LOG ", "onResume");
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LOG ", "onPause");
        time=videoView.getCurrentPosition();
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("LOG ", "onRestart");
        videoView.seekTo(time);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LOG ", "onStop");
        videoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LOG ", "onDestroy");
        Toast.makeText(this, "App terminated", Toast.LENGTH_SHORT).show();
    }
}
