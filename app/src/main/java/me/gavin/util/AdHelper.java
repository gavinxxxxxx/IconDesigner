package me.gavin.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import me.gavin.icon.designer.BuildConfig;

/**
 * AdHelper
 *
 * @author gavin.xiong 2017/11/13
 */
public final class AdHelper {

    private static final String NEXUS5X = "860DDD0506EDD9F91C359F8DF11080CF";
    private static final String APP_ID = "ca-app-pub-9410365151312505~8319846562";
    public static final String UNIT_ID = "ca-app-pub-9410365151312505/4544253089";

    public static void initGoogle(Context context) {
        MobileAds.initialize(context, APP_ID);
    }

    public static void loadGoogle(ViewGroup container, String unitId) {
        AdView adView = new AdView(container.getContext());
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(unitId);
        adView.setVisibility(View.GONE);
        container.addView(adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            builder.addTestDevice(NEXUS5X);
        }
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }
        });
        adView.loadAd(builder.build());
    }

//    public static BannerView loadQQ(Activity activity, ViewGroup container) {
//        BannerView adBanner = new BannerView(activity, ADSize.BANNER, "1106530223", "3020823804797474");
//        adBanner.setRefresh(30);
//        adBanner.setADListener(new AbstractBannerADListener() {
//            @Override
//            public void onNoAD(AdError error) {
//                // onNoAD - Banner onNoAD，eCode = 6000, eMsg = 未知错误，详细码：100135
//                L.e("onNoAD - " + String.format("Banner onNoAD，eCode = %s, eMsg = %s", error.getErrorCode(), error.getErrorMsg()));
//            }
//
//            @Override
//            public void onADReceiv() {
//                L.e("onADReceiv - ");
//            }
//        });
//        container.addView(adBanner);
//        adBanner.loadAD();
//        return adBanner;
//    }

//    public static void initYM(Context context) {
//        AdManager.getInstance(context).init("62358a24a8ceb2f3", "c75752e67808bd0e", true);
//    }
//
//    public static void loadYM(Context context, ViewGroup container) {
//        View bannerView = BannerManager.getInstance(context)
//                .getBannerView(context, new BannerViewListener() {
//                    @Override
//                    public void onRequestSuccess() {
//                        L.e("onRequestSuccess");
//                    }
//
//                    @Override
//                    public void onSwitchBanner() {
//                        L.e("onSwitchBanner");
//                    }
//
//                    @Override
//                    public void onRequestFailed() {
//                        L.e("onRequestFailed");
//                    }
//                });
//        container.addView(bannerView);
//    }
//
//    public static void disposeYM(Context context) {
//        BannerManager.getInstance(context).onDestroy();
//    }
}
