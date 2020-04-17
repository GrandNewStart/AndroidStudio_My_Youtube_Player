package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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

public class ChangeVideoActivity extends AppCompatActivity {
    private EditText et_url;
    private Button btn_update, btn_cancel;
    private String title, thumbnail, date, uploader;
    private final String API_KEY = "AIzaSyDe_8mu0ywtgQ8zSkNQ2-bc5sOZ_ed2-DY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_screen);

        Intent intent = getIntent();
        final int ID = intent.getIntExtra("ID TO UPDATE", -1);

        et_url = (EditText) findViewById(R.id.et_url);
        btn_update = (Button) findViewById(R.id.btn_enter);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = et_url.getText().toString();

                // Parsing the URL in search of the Video ID
                final String videoID = parseURL(input);
                if (videoID.equals("")) {
                    Toast.makeText(ChangeVideoActivity.this, "형식에 맞지 않는 URL입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve JSON object and parse it
                String url = "https://www.googleapis.com/youtube/v3/videos" +
                        "?id=" + videoID +
                        "&key=" + API_KEY +
                        "&part=snippet" +
                        "&fields=items(snippet(title, thumbnails(default(url)),publishedAt,channelTitle))";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
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

                            if (jsonObject.toString().equals(null)) {
                                Toast.makeText(ChangeVideoActivity.this, "잘못된 URL입니다.", Toast.LENGTH_SHORT);
                                return;
                            }

                            date = jsonObject.getString("publishedAt").substring(0, 10);
                            title = jsonObject.getString("title");
                            uploader = jsonObject.getString("channelTitle");
                            thumbnail = jsonObject.getJSONObject("thumbnails").getJSONObject("default").getString("url");

                            // Return data
                            Intent intent = new Intent(ChangeVideoActivity.this, MainActivity.class);
                            intent.putExtra("ID TO UPDATE", ID);
                            intent.putExtra("THUMBNAIL TO UPDATE", thumbnail);
                            intent.putExtra("TITLE TO UPDATE", title);
                            intent.putExtra("UPLOADER TO UPDATE", uploader);
                            intent.putExtra("DATE TO UPDATE", date);
                            intent.putExtra("VIDEO ID TO UPDATE", videoID);
                            setResult(2, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }
        });
    }

    private String parseURL(String url) {
        String videoID = "";
        int count = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                count++;
                continue;
            }
            if (count == 3) {
                videoID = url.substring(i);
                break;
            }
        }

        return videoID;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }
}
