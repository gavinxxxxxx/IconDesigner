package me.gavin.util;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import me.gavin.icon.designer.BuildConfig;

/**
 * AdHelper
 *
 * @author gavin.xiong 2017/11/13
 */
public final class AdHelper {

    public static void init(AdView adView) {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }
        });
        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG)
            builder.addTestDevice("860DDD0506EDD9F91C359F8DF11080CF");
        adView.loadAd(builder.build());
    }
}
