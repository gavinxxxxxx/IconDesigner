package me.gavin.app;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import net.youmi.android.AdManager;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;

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
        MobileAds.initialize(this, "ca-app-pub-9410365151312505~8319846562");
    }

    public static App get() {
        return mApp;
    }
}
