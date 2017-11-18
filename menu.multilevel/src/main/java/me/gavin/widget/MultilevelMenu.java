package me.gavin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ViewGroup;

import me.gavin.util.DisplayUtil;
import me.gavin.widget.menu.multilevel.R;

/**
 * 多级菜单
 *
 * @author gavin.xiong 2017/11/18
 */
public class MultilevelMenu extends ViewGroup {

    private int mWidth, mHeight;
    private int mPadding;

    // float
    private Rect mFloatOutlineRect;
    private int mFloatRadius;
    private int mFloatBackgroundColor;
    private Drawable mFloatIcon;
    private int mFloatIconPadding;

    public MultilevelMenu(Context context) {
        this(context, null);
    }

    public MultilevelMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultilevelMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mPadding = DisplayUtil.dp2px(16);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultilevelMenu);
        mFloatRadius = ta.getDimensionPixelOffset(R.styleable.MultilevelMenu_mlMenu_floatRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
        mFloatBackgroundColor = ta.getColor(R.styleable.MultilevelMenu_mlMenu_floatBackgroundColor, 0xFF303030);
        mFloatIcon = ta.getDrawable(R.styleable.MultilevelMenu_mlMenu_floatIcon);
        if (mFloatIcon == null) {
            mFloatIcon = getResources().getDrawable(R.drawable.ic_apps_black_24dp);
        }
        int floatIconColor = ta.getColor(R.styleable.MultilevelMenu_mlMenu_floatIconColor, 0xFFFFFFFF);
        mFloatIcon.setColorFilter(floatIconColor, PorterDuff.Mode.SRC_IN);
        // mIcon.setTint(floatIconColor);
        // mIcon.setTintMode(PorterDuff.Mode.SRC_IN);
        mFloatIconPadding = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlMenu_floatIconPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        ta.recycle();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(
                MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(widthMeasureSpec)
                        : DisplayUtil.getScreenWidth(),
                MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(heightMeasureSpec)
                        : DisplayUtil.getScreenHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        this.mWidth = w;
        this.mHeight = h;

        // outline
        mFloatOutlineRect = new Rect(
                mWidth - mPadding - mFloatRadius * 2,
                mHeight - mPadding - mFloatRadius * 2,
                mWidth - mPadding,
                mHeight - mPadding);

        mFloatIcon.setBounds(mFloatOutlineRect.left + mFloatIconPadding,
                mFloatOutlineRect.top + mFloatIconPadding,
                mFloatOutlineRect.right - mFloatIconPadding,
                mFloatOutlineRect.bottom - mFloatIconPadding);

        setBackground(buildBackground());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            child.layout(0, 0, 200, 200);
//        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                double distance = Math.sqrt(Math.pow(mWidth - mPadding - mFloatRadius - event.getX(), 2)
                        + Math.pow(mHeight - mPadding - mFloatRadius - event.getY(), 2));
                if (distance <= mFloatRadius) {
                    // 忽略系统设置 强制震动反馈
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }
}
