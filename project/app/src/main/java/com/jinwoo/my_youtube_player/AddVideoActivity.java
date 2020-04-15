package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddVideoActivity extends AppCompatActivity {
    EditText et_url;
    Button btn_enter, btn_cancel;
    String input, title, thumbnail, date, uploader, url;
    OkHttpClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_screen);

        et_url = (EditText) findViewById(R.id.et_url);
        btn_enter = (Button) findViewById(R.id.btn_enter);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        input = "";
        title = "";
        thumbnail = "";
        date = "";
        uploader = "";

        btn_enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                input = et_url.getText().toString();

                // Parsing the URL in search of the Video ID
                String videoID = "";
                int count = 0;
                for (int i = 0; i < input.length(); i++) {
                    if (input.charAt(i) == '/') {
                        count++;
                        continue;
                    }
                    if (count == 3) {
                        videoID = input.substring(i);
                        break;
                    }
                }

                // Retrieve JSON object and parse it
                url = "https://www.googleapis.com/youtube/v3/videos" +
                        "?id=" + videoID +
                        "&key=AIzaSyDe_8mu0ywtgQ8zSkNQ2-bc5sOZ_ed2-DY" +
                        "&part=snippet" +
                        "&fields=items(snippet(title, thumbnails(medium),publishedAt,channelTitle))";
                client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                final String finalVideoID = videoID;
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body().string())
                                    .getJSONArray("items")
                                    .getJSONObject(0)
                                    .getJSONObject("snippet");

                            date = jsonObject.getString("publishedAt").substring(0,10);
                            title = jsonObject.getString("title");
                            uploader = jsonObject.getString("channelTitle");
                            thumbnail = jsonObject.getJSONObject("thumbnails").getJSONObject("medium").getString("url");

                            Intent intent = new Intent();
                            intent.putExtra("VIDEOID", finalVideoID);
                            intent.putExtra("TITLE", title);
                            intent.putExtra("THUMBNAIL", thumbnail);
                            intent.putExtra("DATE", date);
                            intent.putExtra("UPLOADER", uploader);

                            setResult(1, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }
}
