package me.gavin.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.Gravity;

public class Behavior extends CoordinatorLayout.Behavior<MultilevelMenu> {

    public Behavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams lp) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            lp.dodgeInsetEdges = Gravity.BOTTOM;
        }
    }
}