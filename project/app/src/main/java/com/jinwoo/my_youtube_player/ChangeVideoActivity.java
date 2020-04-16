package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChangeVideoActivity extends AppCompatActivity {
    private EditText input;
    private Button update, cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_screen);

        Intent intent = getIntent();
        final int ID = intent.getIntExtra("ID", -1);

        input = (EditText) findViewById(R.id.et_url);
        update = (Button) findViewById(R.id.btn_enter);
        cancel = (Button) findViewById(R.id.btn_cancel);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = input.getText().toString();

                // Parsing the URL in search of the Video ID
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

                Intent intent = new Intent();
                intent.putExtra("NEW_URL", videoID);
                intent.putExtra("ID", ID);
                setResult(3, intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
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
