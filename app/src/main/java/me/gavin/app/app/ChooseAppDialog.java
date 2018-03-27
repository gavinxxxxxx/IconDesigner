package me.gavin.app.app;

import android.content.Context;
import android.content.Intent;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.gavin.base.function.Consumer;
import me.gavin.icon.designer.databinding.DialogChooseAppBinding;
import me.gavin.util.AdHelper;
import me.gavin.util.DisplayUtil;

/**
 * ChooseAppDialog
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseAppDialog extends BottomSheetDialog {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private DialogChooseAppBinding mBinding;
    private AppInfoAdapter mAdapter;

    private List<AppInfo> allList;
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

        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setDimAmount(0.4f);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) mBinding.getRoot().getParent());
        mBehavior.setPeekHeight(DisplayUtil.getScreenHeight());
        mBinding.recycler.setMinimumHeight(DisplayUtil.getScreenHeight());

        AdHelper.loadGoogle(mBinding.container, AdHelper.UNIT_ID);

        PackageManager pm = getContext().getPackageManager();
        Observable.fromIterable(pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER), 0))
                .map(resolve -> AppInfo.from(pm, resolve.activityInfo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    resultList = new ArrayList<>();
                    mAdapter = new AppInfoAdapter(getContext(), resultList);
                    mBinding.recycler.setAdapter(mAdapter);
                    mAdapter.setCallback(appInfo -> {
                        if (callback != null) {
                            callback.accept(appInfo);
                        }
                        dismiss();
                    });
                })
                .doOnComplete(() -> {
                    allList = new ArrayList<>();
                    allList.addAll(resultList);
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
                .subscribe(appInfo -> {
                    resultList.add(appInfo);
                    Collections.sort(resultList, (o1, o2) -> o1.labelPinyin.compareTo(o2.labelPinyin));
                    mAdapter.notifyItemInserted(resultList.indexOf(appInfo));
                    mBinding.recycler.smoothScrollToPosition(0);
                }, Throwable::printStackTrace);
    }

    private void search(String text) {
        Observable.fromIterable(allList)
                .filter(appInfo -> appInfo.label.contains(text)
                        || appInfo.labelPinyin.contains(text.toLowerCase())
                        || appInfo.sPinyin.contains(text.toLowerCase()))
                .toList()
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(newList -> {
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(resultList, newList));
                    resultList.clear();
                    resultList.addAll(newList);
                    diffResult.dispatchUpdatesTo(mAdapter);
                });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mCompositeDisposable.dispose();
    }
}
