package me.gavin.app.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/30
 */
public class AppInfo {

    public String packageName;
    public String className;

    public String label;

    public String labelPinyin;
    public String sPinyin;

    private WeakReference<Drawable> drawable;

    @Nullable
    public static AppInfo from(PackageManager packageManager, ActivityInfo activityInfo) {
        AppInfo app = new AppInfo();
        return app.resolve(packageManager, activityInfo) ? app : null;
    }

    private boolean resolve(PackageManager pm, ActivityInfo activityInfo) {
        packageName = activityInfo.packageName;
        className = activityInfo.name;

        label = activityInfo.loadLabel(pm).toString();

        try {
            labelPinyin = PinyinHelper
                    .convertToPinyinString(label, "", PinyinFormat.WITHOUT_TONE)
                    .toLowerCase();
            sPinyin = PinyinHelper
                    .getShortPinyin(label).toLowerCase();
        } catch (PinyinException e) {
            labelPinyin = "";
            sPinyin = "";
            e.printStackTrace();
        }

        drawable = new WeakReference<>(activityInfo.loadIcon(pm));

        return true;
    }

    public Drawable getDrawable(Context context) {
        if (drawable.get() != null) {
            return drawable.get();
        } else {
            try {
                Drawable icon = context.getPackageManager()
                        .getActivityIcon(new ComponentName(packageName, className));
                drawable = new WeakReference<>(icon);
                return drawable.get();
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
    }

    public Intent getIntent() {
        return new Intent(Intent.ACTION_MAIN)
                .putExtra("me.gavin.icon.designer.id", UUID.randomUUID().toString())
                .setComponent(new ComponentName(packageName, className))
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", label='" + label + '\'' +
                ", labelPinyin='" + labelPinyin + '\'' +
                ", sPinyin='" + sPinyin + '\'' +
                '}';
    }
}