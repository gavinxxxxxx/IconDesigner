package me.gavin.widget;

import android.content.Context;
import android.view.MenuItem;
import android.widget.ImageView;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/21
 */
public class ItemView2 extends ImageView {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private MenuItem mMenuItem;
    private int mLevel;
    private int mOrientation;
    private Callback mCallback;

    public ItemView2(Context context) {
        super(context);
    }

    public void setMenuItem(MenuItem mMenuItem) {
        this.mMenuItem = mMenuItem;
        setImageDrawable(mMenuItem.getIcon());
    }

    public MenuItem getMenuItem() {
        return mMenuItem;
    }

    public void setOrientation(int mOrientation) {
        this.mOrientation = mOrientation;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public interface Callback {
        void onEntered(ItemView2 v, MenuItem item);

        void onExited(ItemView2 v, MenuItem item);

        void onDrop(ItemView2 v, MenuItem item);
    }
}
