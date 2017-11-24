package me.gavin.widget;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/23
 */
public class MyOutlineProvider extends ViewOutlineProvider {

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setOval(0, 0, view.getWidth(), view.getHeight());
    }
}
