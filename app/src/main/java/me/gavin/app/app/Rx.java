package me.gavin.app.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import io.reactivex.Observable;

public class Rx {

    public static Observable<ResolveInfo> intentActivities(PackageManager pm, Intent intent, int flags) {
        return Observable.defer(() -> Observable.fromIterable(pm.queryIntentActivities(intent, flags)));
    }

    public static Intent getMainIntent() {
        return new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
    }
}
