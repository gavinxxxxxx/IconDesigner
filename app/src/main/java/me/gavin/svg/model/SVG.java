package me.gavin.svg.model;

import android.graphics.Matrix;
import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SVG
 *
 * @author gavin.xiong 2017/9/4
 */
public class SVG implements Serializable {

    public String name;
    public float width;
    public float height;
    public ViewBox viewBox;
    public String title;
    public String desc;
    public String preserveAspectRatio;
    public String style;
    public List<Drawable> drawables = new ArrayList<>();
    public List<Path> paths = new ArrayList<>();
    public final Matrix matrix = new Matrix();

    public SVG(float width, float height) {
        this(width, height, null);
    }

    public SVG(float width, float height, ViewBox viewBox) {
        this.width = width;
        this.height = height;
        this.viewBox = viewBox;
        if (this.viewBox == null) {
            this.viewBox = new ViewBox(0, 0, width, height);
        }
    }

    public float getInherentScale() {
        return Math.min(width / viewBox.width, height / viewBox.height);
    }

    @Override
    public String toString() {
        return "SVG{" +
                "name=" + name +
                ", width=" + width +
                ", height=" + height +
                ", viewBox=" + viewBox +
                ", preserveAspectRatio='" + preserveAspectRatio + '\'' +
                ", style='" + style + '\'' +
                ", drawables=" + drawables +
                '}';
    }
}
