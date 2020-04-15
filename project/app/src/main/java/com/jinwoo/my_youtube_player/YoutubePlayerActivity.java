package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity {
    private String videoID;
    private YouTubePlayerView ytpv_player;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private TextView tv_description;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.youtube_player);

        Intent intent = getIntent();
        videoID = intent.getStringExtra("URL");

        ytpv_player = (YouTubePlayerView) findViewById(R.id.ytpv_youtube_player);
        tv_description = (TextView) findViewById(R.id.tv_description);

        //TODO: Get video description and map it on UI

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(videoID);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        ytpv_player.initialize(String.valueOf(R.string.API_KEY), onInitializedListener);
    }
}
