package me.gavin.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

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
        if (BuildConfig.DEBUG) {
            builder.addTestDevice("860DDD0506EDD9F91C359F8DF11080CF");
        }
        adView.loadAd(builder.build());
    }

    public static BannerView init(Activity activity, ViewGroup container) {
        BannerView adBanner = new BannerView(activity, ADSize.BANNER, "1106530223", "3020823804797474");
        adBanner.setRefresh(30);
        adBanner.setADListener(new AbstractBannerADListener() {
            @Override
            public void onNoAD(AdError error) {
                // onNoAD - Banner onNoAD，eCode = 6000, eMsg = 未知错误，详细码：100135
                L.e("onNoAD - " + String.format("Banner onNoAD，eCode = %s, eMsg = %s", error.getErrorCode(), error.getErrorMsg()));
            }

            @Override
            public void onADReceiv() {
                L.e("onADReceiv - ");
            }
        });
        container.addView(adBanner);
        adBanner.loadAD();
        return adBanner;
    }
}
