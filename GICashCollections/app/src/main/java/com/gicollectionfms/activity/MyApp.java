package com.gicollectionfms.activity;

import android.app.Application;

public class MyApp extends Application {
    //https://stackoverflow.com/questions/34887331/attempt-to-invoke-virtual-method-java-lang-string-android-content-context-getpa
    private static MyApp _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
    }

    public static MyApp getInstance() {
        return _instance;
    }

}
