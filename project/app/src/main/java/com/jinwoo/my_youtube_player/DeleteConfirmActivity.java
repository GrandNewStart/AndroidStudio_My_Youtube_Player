package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteConfirmActivity extends AppCompatActivity {

    TextView tv_confirm;
    Button btn_yes, btn_no;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_confirm_screen);

        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_no = (Button) findViewById(R.id.btn_no);

        Intent intent = getIntent();
        final String videoTitle = intent.getStringExtra("TITLE TO DELETE");
        final int videoId = intent.getIntExtra("ID TO DELETE", -1);
        tv_confirm.setText("'" + videoTitle + "' 을(를) 삭제하시겠습니까?");

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteConfirmActivity.this, MainActivity.class);
                intent.putExtra("RESPONSE", true);
                intent.putExtra("ID TO DELETE", videoId);
                intent.putExtra("TITLE TO DELETE", videoTitle);
                setResult(3, intent);
                finish();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteConfirmActivity.this, MainActivity.class);
                intent.putExtra("RESPONSE", false);
                setResult(3, intent);
                finish();
            }
        });
    }
}
