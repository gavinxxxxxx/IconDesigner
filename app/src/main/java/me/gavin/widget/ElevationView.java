package me.gavin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import me.gavin.icon.designer.R;
import me.gavin.util.DisplayUtil;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/23
 */
public class ElevationView extends View {

    public ElevationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setElevation(DisplayUtil.dp2px(6));
//        setOutlineProvider(new MyOutlineProvider());

        setBackground(new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
                canvas.drawOval(rect(), paint);
            }

            @Override
            public void getOutline(Outline outline) {
                outline.setOval((int) rect().left, (int) rect().top, (int) rect().right, (int) rect().bottom);
            }
        }));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(DisplayUtil.dp2px(56), DisplayUtil.dp2px(56));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
        canvas.drawOval(0, 0, getWidth(), getHeight(), paint);
    }
}
