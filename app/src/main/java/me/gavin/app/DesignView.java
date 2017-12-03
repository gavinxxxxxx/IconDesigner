package me.gavin.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.gavin.svg.model.SVG;
import me.gavin.util.CacheHelper;

/**
 * 预览
 * <p>
 * gavin.xxx.xxx@gmail.com
 *
 * @author gavin.xiong 2017/11/8
 */
public class DesignView extends View {

    private final float BG_L_RATIO = 176f / 192f;
    private final float BG_M_RATIO = 152f / 192f;
    private final float BG_S_RATIO = 128f / 192f;

    private int mSize;

    public SVG mSvg;
    private boolean newTag;
    // 画布矩阵
    private final Matrix mMatrix = new Matrix();
    // SVG 固有比例
    private float mInherentScale;


    private Icon mIcon;

    private Path mBgPath = new Path(), mShadowPath = new Path(), mShadowPathTemp = new Path(), mScorePath;

    private Matrix mBgMatrix = new Matrix();

    private Paint mBgPaint, mShadowPaint, mIconPaint, mScorePaint;

    private float scaleNow = 0.5f;

    public DesignView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mIcon = new Icon();
        mIcon.bgShape = 0;
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setColor(mIcon.bgColor);
        mBgPaint.setShadowLayer(0.5f, 0, 0.4f, 0x40000000);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(0x11000000);

        mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScorePaint.setColor(0x20202020);

        // setBackground(buildBackground());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 取最大正方形
        mSize = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        onDraw2(canvas, mMatrix);
    }

    private void onDraw2(Canvas canvas, Matrix matrix) {
        if (mSvg == null) return;

        if (newTag) {
            newTag = false;
            drawReset();
        }

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

        if (mIcon.effectScore) {
            canvas.drawPath(mScorePath, mScorePaint);
        }
    }

    private void drawReset() {
        mMatrix.reset();
        mInherentScale = mSvg.getInherentScale();
        // 比例同步
        mMatrix.postScale(mInherentScale, mInherentScale);
        mMatrix.postTranslate((mSize - mSvg.width) / 2f, (mSize - mSvg.height) / 2f);
        float scale = Math.min(mSize / mSvg.width, mSize / mSvg.height);
        mMatrix.postScale(scale, scale, mSize / 2f, mSize / 2f);

        // 背景
        buildBgPath();

        // 折痕
        buildScorePath();

        // 阴影初始化
        initShadowPath();

        // 当前缩放比
        mMatrix.postScale(scaleNow, scaleNow, mSize / 2f, mSize / 2f);
    }

    private void buildBgPath() {
        float size = Math.max(mSvg.width, mSvg.height) / mInherentScale / 2f / scaleNow;
        float cr = size * 0.06f;
        if (mIcon.bgShape == 0) {
            mBgPath.reset();
            mBgPath.addRoundRect(mSvg.viewBox.width / 2f - size * BG_M_RATIO, mSvg.viewBox.height / 2f - size * BG_M_RATIO,
                    mSvg.viewBox.width / 2f + size * BG_M_RATIO, mSvg.viewBox.height / 2f + size * BG_M_RATIO, cr, cr, Path.Direction.CCW);
        } else if (mIcon.bgShape == 1) {
            mBgPath.reset();
            mBgPath.addCircle(mSvg.viewBox.width / 2f, mSvg.viewBox.height / 2f,
                    size * BG_L_RATIO, Path.Direction.CCW);
        } else if (mIcon.bgShape == 2) {
            mBgPath.reset();
            mBgPath.addRoundRect(mSvg.viewBox.width / 2f - size * BG_S_RATIO, mSvg.viewBox.height / 2f - size * BG_L_RATIO,
                    mSvg.viewBox.width / 2f + size * BG_S_RATIO, mSvg.viewBox.height / 2f + size * BG_L_RATIO,
                    cr, cr, Path.Direction.CCW);
        } else if (mIcon.bgShape == 3) {
            mBgPath.reset();
            mBgPath.addRoundRect(mSvg.viewBox.width / 2f - size * BG_L_RATIO, mSvg.viewBox.height / 2f - size * BG_S_RATIO,
                    mSvg.viewBox.width / 2f + size * BG_L_RATIO, mSvg.viewBox.height / 2f + size * BG_S_RATIO,
                    cr, cr, Path.Direction.CCW);
        }
    }

    private void buildScorePath() {
        float size = Math.max(mSvg.width, mSvg.height) / mInherentScale / 2f / scaleNow;
        mScorePath = new Path();
        mScorePath.addRect(mSvg.viewBox.width / 2f - size,
                mSvg.viewBox.height / 2f - size,
                mSvg.viewBox.width / 2f + size,
                mSvg.viewBox.height / 2f,
                Path.Direction.CCW);
        mScorePath.op(mBgPath, Path.Op.INTERSECT);
    }

    /**
     * 阴影初始化
     * 1024 * 1024 0.8 & 512
     */
    private void initShadowPath() {
        float length = Math.max(mSvg.viewBox.width, mSvg.viewBox.height) / 0.8f;
        float diff = Math.max(mSvg.viewBox.width, mSvg.viewBox.height) / 512f;
        Observable
                .create(e -> {
                    float d = length;
                    while (d >= diff) {
                        d /= 2f;
                        mShadowPathTemp.set(mShadowPath);
                        mShadowPathTemp.offset(d, d);
                        mShadowPath.op(mShadowPathTemp, Path.Op.UNION);
                        e.onNext(d);
                    }
                    e.onComplete();
                })
                // .throttleLast(200, TimeUnit.MILLISECONDS)
                .doOnSubscribe(disposable -> {
                    mShadowPath.reset();
                    for (int i = 0; i < mSvg.paths.size(); i++) {
                        if (mSvg.drawables.get(i).getFillPaint().getColor() != 0) {
                            mShadowPath.op(mSvg.paths.get(i), Path.Op.UNION);
                        }
                    }
                })
                .doOnComplete(this::postInvalidate)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(arg0 -> postInvalidate());
    }

    public void setSVG(SVG svg) {
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

    public int getIconColor() {
        return mIconPaint != null ? mIconPaint.getColor() : 0xFFFF0000;
    }

    public void setBgColor(Integer color) {
        if (color != null) {
            mBgPaint.setColor(color);
            invalidate();
        }
    }

    public int getBgColor() {
        return mBgPaint.getColor();
    }

    public void setIconSize(int progress) {
        float aimScale = 0.3f + progress / 100f * 0.4f;
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

    public boolean toggleEffectScore() {
        mIcon.effectScore = !mIcon.effectScore;
        invalidate();
        return mIcon.effectScore;
    }

    public Bitmap getBitmap(int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix(mMatrix);
        matrix.postScale(size * 1f / mSize, size * 1f / mSize, 0, 0);
        onDraw2(canvas, matrix);
        return bitmap;
    }

    public String save(String name, int size) throws IOException {
        return CacheHelper.saveBitmap(getBitmap(size), name);
    }

}
