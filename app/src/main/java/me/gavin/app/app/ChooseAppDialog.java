package me.gavin.app.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.util.DiffUtil;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.base.function.Consumer;
import me.gavin.icon.designer.databinding.DialogChooseAppBinding;
import me.gavin.util.AdHelper;
import me.gavin.util.DisplayUtil;
import me.gavin.util.L;

/**
 * ChooseAppDialog
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseAppDialog extends BottomSheetDialog {

    private DialogChooseAppBinding mBinding;
    private BottomSheetBehavior mBehavior;
    private AppInfoAdapter mAdapter;

    private List<AppInfo> appInfoList;
    private List<AppInfo> tempList;
    private List<AppInfo> resultList;

    private Consumer<AppInfo> callback;

    public ChooseAppDialog(@NonNull Context context, Consumer<AppInfo> callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogChooseAppBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBehavior = BottomSheetBehavior.from((View) mBinding.getRoot().getParent());
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setDimAmount(0.4f);

        int peekHeight = (int) (DisplayUtil.getScreenHeight() * 9f / 16f);
        mBehavior.setPeekHeight(peekHeight);
        mBinding.recycler.setMinimumHeight(peekHeight - DisplayUtil.dp2px(50) - DisplayUtil.dp2px(51));

        AdHelper.init(mBinding.adView);

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
                    tempList = new ArrayList<>();
                    resultList = new ArrayList<>();
                    mAdapter = new AppInfoAdapter(getContext(), resultList);
                    mAdapter.setCallback(appInfo -> {
                        if (callback != null) {
                            callback.accept(appInfo);
                        }
                        dismiss();
                    });
                    mBinding.recycler.setAdapter(mAdapter);
                })
                .doOnComplete(() -> {
                    appInfoList = new ArrayList<>();
                    appInfoList.addAll(resultList);
                    search(mBinding.editText.getText().toString().replaceAll("\\s", ""));
                    mBinding.editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            search(s.toString().replaceAll("\\s", ""));
                        }
                    });
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newList -> {
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(resultList, newList));
                    resultList.clear();
                    resultList.addAll(tempList);
                    diffResult.dispatchUpdatesTo(mAdapter);
                    mBinding.recycler.scrollToPosition(0);
                }, Throwable::printStackTrace);
    }

    private void search(String text) {
        Observable.fromIterable(appInfoList)
                .filter(appInfo -> appInfo.label.contains(text)
                        || appInfo.labelPinyin.contains(text.toLowerCase())
                        || appInfo.sPinyin.contains(text.toLowerCase()))
                .toList()
                .subscribe(newList -> {
                    L.e(appInfoList);
                    L.e(newList);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(resultList, newList));
                    resultList.clear();
                    resultList.addAll(newList);
                    diffResult.dispatchUpdatesTo(mAdapter);
                });
    }

}
