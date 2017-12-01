package me.gavin.app.app;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * DiffCallback
 *
 * @author gavin.xiong 2017/12/1
 */
public class DiffCallback extends DiffUtil.Callback {

    private List<AppInfo> mOldList, mNewList;//看名字

    public DiffCallback(List<AppInfo> oldList, List<AppInfo> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).packageName.equals(mNewList.get(newItemPosition).packageName);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
