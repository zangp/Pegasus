package com.paddy.pegasus.executors;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ProgressBar;

import com.paddy.pegasus.data.DownloadInfo;
import com.paddy.pegasus.db.DatabaseManager;
import com.paddy.pegasus.util.FileUtil;
import com.paddy.pegasus.util.IOCloseUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pengpeng on 2017/3/13.
 */

public class DownloadTaskRunnable implements Runnable {
    protected final String LOG_TAG = DownloadTaskRunnable.class.getSimpleName();
    public static final int CONNECT_TIME = 15000;
    public static final int BUFFER_SIZE = 3 * 1024;
    private String url;
    private Handler handler;
    private int currentLength;
    private String fileName;
    // 注意在使用range属性时，总长度不要用getContentLength
    private float totalLength;
    private ProgressBar progressBar;

    public DownloadTaskRunnable(String url,Handler handler,ProgressBar progressBar) {
        this.url = url;
        this.progressBar = progressBar;
        this.handler = handler;
        fileName = FileUtil.getNameFromUrl(url).replace(".apk","") + ".apk";
    }

    @Override
    public void run() {
        Log.d("zp_test","url: " + url);
        if (TextUtils.isEmpty(url))
            return;
        resumeDownload();
    }

    private void resumeDownload(){
        RandomAccessFile mAccessFile = null;
        InputStream mStream = null;

        try {
            HttpURLConnection urlConnection = createConnection();
            File file = new File(FileUtil.getExternalCacheDir(),fileName);

            if (file != null && file.length() > 0) {
                DownloadInfo info = DatabaseManager.getInstance().isExistUrl(url);
                if (info != null) {
                    totalLength = info.getContentSize();
                    Log.d("zp_test","start: " + info.getStart() + " end: " + info.getContentSize());
                    urlConnection.setRequestProperty("Range","bytes=" + info.getStart() + "-" + info.getContentSize());
                    // 设置range后，content length的值会发生变化，变成没有下载的内容长度
                    // setRequestProperty这个方法必须在连接发生前进行调用
                    // if (info.getContentSize() == urlConnection.getContentLength()){
                    mAccessFile = new RandomAccessFile(file,"rwd");
                    mAccessFile.seek(info.getStart());
                    currentLength = info.getStart();

                    if (info.getContentSize() == currentLength)
                        return;
                } else {
                    Log.w("zp_test",LOG_TAG + "info is null......");
                }
            } else {
                file = createFile(FileUtil.getExternalCacheDir(),fileName);
                totalLength = urlConnection.getContentLength();
                mAccessFile = new RandomAccessFile(file,"rwd");
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.setUrl(url);
                downloadInfo.setContentSize(urlConnection.getContentLength());
                DatabaseManager.getInstance().insertDownloadInfo(DatabaseManager
                        .getInstance().getWritableDatabase(),downloadInfo);
            }

            int contentSize = urlConnection.getContentLength();
            Log.d("zp_test",LOG_TAG + " currentLength......" + currentLength + " contentSize " + contentSize);
            mStream = urlConnection.getInputStream();
            while (!Thread.interrupted()){
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                if ((length = mStream.read(buffer)) != -1){
                    mAccessFile.write(buffer,0,length);
                    currentLength += length;
                    Message message = Message.obtain();
                    Log.d("zp_test","currentLength: " + currentLength);
                    Log.d("zp_test","totalLength: " + totalLength);

                    message.arg1 = (int) (currentLength / totalLength * 100);
                    message.obj = progressBar;
                    handler.sendMessage(message);
                    Log.d("zp_test","rate: " + message.arg1);
                    if (message.arg1 == 100) {
                        DatabaseManager.getInstance().updateStart(url,currentLength);
                        break;
                    }

                }
            }
            DatabaseManager.getInstance().updateStart(url,currentLength);
            Log.d("zp_test","interrupt: " + currentLength);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("zp_test",LOG_TAG + e.toString());
        }finally {
            IOCloseUtil.inputClose(mStream);
            IOCloseUtil.randomClose(mAccessFile);
        }

    }

    private void normalDownload(){
        InputStream is = null;
        OutputStream os = null;
        try {
            HttpURLConnection urlConnection = createConnection();

            is = urlConnection.getInputStream();
            Log.d("zp_test","file: " + FileUtil.getExternalCacheDir() + " name: " + fileName);
            os = new FileOutputStream(
                    createFile(FileUtil.getExternalCacheDir(),fileName));
            int contentSize = urlConnection.getContentLength();
            Log.d("zp_test","content size: " + contentSize);

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1){
                os.write(buffer,0,length);
                currentLength += length;
                os.flush();

                Message message = Message.obtain();
                message.arg1 = currentLength * 100 / contentSize;
                handler.sendMessage(message);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOCloseUtil.inputClose(is);
            IOCloseUtil.outputClose(os);
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.setConnectTimeout(CONNECT_TIME);
        urlConnection.setReadTimeout(CONNECT_TIME);
        return  urlConnection;
    }

    private File createFile(String fileDir, String fileName){
        File dir = new File(fileDir);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir,fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e("zp_test","文件创建失败！");
            }
        }
        return file;
    }
}
