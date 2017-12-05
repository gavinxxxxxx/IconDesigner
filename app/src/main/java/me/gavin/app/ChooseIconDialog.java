package me.gavin.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.base.function.Consumer;
import me.gavin.icon.designer.databinding.DialogChooseIconBinding;
import me.gavin.svg.model.SVG;
import me.gavin.svg.parser.SVGParser;

/**
 * ChooseIconDialog
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseIconDialog extends BottomSheetDialog {

    private DialogChooseIconBinding mBinding;
    private List<SVG> svgList;
    private SVGAdapter mAdapter;

    private Consumer<SVG> callback;

    public ChooseIconDialog(@NonNull Context context, Consumer<SVG> callback) {
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
                .map(getContext().getAssets()::open)
                .map(SVGParser::parse)
                .doOnSubscribe(disposable -> {
                    svgList = new ArrayList<>();
                    mAdapter = new SVGAdapter(getContext(), svgList, svg -> {
                        callback.accept(svg);
                        dismiss();
                    });
                    mBinding.recycler.setAdapter(mAdapter);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(svg -> {
                    svgList.add(svg);
                    mAdapter.notifyItemInserted(svgList.size());
                }, Throwable::printStackTrace);

//        Observable.just("gavin", "design/action")
//                .flatMap(path -> Observable.just(path)
//                        .map(getContext().getAssets()::list)
//                        .flatMap(Observable::fromArray)
//                        .filter(s -> s.endsWith(".svg"))
//                        .map(s -> String.format("%s%s", TextUtils.isEmpty(path) ? "" : path + "/", s)))
//                .map(getContext().getAssets()::open)
//                .map(SVGParser::parse)
//                .doOnSubscribe(disposable -> {
//                    svgList = new ArrayList<>();
//                    mAdapter = new SVGAdapter(getContext(), svgList, svg -> {
//                        L.e(svg);
//                        callback.accept(svg);
//                        dismiss();
//                    });
//                    mBinding.recycler.setAdapter(mAdapter);
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(svg -> {
//                    svgList.add(svg);
//                    mAdapter.notifyItemInserted(svgList.size());
//                }, L::e);
    }
}
