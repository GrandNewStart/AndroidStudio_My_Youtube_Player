package com.jinwoo.my_youtube_player;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private ListView listView;
    private TextView tv_top, tv_selectAll;
    private Button btn_add, btn_delete;
    private ImageView iv_listMenu;
    private CheckBox cb_selectAll;

    private ArrayList<Video> videoList = new ArrayList<Video>();
    private VideoAdapter adapter;
    private String Google_nick, Google_photo;
    private VideoDBHelper myDb;
    public enum Mode {normal, check};
    public static Mode mode = Mode.normal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // View mapping
        iv_listMenu = (ImageView) findViewById(R.id.iv_listMenu);
        listView = (ListView) findViewById(R.id.listView);
        btn_add = (Button) findViewById(R.id.button_add);
        btn_delete = (Button) findViewById(R.id.button_delete);
        tv_top = (TextView) findViewById(R.id.text_top);
        tv_selectAll = (TextView) findViewById(R.id.tv_selectAll);
        cb_selectAll = (CheckBox) findViewById(R.id.cb_selectAll);

        btn_delete.setVisibility(View.INVISIBLE);
        tv_selectAll.setVisibility(View.INVISIBLE);
        cb_selectAll.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        Google_nick = intent.getStringExtra("NICKNAME");
        Google_photo = intent.getStringExtra("PHOTO URL");
        tv_top.setText(Google_nick + "님의\n저장된 재생 목록");
        tv_top.setTextSize(15);

        // Listview data mapping
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
        adapter = new VideoAdapter(videoList, getApplicationContext());
        listView.setAdapter(adapter);

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

        // Select/Unselect all items
        cb_selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < videoList.size(); i++) {
                    videoList.get(i).setChecked(cb_selectAll.isChecked());
                }
                adapter.notifyDataSetChanged();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setListMode(Mode.normal);
                setListMode(Mode.normal);
                Intent intent = new Intent(MainActivity.this, AddVideoActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int i = 0, count = 0;

                while (videoList.size() > i) {
                    Video video = videoList.get(i);
                    if(video.isChecked()) {
                        videoList.remove(i);
                        myDb.deleteData(video.getID());
                        count++;
                        continue;
                    }
                    i++;
                }

                Toast.makeText(getApplicationContext(), "비디오 " + count + "개가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                setListMode(Mode.normal);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = videoList.get(position);
                if (mode == Mode.normal) {
                    // Play video
                    Intent intent = new Intent(getApplicationContext(), YoutubePlayerActivity.class);
                    intent.putExtra("URL", videoList.get(position).getVideoID());
                    startActivity(intent);
                }
                else {
                    // Check items
                    video.setChecked(!video.isChecked());
                    if (video.isChecked()) view.setBackgroundColor(Color.parseColor("#A07771"));
                    else view.setBackgroundColor(Color.parseColor("#B57171"));
                }
            }
        });
    }

    public void setListMode(Mode newMode) {
        mode = newMode;
        switch(mode) {
            case normal:
                tv_top.setText(Google_nick + "님의\n저장된 재생 목록");
                tv_top.setTextSize(15);
                btn_delete.setVisibility(View.INVISIBLE);
                tv_selectAll.setVisibility(View.INVISIBLE);
                cb_selectAll.setVisibility(View.INVISIBLE);
                iv_listMenu.setVisibility(View.VISIBLE);
                for (int i = 0; i < videoList.size(); i++) {
                    videoList.get(i).setChecked(false);
                }
                adapter.notifyDataSetChanged();
                break;
            case check:
                tv_top.setText("삭제할 동영상을 선택하십시오");
                tv_top.setTextSize(15);
                btn_delete.setVisibility(View.VISIBLE);
                tv_selectAll.setVisibility(View.VISIBLE);
                cb_selectAll.setVisibility(View.VISIBLE);
                iv_listMenu.setVisibility(View.INVISIBLE);
                cb_selectAll.setChecked(false);
                adapter.notifyDataSetChanged();
                break;
        }
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

        // If new video is to be added
        if (resultCode == 1) {
            String videoID = data.getStringExtra("VIDEOID");
            String title = data.getStringExtra("TITLE");
            String thumbnail = data.getStringExtra("THUMBNAIL");
            String date = data.getStringExtra("DATE");
            String uploader = data.getStringExtra("UPLOADER");
            Video video = new Video(thumbnail, title, uploader, date, videoID);

            if (myDb.insertData(thumbnail, title, uploader, date, videoID))
                Toast.makeText(MainActivity.this, "플레이 리스트 추가", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this,"실패", Toast.LENGTH_SHORT).show();

            Cursor cursor = myDb.getAllData();
            int ID = 0;
            while(cursor.moveToNext()) {ID = cursor.getInt(0);}
            video.setID(ID);

            videoList.add(video);
            adapter.notifyDataSetChanged();

        }

        // If an existing video is to be updated
        else if (resultCode == 2) {
            int ID = data.getIntExtra("ID", -1);
            String videoID = data.getStringExtra("VIDEOID");
            String title = data.getStringExtra("TITLE");
            String thumbnail = data.getStringExtra("THUMBNAIL");
            String date = data.getStringExtra("DATE");
            String uploader = data.getStringExtra("UPLOADER");
            Video video = new Video(thumbnail, title, uploader, date, videoID);
            Log.d("LOG", ID + ", " + video.getTitle());
            myDb.updateData(ID, video);

            for (int i = 0; i < videoList.size(); i++) {
                if (videoList.get(i).getID() == ID) {
                    videoList.get(i).setTitle(title);
                    videoList.get(i).setThumbnail(thumbnail);
                    videoList.get(i).setDate(date);
                    videoList.get(i).setUploader(uploader);
                    videoList.get(i).setVideoID(videoID);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            // Go to check mode
            case R.id.listview_menu1:
                setListMode(Mode.check);
                break;
        }
        return false;
    }
}