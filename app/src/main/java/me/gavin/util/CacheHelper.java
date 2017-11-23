package me.gavin.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 缓存助手
 *
 * @author gavin.xiong 2017/4/28
 */
public class CacheHelper {

    private static String getAppDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + File.separator + "IconDesigner" + File.separator;
            File appDir = new File(path);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            return path;
        } else {
            return null;
        }
    }

    public static String saveBitmap(Bitmap bitmap, String name) throws IOException {
        String path = getAppDir() + name + ".png";
        try (OutputStream os = new FileOutputStream(path)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            bitmap.recycle();
            return path;
        }
    }

    public static Uri file2Uri(Context context, File imageFile) {
        String path = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * 通知相册更新
     */
    public static void updateAlbum(Context context, Uri uri) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

}
