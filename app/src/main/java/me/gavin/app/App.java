package me.gavin.app;

import android.app.Application;

/**
 * Application
 *
 * @author gavin.xiong 2017/12/6
 */
public class App extends Application {

    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static App get() {
        return mApp;
    }
}
