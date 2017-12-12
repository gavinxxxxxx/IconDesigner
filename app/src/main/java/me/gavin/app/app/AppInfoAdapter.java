package me.gavin.app.app;

import android.content.Context;

import java.util.List;

import me.gavin.base.function.Consumer;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.icon.designer.R;
import me.gavin.icon.designer.databinding.ItemAppBinding;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/30
 */
public class AppInfoAdapter extends RecyclerAdapter<AppInfo, ItemAppBinding> {

    private Consumer<AppInfo> callback;

    AppInfoAdapter(Context context, List<AppInfo> list) {
        super(context, list, R.layout.item_app);
    }

    public void setCallback(Consumer<AppInfo> callback) {
        this.callback = callback;
    }

    @Override
    protected void onBind(RecyclerHolder<ItemAppBinding> holder, AppInfo appInfo, int position) {
        holder.binding.setItem(appInfo);
        holder.binding.executePendingBindings();
        holder.binding.item.setOnClickListener(v -> {
            if (callback != null) {
                callback.accept(appInfo);
            }
        });
    }
}
