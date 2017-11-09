package me.gavin.svg.model;

import java.io.Serializable;

/**
 * 矩形
 *
 * @author gavin.xiong 2017/8/28
 */
public class IRect extends Drawable implements Serializable {

    public final float x;
    public final float y;
    public final float width;
    public final float height;
    public final float rx;
    public final float ry;

    public IRect(float x, float y, float width, float height, float rx, float ry) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rx = rx;
        this.ry = ry;
    }

}
