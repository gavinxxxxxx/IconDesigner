package me.gavin.icon.material;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/10
 */
public class TestView extends View {

    private final Paint mPaint;
    private final Path mRectPath;

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(5);

        mRectPath = new Path();
        mRectPath.moveTo(50, 50);
        mRectPath.rLineTo(600, 0);
        mRectPath.rLineTo(0, 600);
        mRectPath.rLineTo(-600, 0);
        mRectPath.rLineTo(0, -600);
        mRectPath.close();
        mRectPath.rMoveTo(100, 100);
        mRectPath.rLineTo(0, 400);
        mRectPath.rLineTo(400, 0);
        mRectPath.rLineTo(0, -400);
        mRectPath.rLineTo(-400, 0);
        mRectPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path(mRectPath);
        for (int i = 0; i < 600; i+=1) {
            Path path1 = new Path(mRectPath);
            path1.offset(i, i);
            path.op(path1, Path.Op.UNION);
        }
        mPaint.setColor(Color.RED);
        canvas.drawPath(path, mPaint);

//        mPaint.setColor(Color.YELLOW);
//        canvas.drawPath(mRectPath, mPaint);
    }

}
