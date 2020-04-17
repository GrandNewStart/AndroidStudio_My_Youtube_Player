package com.jinwoo.my_youtube_player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class VideoAdapter extends BaseAdapter {
    private ArrayList<Video> videoList;
    private LayoutInflater inflater;
    private Context context;
    private MainActivity activity;
    private Bitmap bitmap;

    private ImageView iv_thumbnail, iv_itemMenu;
    private TextView tv_title, tv_uploader, tv_date;

    public VideoAdapter(ArrayList<Video> videoList, Context context, MainActivity activity) {
        this.videoList = videoList;
        this.context = context;
        this.activity = activity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Video getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_layout, parent, false);

        final Video video = videoList.get(position);

        iv_thumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
        iv_itemMenu = (ImageView) convertView.findViewById(R.id.iv_itemMenu);
        tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        tv_uploader = (TextView) convertView.findViewById(R.id.tv_uploader);
        tv_date = (TextView) convertView.findViewById(R.id.tv_date);

        // Getting thumbnail img from network
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(video.getThumbnail());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        // Mapping thumbnail
        try {
            thread.join();
            iv_thumbnail.setImageBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Mapping other UIs
        tv_title.setText(video.getTitle());
        tv_uploader.setText(video.getUploader());
        tv_date.setText(video.getDate());

        // Hiding/Displaying item menu
        if (MainActivity.mode == MainActivity.Mode.normal)
            iv_itemMenu.setVisibility(View.VISIBLE);
        else
            iv_itemMenu.setVisibility(View.INVISIBLE);

        // Item menu click
        iv_itemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            // Change video
                            case R.id.item_menu1:
                                int ID = video.getID();
                                Intent intent1 = new Intent(context, ChangeVideoActivity.class);
                                intent1.putExtra("ID TO UPDATE", ID);
                                activity.startActivityForResult(intent1, 2);
                                break;

                            // Delete video
                            case R.id.item_menu2:
                                Intent intent2 = new Intent(context, DeleteConfirmActivity.class);
                                intent2.putExtra("TITLE TO DELETE", video.getTitle());
                                intent2.putExtra("ID TO DELETE", video.getID());
                                activity.startActivityForResult(intent2, 3);
                                break;
                        }

                        return false;
                    }
                });
                menu.inflate(R.menu.item_menu);
                menu.show();
            }
        });

        // Check check
        if (video.isChecked())
            convertView.setBackgroundResource(R.drawable.item_checked);
        else
            convertView.setBackgroundResource(R.drawable.item_unchecked);

        return convertView;
    }
}
