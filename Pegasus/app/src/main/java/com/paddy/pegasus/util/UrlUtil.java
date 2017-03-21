package com.paddy.pegasus.util;

import android.support.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by pengpeng on 2017/3/21.
 */

public class UrlUtil {
    public static String keyFromUrl(String url){
        String key;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(url.getBytes());
            key = byteToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            key = String.valueOf(url.hashCode());
        }
        return key;
    }

    @NonNull
    public static String byteToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
