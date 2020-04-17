package com.jinwoo.my_youtube_player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

public class VideoDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Videos.db";
    public static final String TABLE_NAME = "video_table";

    public VideoDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE_NAME
                + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, THUMBNAIL TEXT, TITLE TEXT, UPLOADER TEXT, DATE TEXT, VIDEOID TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String thumbnail, String title, String uploader, String date, String videoId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("THUMBNAIL", thumbnail);
        contentValues.put("TITLE", title);
        contentValues.put("UPLOADER", uploader);
        contentValues.put("DATE", date);
        contentValues.put("VIDEOID", videoId);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE ID = " + id);
    }

    public void updateData(int id, Video video) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME
                + " SET THUMBNAIL = '" + video.getThumbnail() + "'"
                + ", TITLE = '" + video.getTitle() + "'"
                + ", UPLOADER = '" + video.getUploader() + "'"
                + ", DATE = '" + video.getDate() + "'"
                + ", VIDEOID = '" + video.getVideoID() + "'"
                + " WHERE ID = " + id;
        Log.d("LOG", "query = " + query);
        db.execSQL(query);
    }
}
