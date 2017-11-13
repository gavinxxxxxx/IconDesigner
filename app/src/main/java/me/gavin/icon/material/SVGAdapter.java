package me.gavin.icon.material;

import android.content.Context;

import java.util.List;

import me.gavin.base.function.Consumer;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.icon.material.databinding.ItemIconBinding;
import me.gavin.svg.model.SVG;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/11
 */
class SVGAdapter extends RecyclerAdapter<SVG, ItemIconBinding> {

    private Consumer<SVG> callback;

    SVGAdapter(Context context, List<SVG> list, Consumer<SVG> callback) {
        super(context, list, R.layout.item_icon);
        this.callback = callback;
    }

    @Override
    protected void onBind(RecyclerHolder<ItemIconBinding> holder, SVG svg, int position) {
        holder.binding.svg.set(svg);
        holder.binding.item.setOnClickListener(v -> callback.accept(svg));
    }

}
