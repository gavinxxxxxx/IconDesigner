package me.gavin.app;

import android.support.v7.util.DiffUtil;

import java.util.List;

import me.gavin.svg.model.SVG;

/**
 * DiffCallback
 *
 * @author gavin.xiong 2017/12/1
 */
public class DiffCallback extends DiffUtil.Callback {

    private List<SVG> mOldList, mNewList;

    DiffCallback(List<SVG> oldList, List<SVG> newList) {
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
        return mOldList.get(oldItemPosition) == mNewList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }
}
