package me.gavin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.MenuItem;
import android.view.View;

import me.gavin.util.DisplayUtil;
import me.gavin.util.L;

public class ItemView extends View {

    private MenuItem mMenuItem;

    private int mWidth, mHeight;
    private int mPadding;

    // float
    private Rect mFloatOutlineRect;
    private int mFloatRadius;
    private int mFloatBackgroundColor;
    private Drawable mFloatIcon;
    private int mFloatIconPadding;

    public ItemView(Context context, MenuItem menuItem) {
        super(context);
        this.mMenuItem = menuItem;
        mFloatIcon = mMenuItem.getIcon();
        setElevation(DisplayUtil.dp2px(6));

        mPadding = DisplayUtil.dp2px(16);
        mFloatRadius = DisplayUtil.dp2px(28);
        mFloatBackgroundColor = 0xFF303030;
        mFloatIconPadding = DisplayUtil.dp2px(16);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(widthMeasureSpec)
                        : DisplayUtil.dp2px(256),
                MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(heightMeasureSpec)
                        : DisplayUtil.dp2px(256));
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        this.mWidth = w;
        this.mHeight = h;

        // outline
        mFloatOutlineRect = new Rect(0, 0, mWidth, mHeight);

        mFloatIcon.setBounds(
                mFloatOutlineRect.left + mFloatIconPadding,
                mFloatOutlineRect.top + mFloatIconPadding,
                mFloatOutlineRect.right - mFloatIconPadding,
                mFloatOutlineRect.bottom - mFloatIconPadding);

        setBackground(buildBackground());
    }

    private Drawable buildBackground() {
        return new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mFloatBackgroundColor);
                canvas.drawOval(mFloatOutlineRect.left, mFloatOutlineRect.top, mFloatOutlineRect.right, mFloatOutlineRect.bottom, paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setOval(mFloatOutlineRect);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mFloatIcon.draw(canvas);
    }
}
