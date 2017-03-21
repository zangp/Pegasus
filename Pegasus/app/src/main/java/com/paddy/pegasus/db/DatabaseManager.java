package com.paddy.pegasus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.paddy.pegasus.AppApplication;
import com.paddy.pegasus.data.DownloadInfo;
import com.paddy.pegasus.util.UrlUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengpeng on 2017/3/20.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    protected static final String LOG_TAG = DatabaseManager.class.getSimpleName();
    private static DatabaseManager instance;

    private DatabaseManager(Context context, String name) {
        super(context, name, null, DatabaseConstants.DATABASE_VERSION);
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (LOG_TAG) {
                if (instance == null) {
                    instance = new DatabaseManager(AppApplication.getApp(), DatabaseConstants.DATABASE_NAME);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDownloadTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createDownloadTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + DatabaseConstants.DownloadTable.TABLE_NAME
                + "("
                + DatabaseConstants.DownloadTable.URL + " text PRIMARY KEY,"
                + DatabaseConstants.DownloadTable.CONTENT_SIZE + " integer DEFAULT 0,"
                + DatabaseConstants.DownloadTable.DOWNLOADING + " integer DEFAULT 0,"
                + DatabaseConstants.DownloadTable.START + " integer DEFAULT 0"
                + ");";
        db.execSQL(sql);
    }

    public void insertDownloadInfo(List<DownloadInfo> info) {
        if (info == null || info.size() <= 0)
            return;

        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            Log.e("zp_test", LOG_TAG + " db is null");
            return;
        }

        for (int i = 0; i < info.size(); i++) {
            insertDownloadInfo(db, info.get(i));
        }
    }

    public void insertDownloadInfo(SQLiteDatabase db, DownloadInfo info) {
        if (info == null || db == null)
            return;
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.DownloadTable.URL,
                UrlUtil.keyFromUrl(info.getUrl()));
        cv.put(DatabaseConstants.DownloadTable.CONTENT_SIZE, info.getContentSize());
        cv.put(DatabaseConstants.DownloadTable.DOWNLOADING, info.isDownloading());
        cv.put(DatabaseConstants.DownloadTable.START, info.getStart());
        db.insert(DatabaseConstants.DownloadTable.TABLE_NAME, null, cv);
    }

    public DownloadInfo isExistUrl(String url) {
        String key = getKey(url);
        String sql = "select * from " + DatabaseConstants.DownloadTable.TABLE_NAME
                + " where "
                + DatabaseConstants.DownloadTable.URL
                + " = '"
                + key
                + "'";
        Cursor cursor = doQueryAction(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                DownloadInfo info = getInfoFromCursor(cursor);
                if (cursor != null)
                    cursor.close();
                return info;
            } while (cursor.moveToNext());
        }
        return null;
    }

    public void updateStart(String url, int start) {
        String key = getKey(url);
        ContentValues cv = new ContentValues();
        cv.put(DatabaseConstants.DownloadTable.START, start);
        getWritableDatabase()
                .update(DatabaseConstants.DownloadTable.TABLE_NAME, cv,
                        DatabaseConstants.DownloadTable.URL + " = ?",
                        new String[]{key});
    }

    public ArrayList<DownloadInfo> getAllInfo() {
        ArrayList<DownloadInfo> list = new ArrayList<>();
        String sql = "select * from " + DatabaseConstants.DownloadTable.TABLE_NAME;
        Cursor cursor = doQueryAction(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                DownloadInfo info = getInfoFromCursor(cursor);
                list.add(info);
            } while (cursor.moveToNext());

            if (cursor != null)
                cursor.close();
            return list;
        }
        return null;
    }

    private Cursor doQueryAction(String sql, String[] selectionArgs) {
        return getReadableDatabase().rawQuery(sql, selectionArgs);
    }

    private DownloadInfo getInfoFromCursor(Cursor cursor) {
        DownloadInfo info = new DownloadInfo();
        info.setUrl(cursor.getString(cursor
                .getColumnIndex(DatabaseConstants.DownloadTable.URL)));
        info.setContentSize(cursor.getInt(cursor
                .getColumnIndex(DatabaseConstants.DownloadTable.CONTENT_SIZE)));
        info.setDownloading(cursor.getInt(cursor.getColumnIndex(DatabaseConstants
                .DownloadTable.DOWNLOADING)) == 1 ? true : false);
        info.setStart(cursor.getInt(cursor
                .getColumnIndex(DatabaseConstants.DownloadTable.START)));

        return info;
    }

    private String getKey(String url) {
        if (url == null) {
            Log.e("zp_test", LOG_TAG + " url is null......");
            return null;
        }
        return UrlUtil.keyFromUrl(url);
    }
}
