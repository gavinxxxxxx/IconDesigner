package me.gavin.svg.model;

import java.io.Serializable;

/**
 * 圆
 *
 * @author gavin.xiong 2017/8/28
 */
public class ICircle extends Drawable implements Serializable {

    public final float cx;
    public final float cy;
    public final float r;

    public ICircle(float cx, float cy, float r) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;
    }
}
