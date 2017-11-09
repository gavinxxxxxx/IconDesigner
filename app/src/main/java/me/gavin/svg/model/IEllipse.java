package me.gavin.svg.model;

import java.io.Serializable;

/**
 * 椭圆
 *
 * @author gavin.xiong 2017/8/28
 */
public class IEllipse extends Drawable implements Serializable {

    public final float cx;
    public final float cy;
    public final float rx;
    public final float ry;

    public IEllipse(float cx, float cy, float rx, float ry) {
        this.cx = cx;
        this.cy = cy;
        this.rx = rx;
        this.ry = ry;
    }
}
