package me.gavin.app;

import android.content.Context;
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
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.gavin.base.function.Consumer;
import me.gavin.icon.designer.databinding.DialogChooseIconBinding;
import me.gavin.svg.model.SVG;
import me.gavin.svg.parser.SVGParser;
import me.gavin.util.AdHelper;
import me.gavin.util.DisplayUtil;

/**
 * ChooseIconDialog
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseIconDialog extends BottomSheetDialog {

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private DialogChooseIconBinding mBinding;
    private List<SVG> allList;
    private List<SVG> resultList;
    private SVGAdapter mAdapter;

    private Consumer<SVG> callback;

    ChooseIconDialog(@NonNull Context context, Consumer<SVG> callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DialogChooseIconBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setDimAmount(0.4f);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) mBinding.getRoot().getParent());
        mBehavior.setPeekHeight(DisplayUtil.getScreenHeight());
        mBinding.recycler.setMinimumHeight(DisplayUtil.getScreenHeight());

        AdHelper.init(mBinding.adView);

        Observable.just("design")
                .flatMap(dir -> Observable.just(dir)
                        .map(getContext().getAssets()::list)
                        .flatMap(Observable::fromArray)
                        .filter(s -> !"aaa".equals(s))
                        .map(s -> dir + "/" + s))
                .flatMap(dir -> Observable.just(dir)
                        .map(getContext().getAssets()::list)
                        .flatMap(Observable::fromArray)
                        .filter(s -> s.endsWith(".svg"))
                        .map(s -> dir + "/" + s))
                .map(s -> {
                    SVG svg = SVGParser.parse(getContext().getAssets().open(s));
                    if (s.contains("/ic_") && s.contains("_24px.svg")) {
                        svg.name = s.substring(s.indexOf("/ic_") + 4, s.lastIndexOf("_24px.svg"));
                    } else {
                        svg.name = s;
                    }
                    return svg;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    resultList = new ArrayList<>();
                    mAdapter = new SVGAdapter(getContext(), resultList, svg -> {
                        callback.accept(svg);
                        dismiss();
                    });
                    mBinding.recycler.setAdapter(mAdapter);
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
                .subscribe(svg -> {
                    resultList.add(svg);
                    mAdapter.notifyItemInserted(resultList.size());
                }, Throwable::printStackTrace);
    }


    private void search(String text) {
        Observable.fromIterable(allList)
                .filter(svg -> svg.name.contains(text))
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
