package me.gavin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
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

import java.util.ArrayList;
import java.util.List;

import me.gavin.util.DragUtils;
import me.gavin.widget.menu.multilevel.R;

/**
 * 多级菜单
 *
 * @author gavin.xiong 2017/11/18
 */
public class MultilevelMenu extends ViewGroup {

    public static final int MODE_A = 0;
    public static final int MODE_B = 1;

    private int mWidth, mHeight;

    private int mMode;
    private boolean isMenuShow;
    private boolean menuCloseFlag;

    // 原点
    private int mOriginMargin;
    private Rect mOriginOutlineRect;
    private int mOriginRadius;
    private int mOriginBgColor;
    private Drawable mOriginIcon;
    private int mOriginPadding;

    // MenuItem
    private int mMenuItemMargin;
    private int mMenuItemRadius;
    private int mMenuItemBgColor;
    private int mMenuItemElevation;
    private int mMenuItemPadding;
    private int mMenuItemIconColor;

    // menu
    private Menu mVerticalMenu, mHorizontalMenu;

    private SparseArray<List<ItemView>> mMenuItemArray = new SparseArray<>();

    private Consumer<MenuItem> onMenuItemSelectedListener;

    private String mCurrTitle = "";
    private Paint mTitlePaint;

    public MultilevelMenu(Context context) {
        this(context, null);
    }

