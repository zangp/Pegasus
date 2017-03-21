package com.paddy.pegasus.db;

/**
 * Created by pengpeng on 2017/3/20.
 */

public class DatabaseConstants {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "down.db";

    public static class DownloadTable{
        public static final String TABLE_NAME= "download_info";

        public static final String URL = "url";
        public static final String IS_DOWNLOAD = "is_download";
        public static final String DOWNLOADING = "downloading";
        public static final String CONTENT_SIZE = "content_size";
        // 开始下载的点
        public static final String START = "start";
    }
}
