package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView listView;
    TextView nickname;
    Button add, delete;
    ImageView profilePhoto;
    TextView top;

    ArrayList<Video> videoList = new ArrayList<Video>();
    VideoAdapter adapter;
    String videoID = "";
    VideoDBHelper myDb;
    enum Mode {normal, check};
    Mode mode = Mode.normal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        Intent intent = getIntent();
        String nick = intent.getStringExtra("NICKNAME");
        String photo = intent.getStringExtra("PHOTO URL");

        // View mapping
        listView = (ListView) findViewById(R.id.listView);
        nickname = (TextView) findViewById(R.id.tv_nickname);
        add = (Button) findViewById(R.id.button_add);
        delete = (Button) findViewById(R.id.button_delete);
        profilePhoto = (ImageView) findViewById(R.id.iv_profile);
        top = (TextView) findViewById(R.id.text_top);

        // Mapping Google info into UIs
        nickname.setText(nick);
        Glide.with(this).load(photo).into(profilePhoto);

        // Load data from database and attach it to the listview in normal mode
        viewListMode(Mode.normal);

        // Set click listeners
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                viewListMode(Mode.normal);
                Intent intent = new Intent(MainActivity.this, AddVideoActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myDb = new VideoDBHelper(getApplicationContext());
                Cursor cursor = myDb.getAllData();

                while(cursor.moveToNext()) {
                    int i = cursor.getPosition();
                    int id = cursor.getInt(0);
                    Video video = videoList.get(i);
                    if(video.isChecked()) {
                        myDb.deleteData(video.getID());
                    }
                }
                viewListMode(Mode.normal);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myDb = new VideoDBHelper(getApplicationContext());
                Cursor cursor = myDb.getAllData();

                if (mode == Mode.normal) {
                    Intent intent = new Intent(getApplicationContext(), YoutubePlayer.class);
                    intent.putExtra("URL", videoList.get(position).getVideoID());
                    startActivity(intent);
                }
            }
        });
    }


    public void viewListMode(Mode newMode) {
        mode = newMode;

        switch(mode) {
            case normal:
                top.setText("저장된 재생 목록");
                top.setTextSize(20);
                delete.setVisibility(View.INVISIBLE);
                break;
            case check:
                top.setText("삭제할 동영상을 선택하십시오");
                top.setTextSize(15);
                delete.setVisibility(View.VISIBLE);
                break;
        }

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

        switch (mode) {
            case normal:
                adapter = new VideoAdapter(videoList, getApplicationContext(), 1);
                break;
            case check:
                adapter = new VideoAdapter(videoList, getApplicationContext(), 2);
                break;
        }

        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (mode != Mode.normal) {
            viewListMode(Mode.normal);
        }
        else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {}

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
    public void registerForContextMenu(View view) {
        super.registerForContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
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

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.menu1:
                // Hide video info
                Log.d("DEBUG", info.position+"");
                videoList.get(info.position);
                Resources res = getResources();
                Drawable img = res.getDrawable(R.drawable.icon_play);
                break;
            case R.id.menu2:
                // Change video
                break;
            case R.id.menu3:
                // Delete video
                break;
        }
        return super.onContextItemSelected(item);
    }
}