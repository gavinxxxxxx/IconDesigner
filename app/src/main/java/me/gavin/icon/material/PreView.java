package me.gavin.icon.material;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import me.gavin.icon.material.util.DisplayUtil;
import me.gavin.icon.material.util.Icon;
import me.gavin.icon.material.util.L;
import me.gavin.svg.model.SVG;

/**
 * 预览
 *
 * @author gavin.xiong 2017/11/8
 */
public class PreView extends View {

    private int mSize;

    private Icon mIcon;

    private final Paint mBgPaintLight, mBgPaintDark;

    private Paint mBgPaint;


    public SVG mSvg;
    private boolean newTag;

    private final Matrix mMatrix = new Matrix();


    public PreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mBgPaintLight = new Paint();
        mBgPaintLight.setColor(0xff888888);
        mBgPaintDark = new Paint();
        mBgPaintDark.setColor(0xff555555);

        mIcon = new Icon();
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mIcon.bgColor);
        mBgPaint.setShadowLayer(5, 0, 2, 0x55555555);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mSize = Math.min(
                MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(widthMeasureSpec)
                        : DisplayUtil.getScreenWidth(),
                MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
                        ? MeasureSpec.getSize(heightMeasureSpec)
                        : DisplayUtil.getScreenHeight());
        setMeasuredDimension(mSize, mSize);
    }

    // TODO: 2017/11/8 canvas -> bitmap -> png
    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(0xffffffff);
        drawBackground(canvas);

        if (mIcon.bgShape == 0) {
            canvas.drawRoundRect(120, 120, mSize - 120, mSize - 120, 30, 30, mBgPaint);
        } else {
            canvas.drawCircle(mSize / 2, mSize / 2, mSize / 2 - 120, mBgPaint);
        }

        if (mSvg != null) {
            drawBefore();

            canvas.setMatrix(mMatrix);

            if (mSvg.width / mSvg.height != mSvg.viewBox.width / mSvg.viewBox.height) {
                canvas.translate(
                        mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                                ? (mSvg.width - mSvg.viewBox.width / mSvg.viewBox.height * mSvg.height) / 2 / mInherentScale : 0,
                        mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                                ? 0 : (mSvg.height - mSvg.viewBox.height / mSvg.viewBox.width * mSvg.width) / 2 / mInherentScale);
            }

            for (int i = 0; i < mSvg.paths.size(); i++) {
                canvas.drawPath(mSvg.paths.get(i), mSvg.drawables.get(i).getFillPaint());
                canvas.drawPath(mSvg.paths.get(i), mSvg.drawables.get(i).getStrokePaint());
            }


            drawRect(canvas, 3);
//            drawCircle(canvas, 3);
        }
    }

    private void drawCircle(Canvas canvas, int diff) {
        for (int i = 0; i < mSvg.paths.size(); i++) {
            Path path = mSvg.paths.get(i);
            PathMeasure pathMeasure = new PathMeasure(path, false);
            PathMeasure measure = new PathMeasure();
            Path dst = new Path();
            for (int count = 1; pathMeasure.nextContour(); count++) {
                if (count == 1) {
                    measure.setPath(path, false);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }
                    float length = measure.getLength();
                    L.e(length);
                    measure.getSegment(0, length * 0.125f, dst, true);

                    dst.rLineTo(diff, diff);
                    path.offset(diff, diff);
                    measure.setPath(path, false);

                    measure.getSegment(length * 0.125f, length * 0.625f, dst, false);

                    dst.rLineTo(-diff, -diff);
                    path.offset(-diff, -diff);
                    measure.setPath(path, false);

                    measure.getSegment(length * 0.625f, length, dst, false);
                } else if (count == 2) {
                    dst.rMoveTo(diff, diff);
                    path.offset(diff, diff);

                    measure.setPath(path, true);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }

                    float length = measure.getLength();
                    L.e(length);

                    measure.getSegment(0, length * 0.375f, dst, true);

                    dst.rLineTo(-diff, -diff);
                    path.offset(-diff, -diff);
                    measure.setPath(path, false);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }

                    measure.getSegment(length * 0.375f, length * 0.875f, dst, false);

                    dst.rLineTo(diff, diff);
                    path.offset(diff, diff);
                    measure.setPath(path, false);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }

                    measure.getSegment(length * 0.875f, length, dst, false);
                }
            }


            Paint paint = new Paint();
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setShader(new LinearGradient(0, 0, 23, 23, 0x80000000, 0x00000000, Shader.TileMode.CLAMP));
            paint.setStrokeWidth(0.2f);
            canvas.drawPath(dst, paint);
        }
    }

    private void drawRect(Canvas canvas, int diff) {
        for (int i = 0; i < mSvg.paths.size(); i++) {
            Path path = mSvg.paths.get(i);
            PathMeasure pathMeasure = new PathMeasure(path, false);
            PathMeasure measure = new PathMeasure();
            Path dst = new Path();
            for (int count = 1; pathMeasure.nextContour(); count++) {
                if (count == 1) {
                    measure.setPath(path, false);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }
                    float length = measure.getLength();
                    L.e(length);
                    measure.getSegment(0, length * 0.25f, dst, true);

                    dst.rLineTo(diff, diff);
                    path.offset(diff, diff);
                    measure.setPath(path, false);

                    measure.getSegment(length * 0.25f, length * 0.75f, dst, false);

                    dst.rLineTo(-diff, -diff);
                    path.offset(-diff, -diff);
                    measure.setPath(path, false);

                    measure.getSegment(length * 0.75f, length, dst, false);
                } else if (count == 2) {
                    dst.rMoveTo(diff, diff);
                    path.offset(diff, diff);

                    measure.setPath(path, true);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }

                    float length = measure.getLength();
                    L.e(length);

                    measure.getSegment(0, length * 0.25f, dst, true);

                    dst.rLineTo(-diff, -diff);
                    path.offset(-diff, -diff);
                    measure.setPath(path, false);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }

                    measure.getSegment(length * 0.25f, length * 0.75f, dst, false);

                    dst.rLineTo(diff, diff);
                    path.offset(diff, diff);
                    measure.setPath(path, false);
                    for (int c = 0; c < count; c++) {
                        measure.nextContour();
                    }

                    measure.getSegment(length * 0.75f, length, dst, false);
                }
            }


            Paint paint = new Paint();
            paint.setStrokeJoin(Paint.Join.ROUND);
