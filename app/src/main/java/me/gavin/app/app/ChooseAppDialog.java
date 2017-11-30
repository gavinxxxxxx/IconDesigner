package me.gavin.app.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import me.gavin.base.function.Consumer;
import me.gavin.icon.designer.databinding.LayoutRecyclerBinding;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseAppDialog extends BottomSheetDialog {

    private LayoutRecyclerBinding mBinding;
    private List<AppInfo> appList;
    private AppInfoAdapter mAdapter;

    private Consumer<AppInfo> callback;

    public ChooseAppDialog(@NonNull Context context, Consumer<AppInfo> callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = LayoutRecyclerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setDimAmount(0.4f);


        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        appList = new ArrayList<>();
        for (ResolveInfo resolve : list) {
            appList.add(AppInfo.from(pm, resolve.activityInfo));
        }

        mAdapter = new AppInfoAdapter(getContext(), appList);
        mAdapter.setCallback(appInfo -> {
            if (callback != null) {
                callback.accept(appInfo);
            }
            dismiss();
        });
        mBinding.recycler.setAdapter(mAdapter);
    }

}
