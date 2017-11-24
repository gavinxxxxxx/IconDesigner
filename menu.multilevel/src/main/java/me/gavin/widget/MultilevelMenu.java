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
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
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
 * @todo Behavior
 */
public class MultilevelMenu extends ViewGroup {

    private int mWidth, mHeight;

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

    private int mCurrLevel = 0;

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
            initMenu(mVerticalMenu, menuRes);
        }
        menuRes = ta.getResourceId(R.styleable.MultilevelMenu_mlm_menuHorizontal, 0);
        if (menuRes != 0) {
            mHorizontalMenu = new PopupMenu(getContext(), null).getMenu();
            initMenu(mHorizontalMenu, menuRes);
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

            for (int i = v.getLevel() + 1; i <= mCurrLevel; i++) {
                List<ItemView> itemViews = mMenuItemArray.get(i);
                if (itemViews != null && !itemViews.isEmpty()) {
                    for (ItemView itemView : itemViews) {
                        removeView(itemView);
                    }
                }
                mMenuItemArray.delete(i);
            }


            SubMenu subMenu = item.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                mCurrLevel = v.getLevel() + 1;
                List<ItemView> sub = new ArrayList<>();
                int position = 0;
                for (int i = 0; i < subMenu.size(); i++) {
                    MenuItem menuItem = subMenu.getItem(i);
                    if (menuItem.isVisible()) {
                        ItemView itemView = new ItemView(getContext());
                        itemView.setBackground(buildMenuItemBackground());
                        itemView.setElevation(mMenuItemElevation);
                        itemView.setColorFilter(mMenuItemIconColor, PorterDuff.Mode.SRC_IN);
                        itemView.setPadding(mMenuItemPadding, mMenuItemPadding, mMenuItemPadding, mMenuItemPadding);
                        itemView.setMenuItem(menuItem);
                        itemView.setLevel(mCurrLevel + 1);
                        itemView.setOrientation(v.getOrientation() ^ ItemView.VERTICAL);
                        itemView.setCallback(onItemCallback);
                        addView(itemView);
                        sub.add(itemView);

                        layout(itemView, (v.getRight() + v.getLeft()) / 2,
                                (v.getTop() + v.getBottom()) / 2, position);
                        position++;
                    }
                }
                mMenuItemArray.append(mCurrLevel, sub);
            }
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

    private void initMenu(Menu menu, int menuRes) {
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(menuRes, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() == null || item.getTitle() == null || item.getItemId() == 0) {
                throw new IllegalArgumentException("menu resource must have an icon, title, and id.");
            }
        }
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
            if (distance <= mOriginRadius) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startDragAndDrop(DragUtils.getClipData(), new DragShadowBuilder(), null, 0);
                } else {
                    startDrag(DragUtils.getClipData(), new DragShadowBuilder(), null, 0);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        // L.e("MultilevelMenu: onDragEvent - " + event);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (DragUtils.isDragForMe(event.getClipDescription().getLabel())) {
                    // 忽略系统设置 强制震动反馈
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    showRootMenu(mVerticalMenu, ItemView.VERTICAL);
                    showRootMenu(mHorizontalMenu, ItemView.HORIZONTAL);
                    return true;
                } else {
                    return false;
                }
            case DragEvent.ACTION_DRAG_ENDED:
                mCurrTitle = "";
                invalidate();
                removeAllViews();
                mMenuItemArray.clear();
                return true;
        }
        return true;
    }

    private void showRootMenu(Menu menu, int orientation) {
        if (menu != null) {
            List<ItemView> root = new ArrayList<>();
            int position = 0;
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                if (menuItem.isVisible()) {
                    ItemView itemView = new ItemView(getContext());
                    itemView.setBackground(buildMenuItemBackground());
                    itemView.setElevation(mMenuItemElevation);
                    itemView.setColorFilter(mMenuItemIconColor, PorterDuff.Mode.SRC_IN);
                    itemView.setPadding(mMenuItemPadding, mMenuItemPadding, mMenuItemPadding, mMenuItemPadding);
                    itemView.setMenuItem(menuItem);
                    itemView.setLevel(0);
                    itemView.setOrientation(orientation);
                    itemView.setCallback(onItemCallback);
                    addView(itemView);
                    root.add(itemView);

                    if (orientation == ItemView.VERTICAL) {
                        layout(itemView, mOriginOutlineRect.centerX(),
                                mOriginOutlineRect.centerY() - mOriginRadius + mMenuItemRadius, position);
                    } else if (orientation == ItemView.HORIZONTAL) {
                        layout(itemView, mOriginOutlineRect.centerX() - mOriginRadius + mMenuItemRadius,
                                mOriginOutlineRect.centerY(), position);
                    }
                    position++;
                }
            }
            mMenuItemArray.append(0, root);
        }
    }

    private void layout(ItemView itemView, int originX, int originY, int position) {
        measureChild(itemView, 0, 0);
        if (itemView.getOrientation() == ItemView.HORIZONTAL) {
            itemView.layout(
                    originX - mMenuItemRadius - mMenuItemMargin * (position + 1) - mMenuItemRadius * 2 * (position + 1),
                    originY - mMenuItemRadius,
                    originX - mMenuItemRadius - mMenuItemMargin * (position + 1) - mMenuItemRadius * 2 * position,
                    originY + mMenuItemRadius);
        } else if (itemView.getOrientation() == ItemView.VERTICAL) {
            itemView.layout(
                    originX - mMenuItemRadius,
                    originY - mMenuItemRadius - mMenuItemMargin * (position + 1) - mMenuItemRadius * 2 * (position + 1),
                    originX + mMenuItemRadius,
                    originY - mMenuItemRadius - mMenuItemMargin * (position + 1) - mMenuItemRadius * 2 * position);
        }
    }
}
