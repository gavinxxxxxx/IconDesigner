package me.gavin.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

import java.util.UUID;

/**
 * 快捷方式
 *
 * @author gavin.xiong 2017/11/30
 */
public class ShortcutUtil {

    /**
     * 添加快捷方式
     *
     * @param context  context
     * @param shortcut 要启动的Intent
     * @param name     name
     */
    public static void addShortcut(Context context, Intent shortcut, String name, Bitmap iconBitmap) {
//        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        // 是否允许重复创建
//        intent.putExtra("duplicate", true);
//        // 快捷方式的标题
//        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
//        // 快捷方式的图标
//        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, iconBitmap);
//        // 快捷方式的动作
//        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
//        // 通知 launcher 创建
//        context.sendBroadcast(intent);
        ShortcutManagerCompat.requestPinShortcut(context,
                new ShortcutInfoCompat.Builder(context, UUID.randomUUID().toString())
                        .setIcon(IconCompat.createWithBitmap(iconBitmap))
                        .setShortLabel(name)
                        .setIntent(shortcut)
                        .build(),
                null);
    }

}
