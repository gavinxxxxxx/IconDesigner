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

    // 原点
    private Rect mOriginOutlineRect;
    private int mOriginRadius;
    private int mOriginBgColor;
    private Drawable mOriginIcon;
    private int mOriginPadding;

    // MenuItem
    private int mMenuItemMargin;
    private int mMenuItemRadius;
    private int mMenuItemBgColor;
    private int mMenuItemPadding;
    private int mMenuItemIconColor;

    // menu
    private Menu mVerticalMenu, mHorizontalMenu;

    SparseArray<List<ItemView>> menuArray;
    private SparseArray<List<ItemView2>> mMenuItemArray;

    private Consumer<MenuItem> onMenuItemSelectedListener;

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
        mOriginRadius = ta.getDimensionPixelOffset(R.styleable.MultilevelMenu_mlm_originRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()));
        mOriginBgColor = ta.getColor(R.styleable.MultilevelMenu_mlm_originBgColor, 0xFF303030);
        mOriginPadding = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_originPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
        mOriginIcon = ta.getDrawable(R.styleable.MultilevelMenu_mlm_originIcon);
        if (mOriginIcon == null) {
            mOriginIcon = getResources().getDrawable(R.drawable.ic_apps_black_24dp);
        }
        int floatIconColor = ta.getColor(R.styleable.MultilevelMenu_mlm_originIconColor, 0xFFFFFFFF);
        mOriginIcon.setColorFilter(floatIconColor, PorterDuff.Mode.SRC_IN);
        // mFloatIcon.setTint(floatIconColor);
        // mFloatIcon.setTintMode(PorterDuff.Mode.SRC_IN);

        mMenuItemMargin = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemMargin,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mMenuItemRadius = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        mMenuItemBgColor = ta.getColor(R.styleable.MultilevelMenu_mlm_menuItemBgColor, 0xFF303030);
        mMenuItemPadding = ta.getDimensionPixelSize(R.styleable.MultilevelMenu_mlm_menuItemPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
        mMenuItemIconColor = ta.getColor(R.styleable.MultilevelMenu_mlm_menuItemIconColor, 0xFFFFFFFF);

        int verticalMenuId = ta.getResourceId(R.styleable.MultilevelMenu_mlm_menuVertical, 0);
        if (verticalMenuId != 0) {
            mVerticalMenu = new PopupMenu(getContext(), null).getMenu();
            initMenu(mVerticalMenu, verticalMenuId);
        }
        int hMenuId = ta.getResourceId(R.styleable.MultilevelMenu_mlm_menuHorizontal, 0);
        if (hMenuId != 0) {
            mHorizontalMenu = new PopupMenu(getContext(), null).getMenu();
            initMenu(mHorizontalMenu, verticalMenuId);
        }

        ta.recycle();
    }

    public void setOnMenuItemSelectedListener(Consumer<MenuItem> listener) {
        onMenuItemSelectedListener = listener;
    }

    private int mCurrLevel = 0;

    private ItemView.Callback onItemCallback = new ItemView.Callback() {
        @Override
        public void onEntered(ItemView v, MenuItem item) {
            L.e(item.getTitle() + " - enter - " + v.getCenterPoint());

            for (int i = v.getLevel() + 1; i <= mCurrLevel; i++) {
                List<ItemView> itemViews = menuArray.get(i);
                if (itemViews != null && !itemViews.isEmpty()) {
                    for (ItemView itemView : itemViews) {
                        removeView(itemView);
                    }
                }
                menuArray.delete(i);
            }


            SubMenu subMenu = item.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                mCurrLevel = v.getLevel() + 1;
                List<ItemView> sub = new ArrayList<>();
                int position = 0;
                for (int i = 0; i < subMenu.size(); i++) {
                    MenuItem menuItem = subMenu.getItem(i);
                    if (menuItem.isVisible()) {
                        ItemView itemView = new ItemView(getContext(), menuItem,
                                mCurrLevel, v.getOrientation() ^ ItemView.VERTICAL);
                        itemView.setVisibility(VISIBLE);
                        itemView.setCallback(onItemCallback);
                        addView(itemView);
                        sub.add(itemView);

//                        layout(itemView, v.getCenterPoint().x, v.getCenterPoint().y, position);
                        position++;
                    }
                }
                menuArray.append(mCurrLevel, sub);
            }
        }

        @Override
        public void onExited(ItemView v, MenuItem item) {
            L.e(item.getTitle() + " - exit");
            if (item.getSubMenu() != null && item.getSubMenu().size() > 0) {
                // TODO: 2017/11/21
            }
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
                mWidth - mPadding - mOriginRadius * 2,
                mHeight - mPadding - mOriginRadius * 2,
                mWidth - mPadding,
                mHeight - mPadding);

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            double distance = Math.sqrt(Math.pow(mWidth - mPadding - mOriginRadius - event.getX(), 2)
                    + Math.pow(mHeight - mPadding - mOriginRadius - event.getY(), 2));
            if (distance <= mOriginRadius) {
                DragShadowBuilder dragShadowBuilder = new DragShadowBuilder();
                ClipData dragData = DragUtils.getClipData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    startDragAndDrop(dragData, dragShadowBuilder, null, 0);
                } else {
                    startDrag(dragData, dragShadowBuilder, null, 0);
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

                    removeAllViews();
                    mMenuItemArray = new SparseArray<>();
                    List<ItemView2> root = new ArrayList<>();
                    int position = 0;
                    for (int i = 0; i < mVerticalMenu.size(); i++) {
                        MenuItem menuItem = mVerticalMenu.getItem(i);
                        if (menuItem.isVisible()) {
                            ItemView2 itemView = new ItemView2(getContext());
                            itemView.setMenuItem(menuItem);
                            itemView.setOrientation(ItemView2.VERTICAL);
                            itemView.setPadding(mMenuItemPadding, mMenuItemPadding, mMenuItemPadding, mMenuItemPadding);
                            addView(itemView);
                            root.add(itemView);

//                            ItemView itemView = new ItemView(getContext(), menuItem, 0, ItemView.VERTICAL);
//                            itemView.setVisibility(VISIBLE);
//                            itemView.setCallback(onItemCallback);
//                            addView(itemView);
//                            root.add(itemView);

                            layout(itemView, mWidth - mPadding - mOriginRadius,
                                    mHeight - mPadding - mOriginRadius * 2 + mMenuItemRadius, position);
                            position++;
                        }
                    }
                    mMenuItemArray.append(0, root);
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

    private void layout(ItemView2 itemView, int originX, int originY, int position) {
        L.e("layout - " + originX + " - " + originY + " - " + itemView.getOrientation());
        measureChild(itemView, 0, 0);
        if (itemView.getOrientation() == ItemView.HORIZONTAL) {
            itemView.layout(
                    originX - mMenuItemRadius - mMenuItemMargin * (position + 1) - (mMenuItemRadius * 2 + mMenuItemPadding * 2) * (position + 1) - mMenuItemPadding,
                    originY + mMenuItemRadius + mMenuItemPadding - (mMenuItemRadius * 2 + mMenuItemPadding * 2),
                    originX - mMenuItemRadius - mMenuItemMargin * (position + 1) - (mMenuItemRadius * 2 + mMenuItemPadding * 2) * position - mMenuItemPadding,
                    originY + mMenuItemRadius + mMenuItemPadding);
        } else if (itemView.getOrientation() == ItemView.VERTICAL) {
            itemView.layout(
                    originX + mMenuItemRadius + mMenuItemPadding - (mMenuItemRadius * 2 + mMenuItemPadding * 2),
                    originY - mMenuItemRadius - mMenuItemMargin * (position + 1) - (mMenuItemRadius * 2 + mMenuItemPadding * 2) * (position + 1) - mMenuItemPadding,
                    originX + mMenuItemRadius + mMenuItemPadding,
                    originY - mMenuItemRadius - mMenuItemMargin * (position + 1) - (mMenuItemRadius * 2 + mMenuItemPadding * 2) * position - mMenuItemPadding);
        }
    }
}
