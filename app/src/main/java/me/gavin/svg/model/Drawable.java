package me.gavin.svg.model;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * 图形基类
 *
 * @author gavin.xiong 2017/8/28
 */
public abstract class Drawable implements Serializable {

    private Paint strokePaint;
    private Paint fillPaint;
    private float strokeWidth;
    private float[] strokeDashArray;
    private float strokeDashOffset;

    public Paint getStrokePaint() {
        return strokePaint;
    }

    public void setStrokePaint(Paint strokePaint) {
        this.strokePaint = strokePaint;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
    
    public void scale(boolean scale) {
        // TODO: 2017/9/8  
    }
}
