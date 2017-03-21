package com.paddy.pegasus;

import android.app.Application;

/**
 * Created by pengpeng on 2017/3/13.
 */

public class AppApplication extends Application {
    private static AppApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static AppApplication getApp(){ return app;}
}
