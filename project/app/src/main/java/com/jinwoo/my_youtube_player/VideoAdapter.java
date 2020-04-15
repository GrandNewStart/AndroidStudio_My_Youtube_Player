package com.jinwoo.my_youtube_player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private Bitmap bitmap;
    private int LISTVIEW_MODE;

    private ImageView iv_thumbnail;
    private LinearLayout background;
    private TextView tv_title, tv_uploader, tv_date;
    private CheckBox cb_checkbox;

    public VideoAdapter(ArrayList<Video> videoList, Context context, int mode) {
        this.videoList = videoList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LISTVIEW_MODE = mode;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_layout, parent, false);

        background = (LinearLayout) convertView.findViewById(R.id.itemBackground);
        iv_thumbnail = (ImageView) convertView.findViewById(R.id.img_thumbnail);
        tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        tv_uploader = (TextView) convertView.findViewById(R.id.tv_uploader);
        tv_date = (TextView) convertView.findViewById(R.id.tv_date);
        cb_checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

        final Video video = videoList.get(position);
        final int checkboxPosition = position;

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
        cb_checkbox.setChecked(video.isChecked());

        // Checkbox logic
        cb_checkbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean newState = !videoList.get(checkboxPosition).isChecked();
                videoList.get(checkboxPosition).setChecked(newState);
            }
        });
        cb_checkbox.setChecked(video.isChecked());

        switch (LISTVIEW_MODE) {
            // normal mode
            case 1:
                background.setBackgroundColor(Color.parseColor("#B57171"));
                cb_checkbox.setVisibility(View.INVISIBLE);
                cb_checkbox.setActivated(false);
                break;

            // check mode
            case 2:
                background.setBackgroundColor(Color.parseColor("#A07771"));
                cb_checkbox.setVisibility(View.VISIBLE);
                cb_checkbox.setActivated(true);
                break;
        }

        return convertView;
    }
}
