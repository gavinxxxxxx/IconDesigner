package me.gavin.svg.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * ViewBox
 *
 * @author gavin.xiong 2017/9/4
 */
public class ViewBox implements Serializable {

    public final float x;
    public final float y;
    public final float width;
    public final float height;

    public ViewBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ViewBox(@NonNull float[] floats) {
        if (floats.length != 4) {
            throw new IllegalArgumentException("argument error");
        }
        this.x = floats[0];
        this.y = floats[1];
        this.width = floats[2];
        this.height = floats[3];
    }

    @Override
    public String toString() {
        return "ViewBox[" + x + " " + y + " " + width + " " + height + ']';
    }
}
