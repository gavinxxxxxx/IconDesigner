package me.gavin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.gavin.util.DisplayUtil;
import me.gavin.util.DragUtils;

public class ItemView extends ViewGroup {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private MenuItem mMenuItem;
    private int mLevel;
    private int mOrientation;
    private Callback mCallback;

    // float
    private int mWidth, mHeight;
    private int mPadding;
    private int mMargin;

    // icon
    private ImageView mImageView;
    private int mIconRadius;
    private int mIconBackgroundColor;

    // title;
    private TextView mTextView;

    public ItemView(Context context, MenuItem menuItem, int level, int orientation) {
        super(context);
        this.mMenuItem = menuItem;
        this.mLevel = level;
        this.mOrientation = orientation;

        mIconRadius = DisplayUtil.dp2px(20);
        mIconBackgroundColor = 0xFF303030;

        mMargin = DisplayUtil.dp2px(16);
        mPadding = DisplayUtil.dp2px(8);

        mImageView = new ImageView(context);
        mImageView.setImageDrawable(mMenuItem.getIcon());
        int iconPadding = DisplayUtil.dp2px(8);
        mImageView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        mImageView.setElevation(DisplayUtil.dp2px(6));
        mImageView.setBackground(new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mIconBackgroundColor);
                canvas.drawCircle(rect().centerX(), rect().centerY(), rect().width() / 2f, paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setOval((int) rect().left, (int) rect().top, (int) rect().right, (int) rect().bottom);
            }
        }));
        mImageView.setOnDragListener(onDragListener);
        addView(mImageView);
        mTextView = new TextView(context);
        int hPadding = DisplayUtil.dp2px(8);
        int vPadding = DisplayUtil.dp2px(5);
//        mTextView.setPadding(hPadding, vPadding, hPadding, vPadding);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        mTextView.setTextColor(0xFFFFFFFF);
//        mTextView.setText(mMenuItem.getTitle());
        mTextView.setElevation(DisplayUtil.dp2px(4));
        mTextView.setBackground(new ShapeDrawable(new RectShape() {
            private final int radius = DisplayUtil.dp2px(2);

            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mIconBackgroundColor);
                canvas.drawRoundRect(rect(), radius, radius, paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setRoundRect((int) rect().left, (int) rect().top, (int) rect().right, (int) rect().bottom, radius);
            }
        }));
        addView(mTextView);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public int getPadding() {
        return mPadding;
    }

    public Point getCenterPoint() {
        return new Point(getRight() - mPadding - mIconRadius, getBottom() - mPadding - mIconRadius);
    }

    public int getLevel() {
        return mLevel;
    }

    public int getOrientation() {
        return mOrientation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(mTextView, widthMeasureSpec, heightMeasureSpec);
        int width = mTextView.getMeasuredWidth() + mMargin + mIconRadius * 2 + mPadding * 2;
        int height = Math.max(mTextView.getMeasuredHeight(), mIconRadius * 2) + mPadding * 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mImageView.layout(mWidth - mIconRadius * 2 - mPadding, mPadding, mWidth - mPadding, mHeight - mPadding);
        mTextView.layout(mPadding, mHeight / 2 - mTextView.getMeasuredHeight() / 2,
                mTextView.getMeasuredWidth() + mPadding, mHeight / 2 + mTextView.getMeasuredHeight() / 2);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        Paint.FontMetricsInt fmi = mTitlePaint.getFontMetricsInt();
//        int baseline = (mTitleRect.bottom + mTitleRect.top - fmi.bottom - fmi.top) / 2;
//        canvas.drawText(mMenuItem.getTitle().toString(), mTitleRect.centerX(), baseline, mTitlePaint);
//    }

    private OnDragListener onDragListener = (v, event) -> {
        // L.e(mMenuItem.getTitle() + ": onDragEvent - " + event);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return DragUtils.isDragForMe(event.getClipDescription().getLabel());
            case DragEvent.ACTION_DRAG_ENDED:
                // unselected
                mTextView.setVisibility(VISIBLE);
                mTextView.setTextColor(0xFFFFFFFF);
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                // selected
                 performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                if (mMenuItem.getSubMenu() != null && mMenuItem.getSubMenu().size() > 0) {
                    mTextView.setVisibility(GONE);
                } else {
                    mTextView.setTextColor(0xFFFF0000);
                }
                if (mCallback != null) {
                    mCallback.onEntered(this, mMenuItem);
                }
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                // unselected
                mTextView.setVisibility(VISIBLE);
                mTextView.setTextColor(0xFFFFFFFF);
                if (mCallback != null) {
                    mCallback.onExited(this, mMenuItem);
                }
                return true;
            case DragEvent.ACTION_DROP:
                if (mCallback != null) {
                    mCallback.onDrop(this, mMenuItem);
                }
                return true;
        }
        return false;
    };

    public interface Callback {
        void onEntered(ItemView v, MenuItem item);

        void onExited(ItemView v, MenuItem item);

        void onDrop(ItemView v, MenuItem item);
    }

}
