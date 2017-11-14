package me.gavin.icon.material;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.WindowManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.base.function.Consumer;
import me.gavin.icon.material.databinding.PopChooseIconBinding;
import me.gavin.svg.model.SVG;
import me.gavin.svg.parser.SVGParser;
import me.gavin.util.L;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/13
 */
public class ChooseIconDialog extends BottomSheetDialog {

    private PopChooseIconBinding mBinding;

    private Consumer<SVG> callback;

    public ChooseIconDialog(@NonNull Context context, Consumer<SVG> callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = PopChooseIconBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setDimAmount(0.4f);

        Observable.just("action")
                .flatMap(path -> Observable.just(path)
                        .map(getContext().getAssets()::list)
                        .flatMap(Observable::fromArray)
                        .filter(s -> s.endsWith(".svg"))
                        .map(s -> String.format("%s%s",
                                TextUtils.isEmpty(path) ? "" : path + "/", s)))
                .map(getContext().getAssets()::open)
                .map(SVGParser::parse)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vectors -> mBinding.recycler.setAdapter(
                        new SVGAdapter(getContext(), vectors, svg -> {
                            L.e(svg);
                            callback.accept(svg);
                            dismiss();
                        })), L::e);
    }
}
