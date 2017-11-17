package me.gavin.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.OutputStream;

import me.gavin.svg.model.SVG;
import me.gavin.util.DisplayUtil;
import me.gavin.util.L;

/**
 * 预览
 * <p>
 * gavin.xxx.xxx@gmail.com
 *
 * @author gavin.xiong 2017/11/8
 */
public class PreView extends View {

    private final float BG_CIRCLE_RA = 176f / 192f;
    private final float BG_RECT_RA = 152f / 192f;

    private int mSize;

    public SVG mSvg;
    private boolean newTag;
    private final Matrix mMatrix = new Matrix();
    private float mInherentScale;


    private Icon mIcon;

    private Path mBgPath = new Path(), mShadowPath, mShadowPathTemp = new Path(), mScorePath;

    private Matrix mBgMatrix = new Matrix();

    private Paint mBgPaint, mShadowPaint, mIconPaint, mScorePaint;

    private float scaleNow = 0.65f;

    public PreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mIcon = new Icon();
        mIcon.bgShape = 0;
        mBgPaint = new Paint();
        // mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mIcon.bgColor);
        mBgPaint.setShadowLayer(0.5f, 0, 0.4f, 0x55555555);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(0x11 << 24);

        mScorePaint = new Paint();
        mScorePaint.setAntiAlias(true);
        mScorePaint.setColor(0x20202020);

