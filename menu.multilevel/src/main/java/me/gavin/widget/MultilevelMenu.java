package me.gavin.widget;

import android.content.ClipData;
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
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import me.gavin.util.DisplayUtil;
import me.gavin.util.DragUtils;
import me.gavin.util.L;
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

    // menu
    private Menu mMenu;

    private int mItemPadding;

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

        mItemPadding = DisplayUtil.dp2px(8);
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
        // mFloatIcon.setTint(floatIconColor);
        // mFloatIcon.setTintMode(PorterDuff.Mode.SRC_IN);
        mFloatIconPadding = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlMenu_floatIconPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));

        int menuRes = ta.getResourceId(R.styleable.MultilevelMenu_mlMenu_menu, 0);
        fromMenu(menuRes);
        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem menuItem = mMenu.getItem(i);
            if (menuItem.isVisible()) {
                ItemView itemView = new ItemView(getContext(), menuItem);
                itemView.setPadding(mItemPadding);
                itemView.setVisibility(isInEditMode() ? VISIBLE : GONE);
                addView(itemView);
//                if (menuItem.getSubMenu() != null && menuItem.getSubMenu().size() > 0) {
//                    for (int j = 0; j < menuItem.getSubMenu().size(); j++) {
//                        // TODO: 2017/11/20
//                    }
//                }
            }
        }

        ta.recycle();
    }

//    @Override
//    public LayoutParams generateLayoutParams(AttributeSet attrs) {
//        return new MarginLayoutParams(getContext(), attrs);
//    }

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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        L.e("Multilevel.onLayout");
        int diff = DisplayUtil.dp2px(8);
        int dis = DisplayUtil.dp2px(8);
//        for (int i = 0; i < getChildCount(); i++) {
//            View child = getChildAt(i);
//            if (child instanceof ItemView) {
//                ItemView itemView = (ItemView) child;
//                itemView.setCenterPoint(mFloatOutlineRect.right - mFloatRadius,
//                        mFloatOutlineRect.bottom - mFloatRadius - dis * i - dis - diff);
//                itemView.layout(l, t, r, b);
//            }
//        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ItemView) {
                ItemView itemView = (ItemView) child;
                int cx = mFloatOutlineRect.right - mFloatRadius;
                int cy = mFloatOutlineRect.bottom - mFloatRadius - dis * i - dis - diff;
                itemView.setCenterPoint(cx, cy);
                measureChild(itemView, 0, 0);
                itemView.layout(
                        mFloatOutlineRect.right - diff - child.getMeasuredWidth() + mItemPadding,
                        mFloatOutlineRect.top - dis * i - dis - child.getMeasuredHeight() * i - child.getMeasuredHeight() - mItemPadding,
                        mFloatOutlineRect.right - diff + mItemPadding,
                        mFloatOutlineRect.top - dis * i - dis - child.getMeasuredHeight() * i - mItemPadding);
            }
        }
    }

    private void fromMenu(int menuRes) {
        mMenu = new PopupMenu(getContext(), null).getMenu();
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(menuRes, mMenu);
        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem item = mMenu.getItem(i);
            if (item.getIcon() == null || item.getTitle() == null || item.getItemId() == 0) {
                throw new IllegalArgumentException("menu resource must have an icon, title, and id.");
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mFloatIcon.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            DragShadowBuilder dragShadowBuilder = new DragShadowBuilder();
            ClipData dragData = DragUtils.getClipData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startDragAndDrop(dragData, dragShadowBuilder, null, 0);
            } else {
                startDrag(dragData, dragShadowBuilder, null, 0);
            }
        }
        return true;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        L.e("MultilevelMenu: onDragEvent - " + event);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (DragUtils.isDragForMe(event.getClipDescription().getLabel())) {
                    double distance = Math.sqrt(Math.pow(mWidth - mPadding - mFloatRadius - event.getX(), 2)
                            + Math.pow(mHeight - mPadding - mFloatRadius - event.getY(), 2));
                    if (distance <= mFloatRadius) {
                        // 忽略系统设置 强制震动反馈
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        for (int i = 0; i < getChildCount(); i++) {
                            getChildAt(i).setVisibility(VISIBLE);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            case DragEvent.ACTION_DRAG_ENDED:
                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).setVisibility(GONE);
                }
                return true;
        }
        return true;
    }
}
