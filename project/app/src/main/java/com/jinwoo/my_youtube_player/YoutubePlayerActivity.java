package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YoutubePlayerActivity extends YouTubeBaseActivity {
    private String videoID;
    private YouTubePlayerView ytpv_player;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private TextView tv_description, tv_tags;
    private final String API_KEY = "AIzaSyDe_8mu0ywtgQ8zSkNQ2-bc5sOZ_ed2-DY";
    private String description, tags;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.youtube_player);

        Intent intent = getIntent();
        videoID = intent.getStringExtra("URL");

        ytpv_player = (YouTubePlayerView) findViewById(R.id.ytpv_youtube_player);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_tags = (TextView) findViewById(R.id.tv_tags);

        tv_description.setMovementMethod(new ScrollingMovementMethod());
        tv_tags.setMovementMethod(new ScrollingMovementMethod());

        String url = "https://www.googleapis.com/youtube/v3/videos" +
                "?id=" + videoID +
                "&key=" + API_KEY +
                "&part=snippet" +
                "&fields=items(snippet(description,tags))";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(YoutubePlayerActivity.this, "Failed to retrieve descriptions", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject jsonObject = null;
                JSONArray jsonArray = null;
                try {
                    jsonObject = new JSONObject(response.body().string())
                            .getJSONArray("items")
                            .getJSONObject(0)
                            .getJSONObject("snippet");

                    description = jsonObject.getString("description");

                    tags = "#";
                    jsonArray = jsonObject.getJSONArray("tags");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        tags += jsonArray.get(i).toString();
                        if (i + 1 == jsonArray.length()) continue;
                        tags += " #";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_description.setText(description);
                            tv_tags.setText(tags);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
