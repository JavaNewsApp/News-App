package com.example.ForMoreNews.bigproject;

import android.app.Application;

/**
 * Created by yu on 17-9-11.
 */

public class App extends Application {
    private static App app;
    public static App getInstance(){
        return app;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
