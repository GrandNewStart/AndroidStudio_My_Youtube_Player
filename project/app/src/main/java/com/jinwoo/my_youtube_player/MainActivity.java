package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private ListView listView;
    private TextView tv_top, tv_selectAll;
    private Button btn_add, btn_delete;
    private ImageView iv_listMenu;
    private CheckBox cb_selectAll;

    private ArrayList<Video> videoList = new ArrayList<Video>();
    private VideoAdapter adapter;
    private String videoID = "", Google_nick, Google_photo;
    private VideoDBHelper myDb;
    private enum Mode {normal, check};
    private Mode mode = Mode.normal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        Intent intent = getIntent();
        Google_nick = intent.getStringExtra("NICKNAME");
        Google_photo = intent.getStringExtra("PHOTO URL");

        // View mapping
        iv_listMenu = (ImageView) findViewById(R.id.iv_listMenu);
        listView = (ListView) findViewById(R.id.listView);
        btn_add = (Button) findViewById(R.id.button_add);
        btn_delete = (Button) findViewById(R.id.button_delete);
        tv_top = (TextView) findViewById(R.id.text_top);
        tv_selectAll = (TextView) findViewById(R.id.tv_selectAll);
        cb_selectAll = (CheckBox) findViewById(R.id.cb_selectAll);

        // Load data from database and attach it to the listview in normal mode
        setListMode(Mode.normal);

        // Set click listeners
        iv_listMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(MainActivity.this, v);
                menu.setOnMenuItemClickListener(MainActivity.this);
                menu.inflate(R.menu.listview_menu);
                menu.show();
            }
        });

        cb_selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < videoList.size(); i++) {
                    videoList.get(i).setChecked(cb_selectAll.isChecked());
                }
                adapter = new VideoAdapter(videoList, getApplicationContext(), 2);
                listView.setAdapter(adapter);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setListMode(Mode.normal);
                Intent intent = new Intent(MainActivity.this, AddVideoActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myDb = new VideoDBHelper(getApplicationContext());
                Cursor cursor = myDb.getAllData();

                if (cursor.getCount() == 0) {
                    Toast.makeText(MainActivity.this, "영상을 선택해주십시오", Toast.LENGTH_SHORT);
                    return;
                }

                while(cursor.moveToNext()) {
                    int i = cursor.getPosition();
                    int id = cursor.getInt(0);
                    Video video = videoList.get(i);
                    if(video.isChecked()) {
                        myDb.deleteData(video.getID());
                    }
                }
                setListMode(Mode.normal);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mode == Mode.normal) {
                    Intent intent = new Intent(getApplicationContext(), YoutubePlayerActivity.class);
                    intent.putExtra("URL", videoList.get(position).getVideoID());
                    startActivity(intent);
                }
                else {
                    videoList.get(position).setChecked(!videoList.get(position).isChecked());
                }
            }
        });

    }

    public void setListMode(Mode newMode) {
        mode = newMode;

        myDb = new VideoDBHelper(getApplicationContext());
        Cursor cursor = myDb.getAllData();

        if (cursor.getCount() == 0) {
            videoList.clear();
        }
        else {
            videoList.clear();
            while (cursor.moveToNext()) {
                Video video = new Video(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5));
                video.setID(cursor.getInt(0));
                videoList.add(video);
            }
        }

        switch(mode) {
            case normal:
                tv_top.setText(Google_nick + "님의\n저장된 재생 목록");
                tv_top.setTextSize(15);
                btn_delete.setVisibility(View.INVISIBLE);
                tv_selectAll.setVisibility(View.INVISIBLE);
                cb_selectAll.setVisibility(View.INVISIBLE);
                iv_listMenu.setVisibility(View.VISIBLE);
                adapter = new VideoAdapter(videoList, getApplicationContext(), 1);
                break;
            case check:
                tv_top.setText("삭제할 동영상을 선택하십시오");
                tv_top.setTextSize(15);
                btn_delete.setVisibility(View.VISIBLE);
                tv_selectAll.setVisibility(View.VISIBLE);
                cb_selectAll.setVisibility(View.VISIBLE);
                iv_listMenu.setVisibility(View.INVISIBLE);
                cb_selectAll.setChecked(false);
                adapter = new VideoAdapter(videoList, getApplicationContext(), 2);
                break;
        }

        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (mode != Mode.normal) {
            setListMode(Mode.normal);
        }
        else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If URL data is fetched from the user
        if (resultCode == 1) {
            videoID = data.getStringExtra("VIDEOID");
            if (videoID.equals("")) {
                return;
            }

            String title = data.getStringExtra("TITLE");
            String thumbnail = data.getStringExtra("THUMBNAIL");
            String date = data.getStringExtra("DATE");
            String uploader = data.getStringExtra("UPLOADER");
            Resources res = getResources();
            Drawable img = ResourcesCompat.getDrawable(res, R.drawable.icon_play, null);

            Video video = new Video(thumbnail, title, uploader, date, videoID);

            if (myDb.insertData(thumbnail, title, uploader, date, videoID)) {
                Toast.makeText(getApplicationContext(),"플레이 리스트 추가", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"실패", Toast.LENGTH_SHORT).show();
            }

            videoList.add(video);
            adapter = new VideoAdapter(videoList, getApplicationContext(), 1);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.listview_menu1:
                // Go to check mode
                setListMode(Mode.check);
                break;
        }
        return false;
    }
}