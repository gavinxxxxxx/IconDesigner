package me.gavin.app.preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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

    private SVG mSvg;

    private final Icon mIcon;

    private Path mBgPath, mScorePath;

    private Bitmap mIconBitmap, mShadowBitmap;

    private final Paint mBgPaint, mIconPaint, mShadowPaint, mScorePaint;

    public PreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mIcon = new Icon();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mIcon.bgColor);
        mBgPaint.setShadowLayer(DisplayUtil.dp2px(mIcon.bgShadowLayer), 0,
                DisplayUtil.dp2px(mIcon.bgShadowLayer / 1.25f), 0x30000000);

        mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mIconPaint.setStyle(Paint.Style.FILL);
        mIconPaint.setColor(0xFFFFFFFF);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColorFilter(new PorterDuffColorFilter(0x30000000, PorterDuff.Mode.SRC_IN));

        mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mScorePaint.setStyle(Paint.Style.FILL);
        mScorePaint.setColor(0x18181818);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 取最大正方形
        mSize = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(mSize, mSize);
        if (mSvg != null && mSize > 0) {
            mBgPath = Utils.getBgPath(mIcon.bgShape, mSize, mIcon.bgCorner);
            mIconBitmap = Utils.SVGToBitmap(mSvg, mSize, mIcon.iconScale);
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            mScorePath = Utils.getScorePath(mSize, mBgPath);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSvg == null || mSize <= 0) return;

        canvas.drawPath(mBgPath, mBgPaint);

        canvas.drawBitmap(mShadowBitmap, 0, 0, mShadowPaint);

        canvas.drawBitmap(mIconBitmap, 0, 0, mIconPaint);

        if (mIcon.effectScore) {
            canvas.drawPath(mScorePath, mScorePaint);
        }
    }

    public void setSVG(SVG mSvg) {
        this.mSvg = mSvg;
        if (mSvg != null && mSize > 0) {
            mIconBitmap = Utils.SVGToBitmap(mSvg, mSize, mIcon.iconScale);
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            invalidate();
        }
    }

    public void setBgShape(int shape) {
        this.mIcon.bgShape = shape;
        if (mSvg != null && mSize > 0) {
            mBgPath = Utils.getBgPath(mIcon.bgShape, mSize, mIcon.bgCorner);
            mShadowBitmap = Utils.getShadow(mIconBitmap, mSize, mBgPath, true);
            mScorePath = Utils.getScorePath(mSize, mBgPath);
            invalidate();
        }
    }

    public int getBgColor() {
        return this.mIcon.bgColor == null ? 0xFFFF0000 : mIcon.bgColor;
    }

    public void setBgColor(Integer color) {
        this.mIcon.bgColor = color;
        mBgPaint.setColor(color == null ? 0 : color);
        invalidate();
    }

    public void setIconSize(float progress) {
        this.mIcon.iconScale = 0.3f + progress / 100f * 0.4f;
        if (mSvg != null && mSize > 0) {
            mIconBitmap = Utils.SVGToBitmap(mSvg, mSize, mIcon.iconScale);
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

    public Bitmap getBitmap(int size) {
        return Utils.getBitmap(mSvg, mIcon, size, mBgPaint, mShadowPaint, mIconPaint, mScorePaint);
    }

    public String save(String name, int size) throws IOException {
        return CacheHelper.saveBitmap(getBitmap(size), name);
    }

}
