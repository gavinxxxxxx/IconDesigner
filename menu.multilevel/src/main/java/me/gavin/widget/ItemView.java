package me.gavin.widget;

import android.content.Context;
import android.os.Build;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;

import me.gavin.util.DragUtils;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/21
 */
public class ItemView extends ImageView {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private MenuItem mMenuItem;
    private int mLevel;
    private int mOrientation;
    private int mMode;
    private Callback mCallback;

    public ItemView(Context context) {
        super(context);
    }

    public void setMenuItem(MenuItem mMenuItem) {
        this.mMenuItem = mMenuItem;
        setImageDrawable(mMenuItem.getIcon());
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

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && mMode == MultilevelMenu.MODE_B) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startDragAndDrop(DragUtils.getClipData(), new DragShadowBuilder(), null, 0);
            } else {
                startDrag(DragUtils.getClipData(), new DragShadowBuilder(), null, 0);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        // L.e(mMenuItem.getTitle() + ": onDragEvent - " + event);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return DragUtils.isDragForMe(event.getClipDescription().getLabel());
            case DragEvent.ACTION_DRAG_ENDED:
                // unselected
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                // selected
                if (getVisibility() == VISIBLE) {
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    if (mCallback != null) {
                        mCallback.onEntered(this, mMenuItem);
                    }
                    return true;
                } else {
                    return false;
                }
            case DragEvent.ACTION_DRAG_EXITED:
                // unselected
                if (getVisibility() == VISIBLE) {
                    if (mCallback != null) {
                        mCallback.onExited(this, mMenuItem);
                    }
                    return true;
                } else {
                    return false;
                }
            case DragEvent.ACTION_DROP:
                if (getVisibility() == VISIBLE) {
                    if (mCallback != null) {
                        mCallback.onDrop(this, mMenuItem);
                    }
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }

    public interface Callback {
        void onEntered(ItemView v, MenuItem item);

        void onExited(ItemView v, MenuItem item);

        void onDrop(ItemView v, MenuItem item);
    }
}
