package com.paddy.pegasus.util;

import android.os.Environment;
import android.text.TextUtils;

import com.paddy.pegasus.AppApplication;

import java.io.File;

/**
 * Created by pengpeng on 2017/3/13.
 */

public class FileUtil {

    public static void init(){
        File file = new File(getExternalCacheDir());
        if (!file.exists())
            file.mkdirs();
    }

    public static String getExternalCacheDir(){
        if (existSDCard()){
            return AppApplication.getApp().getExternalCacheDir()
                    .getAbsolutePath().toString() + File.separator + "file" + File.separator;
        } else {
            new RuntimeException("sd card未挂载");
            return "";
        }
    }

    private static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public static String getNameFromUrl(String url){
        if (TextUtils.isEmpty(url))
            return null;
        String[] splitString = url.split("/");
        return splitString[splitString.length - 1];
    }
}
