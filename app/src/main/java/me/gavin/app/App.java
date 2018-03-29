package me.gavin.app;

import android.app.Application;

import me.gavin.util.AdHelper;
import me.gavin.util.CrashHandler;

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
        CrashHandler.get().init();
        mApp = this;
        AdHelper.initGoogle(this);
    }

    public static App get() {
        return mApp;
    }
}