//            paint.setStyle(Paint.Style.STROKE);
            paint.setShader(new LinearGradient(0, 0, 23, 23, 0x30000000, 0x00000000, Shader.TileMode.CLAMP));
            paint.setStrokeWidth(0.2f);
            canvas.drawPath(dst, paint);
        }
    }

    private void drawBackground(Canvas canvas) {
        int cw = 33;
        for (int x = 0; x < mSize; x += cw) {
            for (int y = 0; y < mSize; y += cw) {
                canvas.drawRect(x, y, x + cw, y + cw,
                        ((x ^ y) & 1) == 1 ? mBgPaintDark : mBgPaintLight);
            }
        }
    }

    private float mInherentScale;

    private void drawBefore() {
        if (newTag) {
            newTag = false;
            mMatrix.reset();
            mInherentScale = mSvg.getInherentScale();
            mMatrix.postScale(mInherentScale, mInherentScale);
            mMatrix.postTranslate((getWidth() - mSvg.width) / 2f, (getHeight() - mSvg.height) / 2f);
            float size = Math.min(getWidth(), getHeight()) * 0.8f;
            float scale = Math.min(size / mSvg.width, size / mSvg.height);
            mMatrix.postScale(scale, scale, getWidth() / 2f, getHeight() / 2f);
        }
    }

    public void setSVG(SVG svg) {
        this.mSvg = svg;
        newTag = true;
        // requestLayout();
        invalidate();
    }

}
