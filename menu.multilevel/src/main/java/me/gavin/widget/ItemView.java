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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.gavin.util.DisplayUtil;
import me.gavin.util.DragUtils;
import me.gavin.util.L;

public class ItemView extends ViewGroup {

    private MenuItem mMenuItem;

    private int mX, mY;

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
        setWillNotDraw(false);
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

        imageView = new ImageView(context);
        imageView.setImageDrawable(mFloatIcon);
        imageView.setPadding(mFloatIconPadding, mFloatIconPadding, mFloatIconPadding, mFloatIconPadding);
        imageView.setElevation(DisplayUtil.dp2px(6));
        imageView.setBackground(new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(0xFFFF0000);
                canvas.drawCircle(rect().centerX(), rect().centerY(), rect().width() / 2f, paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setOval((int) rect().left, (int) rect().top, (int) rect().right, (int) rect().bottom);
            }
        }));
        imageView.setOnDragListener(onDragListener);
        addView(imageView);
        textView = new TextView(context);
        textView.setPadding(mTitleHPadding, mTitleVPadding, mTitleHPadding, mTitleVPadding);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        textView.setTextColor(0xFFFFFFFF);
        textView.setText(mMenuItem.getTitle());
        textView.setElevation(DisplayUtil.dp2px(4));
        textView.setBackground(new ShapeDrawable(new RectShape() {
            private final int radius = DisplayUtil.dp2px(4);

            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mFloatBackgroundColor);
                canvas.drawRoundRect(rect(), radius, radius, paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setRoundRect((int) rect().left, (int) rect().top, (int) rect().right, (int) rect().bottom, radius);
            }
        }));
        addView(textView);
    }

    ImageView imageView;
    TextView textView;

    public void setCenterPoint(int x, int y) {
        this.mX = x;
        this.mY = y;
        imageView.layout(mWidth - mFloatRadius * 2, 0, mWidth, mHeight);
        measureChild(textView, 0, 0);
        textView.layout(0, mHeight / 2 - textView.getMeasuredHeight() / 2,
                textView.getMeasuredWidth(), mHeight / 2 + textView.getMeasuredHeight() / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        L.e("Item.onMeasure");
        int width = mTitleBound.width() + mTitleHPadding * 2 + mTitleMargin + mFloatRadius * 2;
        int height = Math.max(mTitleBound.height() + mTitleVPadding * 2, mFloatRadius * 2);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        L.e("Item.onSizeChanged");
        this.mWidth = w;
        this.mHeight = h;

        // outline
        mFloatOutlineRect = new Rect(mX - mFloatRadius, mY - mFloatRadius, mX + mFloatRadius, mY + mFloatRadius);

        mFloatIcon.setBounds(
                mX - mFloatRadius + mFloatIconPadding,
                mY - mFloatRadius + mFloatIconPadding,
                mX + mFloatRadius - mFloatIconPadding,
                mY + mFloatRadius - mFloatIconPadding);

        mTitleRect = new Rect(
                mX - mFloatRadius - mTitleMargin - mTitleBound.width() - mTitleHPadding * 2,
                mY - mTitleBound.height() / 2 - mTitleVPadding,
                mX - mFloatRadius - mTitleMargin,
                mY + mTitleBound.height() / 2 + mTitleVPadding);

//        setBackground(buildBackground());
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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x20800000);

//        mFloatIcon.draw(canvas);
//
//        canvas.drawRoundRect(mTitleRect.left, mTitleRect.top, mTitleRect.right, mTitleRect.bottom, 10, 10, mTitleBgPaint);
//
//        Paint.FontMetricsInt fmi = mTitlePaint.getFontMetricsInt();
//        int baseline = (mTitleRect.bottom + mTitleRect.top - fmi.bottom - fmi.top) / 2;
//        canvas.drawText(mMenuItem.getTitle().toString(), mTitleRect.centerX(), baseline, mTitlePaint);
    }

    private OnDragListener onDragListener = (v, event) -> {
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
    };

//    @Override
//    public boolean onDragEvent(DragEvent event) {
//         L.e(mMenuItem.getTitle() + ": onDragEvent - " + event);
//        switch (event.getAction()) {
//            case DragEvent.ACTION_DRAG_STARTED:
//                return DragUtils.isDragForMe(event.getClipDescription().getLabel());
//            case DragEvent.ACTION_DRAG_ENDED:
//                // unselected
//                mTitlePaint.setColor(0xFFFFFFFF);
//                return true;
//            case DragEvent.ACTION_DRAG_ENTERED:
//                // selected
//                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
//                mTitlePaint.setColor(0xFFFF0000);
//                invalidate();
//                return true;
//            case DragEvent.ACTION_DRAG_EXITED:
//                // unselected
//                mTitlePaint.setColor(0xFFFFFFFF);
//                invalidate();
//                return true;
//            case DragEvent.ACTION_DROP:
//                // TODO: 2017/11/20
//                Toast.makeText(getContext(), mMenuItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
//                return true;
//        }
//        return false;
//    }

}
