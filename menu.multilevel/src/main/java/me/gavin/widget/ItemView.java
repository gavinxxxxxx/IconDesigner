package me.gavin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import me.gavin.util.DisplayUtil;
import me.gavin.util.DragUtils;
import me.gavin.util.L;

public class ItemView extends View {

    private MenuItem mMenuItem;

    // float
    private int mWidth, mHeight;
    private Rect mFloatOutlineRect;

    // icon
    private int mFloatRadius;
    private int mFloatBackgroundColor;
    private Drawable mFloatIcon;
    private int mFloatIconPadding;

    // text;
    private Rect mTitleRect;
    private Paint mTitleBgPaint;
    private Paint mTitlePaint;
    private Rect mTitleBound = new Rect();
    private int mTitleHPadding;
    private int mTitleVPadding;
    private int mTitleMargin;

    public ItemView(Context context, MenuItem menuItem) {
        super(context);
        this.mMenuItem = menuItem;
        mFloatIcon = mMenuItem.getIcon();
        mFloatIcon.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
        setElevation(DisplayUtil.dp2px(6));

        mFloatRadius = DisplayUtil.dp2px(20);
        mFloatBackgroundColor = 0xFF303030;
        mFloatIconPadding = DisplayUtil.dp2px(8);

        mTitleBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitleBgPaint.setColor(0xFF303030);
        mTitleBgPaint.setShadowLayer(5, 1, 3, 0xFFFF0000);
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setColor(0xFFFFFFFF);
        mTitlePaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14f, getResources().getDisplayMetrics()));
        mTitlePaint.getTextBounds(mMenuItem.getTitle().toString(), 0, mMenuItem.getTitle().length(), mTitleBound);
        mTitleHPadding = DisplayUtil.dp2px(8);
        mTitleVPadding = DisplayUtil.dp2px(6);
        mTitleMargin = DisplayUtil.dp2px(16);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mTitleBound.width() + mTitleHPadding * 2 + mTitleMargin + mFloatRadius * 2;
        int height = Math.max(mTitleBound.height() + mTitleVPadding * 2, mFloatRadius * 2);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        this.mWidth = w;
        this.mHeight = h;

        // outline
        mFloatOutlineRect = new Rect(mWidth - mFloatRadius * 2, 0, mWidth, mHeight);

        mFloatIcon.setBounds(
                mWidth - mFloatRadius * 2 + mFloatIconPadding,
                mFloatIconPadding,
                mWidth - mFloatIconPadding,
                mHeight - mFloatIconPadding);

        mTitleRect = new Rect(
                0,
                mHeight / 2 - mTitleBound.height() / 2 - mTitleVPadding,
                mTitleBound.width() + mTitleHPadding * 2,
                mHeight / 2 + mTitleBound.height() / 2 + mTitleVPadding);

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

        canvas.drawRoundRect(mTitleRect.left, mTitleRect.top, mTitleRect.right, mTitleRect.bottom, 10, 10, mTitleBgPaint);

        Paint.FontMetricsInt fmi = mTitlePaint.getFontMetricsInt();
        int baseline = (mTitleRect.bottom + mTitleRect.top - fmi.bottom - fmi.top) / 2;
        canvas.drawText(mMenuItem.getTitle().toString(), mTitleRect.centerX(), baseline, mTitlePaint);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        L.e(mMenuItem.getTitle() + ": onDragEvent - " + event);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return DragUtils.isDragForMe(event.getClipDescription().getLabel());
            case DragEvent.ACTION_DRAG_ENDED:
                // unselected
                mTitlePaint.setColor(0xFFFFFFFF);
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                // selected
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                mTitlePaint.setColor(0xFFFF0000);
                invalidate();
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                // unselected
                mTitlePaint.setColor(0xFFFFFFFF);
                invalidate();
                return true;
            case DragEvent.ACTION_DROP:
                // TODO: 2017/11/20
                Toast.makeText(getContext(), mMenuItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

}
