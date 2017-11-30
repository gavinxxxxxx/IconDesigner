package me.gavin.app.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/30
 */
public class AppInfo {

    public String packageName;

    public ComponentName component;

    public String label;

    // ublic Pinyin labelPinyin;

    public Drawable drawable;


    @Nullable
    public static AppInfo from(PackageManager packageManager, ActivityInfo activityInfo) {
        AppInfo app = new AppInfo();
        return app.resolve(packageManager, activityInfo) ? app : null;
    }

    private boolean resolve(PackageManager packageManager, ActivityInfo activityInfo) {
        component = new ComponentName(activityInfo.packageName, activityInfo.name);

        packageName = activityInfo.packageName;

        label = activityInfo.loadLabel(packageManager).toString();
        // labelPinyin = Pinyin.from(label);

        drawable = activityInfo.loadIcon(packageManager);

        return true;
    }

    public Intent getIntent() {
        return new Intent(Intent.ACTION_MAIN)
                // .putExtra("__materialize_nonce__", System.currentTimeMillis())
                .setComponent(component)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
    }

    @Override
    public String toString() {
        return "AppInfo{" + (component == null ? "(not resolved)" : component.flattenToShortString()) + "}";
    }

}