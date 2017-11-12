package me.gavin.icon.material;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
    private final Path mRectPath, mCirclePath, mZPath;

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(5);

        mRectPath = new Path();
        mRectPath.moveTo(50, 20);
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

        mCirclePath = new Path();
        mCirclePath.moveTo(50, 1170);
        mCirclePath.rCubicTo(0, 165, 135, 300, 300, 300);
        mCirclePath.rCubicTo(165, 0, 300, -135, 300, -300);
        mCirclePath.rCubicTo(0, -165, -135, -300, -300, -300);
        mCirclePath.rCubicTo(-165, 0, -300, 135, -300, 300);
        mCirclePath.rMoveTo(100, 0);
        mCirclePath.rCubicTo(0, -110, 90, -200, 200, -200);
        mCirclePath.rCubicTo(110, 0, 200, 90, 200, 200);
        mCirclePath.rCubicTo(0, 110, -90, 200, -200, 200);
        mCirclePath.rCubicTo(-110, 0, -200, -90, -200, -200);

        mZPath = new Path();
        mZPath.moveTo(200, 400);

        mZPath.rLineTo(0, 300);
        mZPath.rLineTo(300, 0);
        mZPath.rLineTo(0, 300);
        mZPath.rLineTo(300, 0);
        mZPath.rLineTo(0, -300);
        mZPath.rLineTo(300, 0);
        mZPath.rLineTo(0, -300);
        mZPath.rLineTo(-300, 0);
        mZPath.rLineTo(0, -300);
        mZPath.rLineTo(-300, 0);
        mZPath.rLineTo(0, 300);
        mZPath.rLineTo(-300, 0);
        mZPath.close();

        mZPath.rMoveTo(300, 100);
        mZPath.rLineTo(100, 0);
        mZPath.rLineTo(0, -100);
        mZPath.rLineTo(100, 0);
        mZPath.rLineTo(0, 100);
        mZPath.rLineTo(100, 0);
        mZPath.rLineTo(0, 100);
        mZPath.rLineTo(-100, 0);
        mZPath.rLineTo(0, 100);
        mZPath.rLineTo(-100, 0);
        mZPath.rLineTo(0, -100);
        mZPath.rLineTo(-100, 0);
        mZPath.rLineTo(0, -100);
        mZPath.close();

        Matrix matrix = new Matrix();
        matrix.preRotate(75, 500, 500);
        mZPath.transform(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        draw(canvas, mRectPath);
//        draw(canvas, mCirclePath);
        draw(canvas, mZPath);
    }

    private void draw(Canvas canvas, Path path) {
        Path copy = new Path(path);
        for (int i = 0; i < 250; i += 10) {
            Path path1 = new Path(path);
            path1.offset(i, i);
            copy.op(path1, Path.Op.UNION);
        }
        mPaint.setColor(Color.RED);
        canvas.drawPath(copy, mPaint);

        mPaint.setColor(Color.YELLOW);
        canvas.drawPath(path, mPaint);
    }

}
