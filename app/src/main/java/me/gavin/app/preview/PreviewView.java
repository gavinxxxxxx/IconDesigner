package me.gavin.app.preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;

import me.gavin.svg.model.SVG;
import me.gavin.util.CacheHelper;
import me.gavin.util.DisplayUtil;

/**
 * PreviewView
 *
 * @author gavin.xiong 2017/12/4
 */
public class PreviewView extends View {

    private int mSize;

    private SVG mSrcSVG;
    private Drawable mSrcDrawable;
    private Bitmap mSrcBitmap;
    private String mSrcText;

    private final Icon mIcon;

    private Path mKeyLinesPath = new Path(), mBgPath, mBgLayerPath, mScorePath, mEarPath;

    private Bitmap mIconBitmap, mShadowBitmap, mEarShadowBitmap;

    private final Paint mKeyLinesPaint, mBgPaint, mBgLayerPaint, mIconPaint, mShadowPaint, mScorePaint, mEarPaint;

    public PreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mIcon = new Icon();

        mKeyLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mKeyLinesPaint.setStyle(Paint.Style.STROKE);
        mKeyLinesPaint.setStrokeWidth(1);
        mKeyLinesPaint.setColor(0xFFFFFFFF);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mIcon.bgColor);

        mBgLayerPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgLayerPaint.setStyle(Paint.Style.FILL);
        mBgLayerPaint.setColor(0x00000000);

        mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mIconPaint.setStyle(Paint.Style.FILL);
        mIconPaint.setColorFilter(this.mIcon.iconColor == null ? null
                : new PorterDuffColorFilter(this.mIcon.iconColor, PorterDuff.Mode.SRC_IN));

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColorFilter(new PorterDuffColorFilter(mIcon.shadowAlpha << 24, PorterDuff.Mode.SRC_IN));

        mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mScorePaint.setStyle(Paint.Style.FILL);
        mScorePaint.setColor(0x18181818);

        mEarPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mEarPaint.setStyle(Paint.Style.FILL);
        mEarPaint.setColor(Utils.getEarColor(mIcon.bgColor));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 取最大正方形
        mSize = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(mSize, mSize);
        mBgLayerPaint.setShadowLayer(mSize * Icon.BG_SL_RATIO, 0,
                DisplayUtil.dp2px(mSize * Icon.BG_SL_RATIO / 4f), 0x50000000);
        if (mSrcSVG != null && mSize > 0) {
            mBgPath = Utils.getBgPath(mIcon.bgShape, mSize, mIcon.bgCorner, mIcon.effectEar);
            mBgLayerPath = Utils.getBgLayerPath(mBgPath, mSize);
            mIconBitmap = Utils.getBitmap(mSrcSVG, mSize, mIcon.iconScale, mBgPath);
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            mEarPath = Utils.getEarPath(mBgPath, mSize);
            mEarShadowBitmap = Utils.getEarShadow(mEarPath, mSize, mBgPath, true);
            mScorePath = Utils.getScorePath(mSize, mBgPath);
            invalidate();
        }
        mKeyLinesPath = Utils.getKeyLines(mSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBgPath != null && mIconBitmap != null && mShadowBitmap != null && mSize > 0) {
            canvas.drawPath(mBgLayerPath, mBgLayerPaint);
            canvas.drawPath(mBgPath, mBgPaint);
            canvas.drawBitmap(mShadowBitmap, 0, 0, mShadowPaint);
            canvas.drawBitmap(mIconBitmap, 0, 0, mIconPaint);
            if (mIcon.effectScore && mScorePath != null) {
                canvas.drawPath(mScorePath, mScorePaint);
            }
            if (mIcon.effectEar && mEarPath != null) {
                canvas.drawBitmap(mEarShadowBitmap, 0, 0, mShadowPaint);
                canvas.drawPath(mEarPath, mEarPaint);
            }
        }
        if (mSize > 0 && mIcon.showKeyLines && mKeyLinesPath != null) {
            canvas.drawPath(mKeyLinesPath, mKeyLinesPaint);
        }
    }

    private void recyclerSrc() {
        this.mSrcSVG = null;
        this.mSrcDrawable = null;
        this.mSrcText = null;
        if (mSrcBitmap != null) {
            mSrcBitmap.recycle();
            mSrcBitmap = null;
        }
    }

    public void setSVG(SVG mSvg) {
        recyclerSrc();
        this.mSrcSVG = mSvg;
        if (mSvg != null && mSize > 0) {
            mIconBitmap = Utils.getBitmap(mSvg, mSize, mIcon.iconScale, mBgPath);
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public void setDrawable(Drawable drawable) {
        recyclerSrc();
        this.mSrcDrawable = drawable;
        mIconBitmap = Utils.getBitmap(drawable, mSize, mIcon.iconScale, mBgPath);
        if (mSize > 0) {
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public void setBitmap(Bitmap bitmap) {
        recyclerSrc();
        this.mSrcBitmap = bitmap;
        mIconBitmap = Utils.getBitmap(mSrcBitmap, mSize, mIcon.iconScale, mBgPath);
        if (mSize > 0) {
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public void setText(String text) {
        recyclerSrc();
        this.mSrcText = text;
        mIconBitmap = Utils.getBitmap(text, mSize, mIcon.iconScale, mBgPath);
        if (mSize > 0) {
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public void setBgShape(int shape) {
        this.mIcon.bgShape = shape;
        mBgPath = Utils.getBgPath(mIcon.bgShape, mSize, mIcon.bgCorner, mIcon.effectEar);
        mBgLayerPath = Utils.getBgLayerPath(mBgPath, mSize);
        mEarPath = Utils.getEarPath(mBgPath, mSize);
        mEarShadowBitmap = Utils.getEarShadow(mEarPath, mSize, mBgPath, true);
        mScorePath = Utils.getScorePath(mSize, mBgPath);
        if (mShadowBitmap != null && !mShadowBitmap.isRecycled()) {
            mShadowBitmap.recycle();
        }
        if (mIconBitmap != null && !mIconBitmap.isRecycled() && mSize > 0) {
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public void setBgCorner(int progress) {
        this.mIcon.bgCorner = progress / 192f;
        this.setBgShape(mIcon.bgShape);
    }

    public int getBgColor() {
        return this.mIcon.bgColor == null ? 0xFFFF0000 : mIcon.bgColor;
    }

    public void setBgColor(Integer color) {
        this.mIcon.bgColor = color;
        mBgPaint.setColor(color == null ? 0 : color);
        mEarPaint.setColor(Utils.getEarColor(mIcon.bgColor));
        invalidate();
    }

    public void setIconSize(int progress) {
        this.mIcon.iconScale = Icon.ICON_SCALE_MIN + progress / 100f * Icon.ICON_SCALE_ADJ;
        if (mIconBitmap != null) {
            mIconBitmap.recycle();
        }
        if (mShadowBitmap != null && !mShadowBitmap.isRecycled()) {
            mShadowBitmap.recycle();
        }
        if (mSrcSVG != null && mSize > 0) {
            mIconBitmap = Utils.getBitmap(mSrcSVG, mSize, mIcon.iconScale, mBgPath);
        } else if (mSrcDrawable != null && mSize > 0) {
            mIconBitmap = Utils.getBitmap(mSrcDrawable, mSize, mIcon.iconScale, mBgPath);
        } else if (mSrcBitmap != null && mSize > 0) {
            mIconBitmap = Utils.getBitmap(mSrcBitmap, mSize, mIcon.iconScale, mBgPath);
        } else if (mSrcText != null && mSize > 0) {
            mIconBitmap = Utils.getBitmap(mSrcText, mSize, mIcon.iconScale, mBgPath);
        }
        if (mIconBitmap != null && !mIconBitmap.isRecycled()) {
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public int getIconColor() {
        return this.mIcon.iconColor == null ? 0xFFFF0000 : mIcon.iconColor;
    }

    public void setIconColor(Integer color) {
        this.mIcon.iconColor = color;
        mIconPaint.setColorFilter(this.mIcon.iconColor == null ? null
                : new PorterDuffColorFilter(this.mIcon.iconColor, PorterDuff.Mode.SRC_IN));
        invalidate();
    }

    public void setShadowAlpha(int progress) {
        this.mIcon.shadowAlpha = progress;
        mShadowPaint.setColorFilter(new PorterDuffColorFilter(progress << 24, PorterDuff.Mode.SRC_IN));
        invalidate();
    }

    public void toggleEffectScore() {
        this.mIcon.effectScore = !this.mIcon.effectScore;
        invalidate();
    }

    public void toggleEffectEar() {
        this.mIcon.effectEar = !this.mIcon.effectEar;
        mBgPath = Utils.getBgPath(mIcon.bgShape, mSize, mIcon.bgCorner, mIcon.effectEar);
        mBgLayerPath = Utils.getBgLayerPath(mBgPath, mSize);
        mEarPath = Utils.getEarPath(mBgPath, mSize);
        mEarShadowBitmap = Utils.getEarShadow(mEarPath, mSize, mBgPath, true);
        mScorePath = Utils.getScorePath(mSize, mBgPath);
        invalidate();
    }

    public void toggleEffectLines() {
        this.mIcon.showKeyLines = !this.mIcon.showKeyLines;
        invalidate();
    }

    public Bitmap getBitmap(int size) {
        Bitmap result = Utils.getBitmap(mSrcSVG, mSrcDrawable, mSrcBitmap, mSrcText,
                mIcon, size, mBgPaint, mBgLayerPaint, mShadowPaint, mIconPaint, mScorePaint, mEarPaint);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            draw(canvas);
        }
        return result;
    }

    public String save(String name, int size) throws IOException {
        return CacheHelper.saveBitmap(getBitmap(size), name);
    }

    public void save() {
        this.mIcon.put();
    }
}
