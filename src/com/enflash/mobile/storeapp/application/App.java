package com.enflash.mobile.storeapp.application;

import android.app.Application;

public class App extends Application {

    private static App instance;

    public static App getAppInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            instance = this;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}