        setBackground(buildBackground());
    }

    private Drawable buildBackground() {
        return new ShapeDrawable(new RectShape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                int cw = 31;
                for (int x = 0; x < rect().width(); x += cw) {
                    for (int y = 0; y < rect().height(); y += cw) {
                        paint.setColor(((x ^ y) & 1) == 1 ? 0xff555555 : 0xff888888);
                        canvas.drawRect(x, y, x + cw, y + cw, paint);
                    }
                }
            }
        });
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
        onDraw2(canvas, mMatrix);
    }

    private void onDraw2(Canvas canvas, Matrix matrix) {
        if (mSvg == null) return;

        drawBefore();

        canvas.setMatrix(matrix);

        if (mSvg.width / mSvg.height != mSvg.viewBox.width / mSvg.viewBox.height) {
            canvas.translate(
                    mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                            ? (mSvg.width - mSvg.viewBox.width / mSvg.viewBox.height * mSvg.height) / 2 / mInherentScale : 0,
                    mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                            ? 0 : (mSvg.height - mSvg.viewBox.height / mSvg.viewBox.width * mSvg.width) / 2 / mInherentScale);
        }

        canvas.drawPath(mBgPath, mBgPaint);

        mShadowPathTemp.set(mShadowPath);
        mShadowPathTemp.op(mBgPath, Path.Op.INTERSECT);
        canvas.drawPath(mShadowPathTemp, mShadowPaint);

        for (int i = 0; i < mSvg.paths.size(); i++) {
            if (mSvg.drawables.get(i).getFillPaint().getColor() != 0) {
                canvas.drawPath(mSvg.paths.get(i), mIconPaint != null ? mIconPaint : mSvg.drawables.get(i).getFillPaint());
            }
        }

        canvas.drawPath(mScorePath, mScorePaint);
    }

    private void drawBefore() {
        if (newTag) {
            newTag = false;
            mMatrix.reset();
            mInherentScale = mSvg.getInherentScale();
            mMatrix.postScale(mInherentScale, mInherentScale);
            mMatrix.postTranslate((mSize - mSvg.width) / 2f, (mSize - mSvg.height) / 2f);
            float size = Math.min(mSize, mSize) * 0.8f;
            float scale = Math.min(size / mSvg.width, size / mSvg.height);
            mMatrix.postScale(scale, scale, mSize / 2f, mSize / 2f);

            // 背景
            buildBgPath();

            // 折痕
            buildScorePath();

            // 当前缩放比
            mMatrix.postScale(scaleNow, scaleNow, mSize / 2f, mSize / 2f);
            // 阴影初始化
            initShadowPath(40f, 0.1f);
        }
    }

    private void buildBgPath() {
        if (mIcon.bgShape == 0) {
            mBgPath.reset();
            float size = mSvg.viewBox.width / 2f / 0.8f * BG_RECT_RA / scaleNow;
            mBgPath.addRoundRect(mSvg.viewBox.width / 2f - size,
                    mSvg.viewBox.height / 2f - size,
                    mSvg.viewBox.width / 2f + size,
                    mSvg.viewBox.height / 2f + size,
                    2.6f,
                    2.6f,
                    Path.Direction.CCW);
        } else if (mIcon.bgShape == 1) {
            mBgPath.reset();
            mBgPath.addCircle(mSvg.viewBox.width / 2f,
                    mSvg.viewBox.height / 2f,
                    mSvg.viewBox.width / 2f / 0.8f * BG_CIRCLE_RA / scaleNow,
                    Path.Direction.CCW);
        }
    }

    private void buildScorePath() {
        float w = mSvg.viewBox.width / 0.8f / scaleNow / 2f;
        float h = mSvg.viewBox.height / 0.8f / scaleNow / 2f;
        mScorePath = new Path();
        mScorePath.addRect(mSvg.viewBox.width / 2f - w,
                mSvg.viewBox.height / 2f - h,
                mSvg.viewBox.width / 2f + w,
                mSvg.viewBox.height / 2f,
                Path.Direction.CCW);
        mScorePath.op(mBgPath, Path.Op.INTERSECT);
    }

    /**
     * 阴影初始化
     *
     * @param length
     * @param diff
     */
    private void initShadowPath(float length, float diff) {
        mShadowPath = new Path();
        for (int i = 0; i < mSvg.paths.size(); i++) {
            if (mSvg.drawables.get(i).getFillPaint().getColor() != 0) {
                mShadowPath.op(mSvg.paths.get(i), Path.Op.UNION);
            }
        }
        float d = length;
        while (d >= diff) {
            d /= 2f;
            mShadowPathTemp.set(mShadowPath);
            mShadowPathTemp.offset(d, d);
            mShadowPath.op(mShadowPathTemp, Path.Op.UNION);
        }
    }

    public void setSVG(SVG svg) {
        L.e(scaleNow);
        this.mSvg = svg;
        newTag = true;
        invalidate();
    }

    public void setIconColor(Integer color) {
        if (color == null) {
            mIconPaint = null;
        } else {
            mIconPaint = new Paint();
            mIconPaint.setAntiAlias(true);
            mIconPaint.setColor(color);
        }
        invalidate();
    }

    public void setBgColor(Integer color) {
        if (color != null) {
            mBgPaint.setColor(color);
            invalidate();
        }
    }

    public void setIconSize(int progress) {
        float aimScale = 0.3f + progress / 100f * 0.7f;
        mMatrix.postScale(aimScale / scaleNow, aimScale / scaleNow, mSize / 2f, mSize / 2f);

        mBgMatrix.reset();
        mBgMatrix.postScale(scaleNow / aimScale, scaleNow / aimScale, mSvg.viewBox.width / 2f, mSvg.viewBox.width / 2f);
        mBgPath.transform(mBgMatrix);

        mScorePath.transform(mBgMatrix);

        invalidate();
        scaleNow = aimScale;
    }

    public void setShadowAlpha(int progress) {
        int alpha = (int) (progress / 200f * 0xFF);
        mShadowPaint.setColor(alpha << 24);
        invalidate();
    }

    public void setBgShape(int shape) {
        mIcon.bgShape = shape;
        buildBgPath();
        buildScorePath();
        invalidate();
    }

    public void save(OutputStream outputStream, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix(mMatrix);
        matrix.postScale(size * 1f / mSize, size * 1f / mSize, 0, 0);
        onDraw2(canvas, matrix);
//        try (fos) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//            L.e(e);
//        }
    }

}