    public MultilevelMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultilevelMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(0xfff5f5f5);
        mTitlePaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mTitlePaint.setShadowLayer(2, 2, 2, 0xff2b2b2b);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultilevelMenu);

        mMode = ta.getInt(R.styleable.MultilevelMenu_mlm_mode, MODE_A);

        mOriginMargin = ta.getDimensionPixelOffset(R.styleable.MultilevelMenu_mlm_originMargin,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        mOriginRadius = ta.getDimensionPixelOffset(R.styleable.MultilevelMenu_mlm_originRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
        mOriginBgColor = ta.getColor(R.styleable.MultilevelMenu_mlm_originBgColor, 0xFF303030);
        mOriginPadding = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_originPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        mOriginIcon = ta.getDrawable(R.styleable.MultilevelMenu_mlm_originIcon);
        if (mOriginIcon == null) {
            mOriginIcon = getResources().getDrawable(R.drawable.ic_gesture_24dp);
        }
        int floatIconColor = ta.getColor(R.styleable.MultilevelMenu_mlm_originIconColor, 0xFFFFFFFF);
        mOriginIcon.setColorFilter(floatIconColor, PorterDuff.Mode.SRC_IN);
        // mFloatIcon.setTint(floatIconColor);
        // mFloatIcon.setTintMode(PorterDuff.Mode.SRC_IN);

        mMenuItemMargin = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemMargin,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mMenuItemRadius = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        mMenuItemBgColor = ta.getColor(R.styleable.MultilevelMenu_mlm_menuItemBgColor, 0xFF303030);
        mMenuItemElevation = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemElevation,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()));
        mMenuItemPadding = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mMenuItemIconColor = ta.getColor(R.styleable.MultilevelMenu_mlm_menuItemIconColor, 0xFFFFFFFF);

        int menuRes = ta.getResourceId(R.styleable.MultilevelMenu_mlm_menuVertical, 0);
        if (menuRes != 0) {
            mVerticalMenu = new PopupMenu(getContext(), null).getMenu();
            fromMenu(mVerticalMenu, menuRes);
        }
        menuRes = ta.getResourceId(R.styleable.MultilevelMenu_mlm_menuHorizontal, 0);
        if (menuRes != 0) {
            mHorizontalMenu = new PopupMenu(getContext(), null).getMenu();
            fromMenu(mHorizontalMenu, menuRes);
        }

        ta.recycle();
    }

    public void setOnMenuItemSelectedListener(Consumer<MenuItem> listener) {
        onMenuItemSelectedListener = listener;
    }

    private ItemView.Callback onItemCallback = new ItemView.Callback() {
        @Override
        public void onEntered(ItemView v, MenuItem item) {
            mCurrTitle = item.getTitle().toString();
            invalidate();
            for (int i = 0; i < getChildCount(); i++) {
                ItemView child = ((ItemView) getChildAt(i));
                if (child.getLevel() > v.getLevel()) {
                    child.setVisibility(GONE);
                }
            }
            showMenuItem(item.getSubMenu());
        }

        @Override
        public void onExited(ItemView v, MenuItem item) {
            mCurrTitle = "";
            invalidate();
        }

        @Override
        public void onDrop(ItemView v, MenuItem item) {
            if (onMenuItemSelectedListener != null) {
                onMenuItemSelectedListener.accept(item);
            }
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        this.mWidth = w;
        this.mHeight = h;

        // outline
        mOriginOutlineRect = new Rect(
                mWidth - mOriginMargin - mOriginRadius * 2,
                mHeight - mOriginMargin - mOriginRadius * 2,
                mWidth - mOriginMargin,
                mHeight - mOriginMargin);

        mOriginIcon.setBounds(
                mOriginOutlineRect.left + mOriginPadding,
                mOriginOutlineRect.top + mOriginPadding,
                mOriginOutlineRect.right - mOriginPadding,
                mOriginOutlineRect.bottom - mOriginPadding);

        layoutMenu(mVerticalMenu, mOriginOutlineRect.centerX(), mOriginOutlineRect.centerY() - mOriginRadius + mMenuItemRadius, ItemView.VERTICAL, 0);
        layoutMenu(mHorizontalMenu, mOriginOutlineRect.centerX() - mOriginRadius + mMenuItemRadius, mOriginOutlineRect.centerY(), ItemView.HORIZONTAL, 0);

        setBackground(buildBackground());
    }

    private Drawable buildBackground() {
        return new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mOriginBgColor);
                canvas.drawOval(mOriginOutlineRect.left, mOriginOutlineRect.top, mOriginOutlineRect.right, mOriginOutlineRect.bottom, paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setOval(mOriginOutlineRect);
            }
        });
    }

    private Drawable buildMenuItemBackground() {
        return new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(mMenuItemBgColor);
                canvas.drawOval(rect(), paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setOval((int) rect().left, (int) rect().top, (int) rect().right, (int) rect().bottom);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // do nothing
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mOriginIcon.draw(canvas);
        canvas.drawText(mCurrTitle, 20, mHeight - 20, mTitlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            double distance = Math.sqrt(Math.pow(mWidth - mOriginMargin - mOriginRadius - event.getX(), 2)
                    + Math.pow(mHeight - mOriginMargin - mOriginRadius - event.getY(), 2));
            if (mMode == MODE_A && distance <= mOriginRadius
                    || mMode == MODE_B && (distance <= mOriginRadius || isMenuShow)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startDragAndDrop(DragUtils.getClipData(), new DragShadowBuilder(), null, 0);
                } else {
                    startDrag(DragUtils.getClipData(), new DragShadowBuilder(), null, 0);
                }
                menuCloseFlag = isMenuShow && distance <= mOriginRadius;
                isMenuShow = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        L.e("MultilevelMenu: onDragEvent - " + event);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (DragUtils.isDragForMe(event.getClipDescription().getLabel())) {
                    showMenuItem(mVerticalMenu);
                    showMenuItem(mHorizontalMenu);
                    return true;
                } else {
                    return false;
                }
            case DragEvent.ACTION_DRAG_ENTERED:
                // 忽略系统设置 强制震动反馈
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                break;
            case DragEvent.ACTION_DROP:
                if (mMode == MODE_B && menuCloseFlag) {
                    for (int i = 0; i < getChildCount(); i++) {
                        getChildAt(i).setVisibility(GONE);
                    }
                    isMenuShow = false;
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                mCurrTitle = "";
                menuCloseFlag = false;
                if (mMode == MODE_A) {
                    for (int i = 0; i < getChildCount(); i++) {
                        getChildAt(i).setVisibility(GONE);
                    }
                    isMenuShow = false;
                }
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                if (mMode == MODE_B && menuCloseFlag) {
                    double distance = Math.sqrt(Math.pow(mWidth - mOriginMargin - mOriginRadius - event.getX(), 2)
                            + Math.pow(mHeight - mOriginMargin - mOriginRadius - event.getY(), 2));
                    if (distance > mOriginRadius) {
                        menuCloseFlag = false;
                    }
                }
                return true;
        }
        return true;
    }

    private void fromMenu(Menu menu, int menuRes) {
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(menuRes, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() == null || item.getTitle() == null || item.getItemId() == 0) {
                throw new IllegalArgumentException("menu resource must have an icon, title, and id.");
            }
        }
    }

    private void layoutMenu(Menu menu, int originX, int originY, int orientation, int level) {
        if (menu == null) return;
        List<ItemView> itemViewList = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isVisible()) {
                ItemView itemView = new ItemView(getContext());
                itemView.setMode(mMode);
                itemView.setId(menuItem.getItemId());
                itemView.setBackground(buildMenuItemBackground());
                itemView.setElevation(mMenuItemElevation);
                itemView.setColorFilter(mMenuItemIconColor, PorterDuff.Mode.SRC_IN);
                itemView.setPadding(mMenuItemPadding, mMenuItemPadding, mMenuItemPadding, mMenuItemPadding);
                itemView.setMenuItem(menuItem);
                itemView.setLevel(level);
                itemView.setOrientation(orientation);
                itemView.setCallback(onItemCallback);
                itemView.setVisibility(GONE);
                addView(itemView);
                itemViewList.add(itemView);

                Point center = layout(itemView, originX, originY, position);
                layoutMenu(menuItem.getSubMenu(), center.x, center.y, orientation ^ ItemView.VERTICAL, level + 1);

                position++;
            }
        }
        mMenuItemArray.append(level, itemViewList);
    }

    private void showMenuItem(Menu menu) {
        if (menu == null || menu.size() == 0) return;
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isVisible()) {
                View child = findViewById(menuItem.getItemId());
                bringChildToFront(child); // 置顶层级以响应事件
                child.setVisibility(VISIBLE);
            }
        }
    }

    private Point layout(ItemView itemView, int originX, int originY, int position) {
        measureChild(itemView, 0, 0);
        Point center = new Point();
        if (itemView.getOrientation() == ItemView.HORIZONTAL) {
            center.set(originX - mMenuItemRadius - mMenuItemMargin * (position + 1) - mMenuItemRadius * 2 * position - mMenuItemRadius, originY);
        } else if (itemView.getOrientation() == ItemView.VERTICAL) {
            center.set(originX, originY - mMenuItemRadius - mMenuItemMargin * (position + 1) - mMenuItemRadius * 2 * position - mMenuItemRadius);
        }
        itemView.layout(center.x - mMenuItemRadius, center.y - mMenuItemRadius, center.x + mMenuItemRadius, center.y + mMenuItemRadius);
        return center;
    }
}
