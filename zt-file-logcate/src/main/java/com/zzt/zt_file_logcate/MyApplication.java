package com.zzt.zt_file_logcate;

import android.app.Application;
import android.content.Context;

/**
 * @author: zeting
 * @date: 2025/4/9
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public Context getAppContext() {
        return this;
    }

}
