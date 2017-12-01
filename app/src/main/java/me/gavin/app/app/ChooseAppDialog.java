package me.gavin.app.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.util.DiffUtil;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.base.function.Consumer;
import me.gavin.icon.designer.databinding.LayoutRecyclerBinding;

/**
 * ChooseAppDialog
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseAppDialog extends BottomSheetDialog {

    private LayoutRecyclerBinding mBinding;
    private AppInfoAdapter mAdapter;

    private List<AppInfo> appInfoList;
    private List<AppInfo> tempList;

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

        PackageManager pm = getContext().getPackageManager();
        Rx.intentActivities(pm, Rx.getMainIntent(), 0)
                .map(resolve -> AppInfo.from(pm, resolve.activityInfo))
                .map(appInfo -> {
                    tempList.add(appInfo);
                    Collections.sort(tempList, (o1, o2) -> o1.labelPinyin.compareTo(o2.labelPinyin));
                    return tempList;
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    appInfoList = new ArrayList<>();
                    tempList = new ArrayList<>();
                    mAdapter = new AppInfoAdapter(getContext(), appInfoList);
                    mAdapter.setCallback(appInfo -> {
                        if (callback != null) {
                            callback.accept(appInfo);
                        }
                        dismiss();
                    });
                    mBinding.recycler.setAdapter(mAdapter);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newList -> {
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(appInfoList, newList));
                    appInfoList.clear();
                    appInfoList.addAll(tempList);
                    diffResult.dispatchUpdatesTo(mAdapter);
                    mBinding.recycler.scrollToPosition(0);
                }, Throwable::printStackTrace);
    }

}
