package me.gavin.app.preview;

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

    private final Path mBgPath = new Path();

    private final Paint mBgPaint, mIconPaint;

    private Bitmap mIconBitmap, mShadowBitmap;

    private final Matrix mMatrix = new Matrix();

    public PreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mIcon = new Icon();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mIcon.bgColor);
        mBgPaint.setShadowLayer(DisplayUtil.dp2px(mIcon.bgShadowLayer), 0,
                DisplayUtil.dp2px(mIcon.bgShadowLayer / 1.25f), 0x40000000);

        mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIconPaint.setStyle(Paint.Style.FILL);
        mIconPaint.setColor(0xFFFFFFFF);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 取最大正方形
        mSize = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(mSize, mSize);
        resetBgPath();
        resetIconPath();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSvg == null || mSize <= 0) return;

        canvas.drawPath(mBgPath, mBgPaint);

        canvas.drawBitmap(mIconBitmap, 0, 0, mIconPaint);
    }

    private void resetBgPath() {
        if (mSvg == null || mSize <= 0) return;
        mBgPath.reset();
        float half = mSize / 2f;
        float corner = DisplayUtil.dp2px(mIcon.bgCorner);
        if (mIcon.bgShape == 0) {
            float hs = half * Icon.BG_M_RATIO;
            mBgPath.addRoundRect(half - hs, half - hs, half + hs, half + hs,
                    corner, corner, Path.Direction.CCW);
        } else if (mIcon.bgShape == 1) {
            mBgPath.addCircle(half, half, half * Icon.BG_L_RATIO, Path.Direction.CCW);
        } else if (mIcon.bgShape == 2) {
            float hhs = half * Icon.BG_L_RATIO;
            float hvs = half * Icon.BG_S_RATIO;
            mBgPath.addRoundRect(half - hhs, half - hvs, half + hhs, half + hvs,
                    corner, corner, Path.Direction.CCW);
        } else if (mIcon.bgShape == 3) {
            float hhs = half * Icon.BG_S_RATIO;
            float hvs = half * Icon.BG_L_RATIO;
            mBgPath.addRoundRect(half - hhs, half - hvs, half + hhs, half + hvs,
                    corner, corner, Path.Direction.CCW);
        }
    }

    private void resetIconPath() {
        if (mSvg == null || mSize <= 0) return;
        mIconBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas iconCanvas = new Canvas(mIconBitmap);

        mMatrix.reset();
        float mInherentScale = mSvg.getInherentScale();
        // 比例同步
        mMatrix.postScale(mInherentScale, mInherentScale);
        mMatrix.postTranslate((mSize - mSvg.width) / 2f, (mSize - mSvg.height) / 2f);
        float scale = Math.min(mSize / mSvg.width, mSize / mSvg.height);
        mMatrix.postScale(scale, scale, mSize / 2f, mSize / 2f);
        // 当前缩放比
        mMatrix.postScale(mIcon.iconScale, mIcon.iconScale, mSize / 2f, mSize / 2f);

        iconCanvas.setMatrix(mMatrix);

        if (mSvg.width / mSvg.height != mSvg.viewBox.width / mSvg.viewBox.height) {
            iconCanvas.translate(
                    mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                            ? (mSvg.width - mSvg.viewBox.width / mSvg.viewBox.height * mSvg.height) / 2 / mInherentScale : 0,
                    mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                            ? 0 : (mSvg.height - mSvg.viewBox.height / mSvg.viewBox.width * mSvg.width) / 2 / mInherentScale);
        }

        for (int i = 0; i < mSvg.paths.size(); i++) {
            if (mSvg.drawables.get(i).getFillPaint().getColor() != 0) {
                iconCanvas.drawPath(mSvg.paths.get(i), mIconPaint != null ? mIconPaint : mSvg.drawables.get(i).getFillPaint());
            }
        }
    }

    public void setSVG(SVG mSvg) {
        this.mSvg = mSvg;
        resetIconPath();
        invalidate();
    }

    public void setBgShape(int shape) {
        this.mIcon.bgShape = shape;
        resetBgPath();
        invalidate();
    }

    public int getBgColor() {
        return mIcon.bgColor;
    }

    public void setBgColor(Integer color) {
        this.mIcon.bgColor = color;
        invalidate();
    }

    public void setIconSize(float progress) {
        float scale = 0.3f + progress / 100f * 0.4f;
        // mMatrix.postScale(scale / scaleNow, scale / scaleNow, mSize / 2f, mSize / 2f);
        invalidate();
        this.mIcon.iconScale = scale;
    }

    public int getIconColor() {
        return mIcon.iconColor;
    }

    public void setIconColor(Integer color) {
        this.mIcon.iconColor = color;
        invalidate();
    }

    public void setShadowAlpha(int progress) {
        this.mIcon.shadowAlpha = progress;
    }

    public void toggleEffectScore() {
        this.mIcon.effectScore = !this.mIcon.effectScore;
        invalidate();
    }

    public Bitmap getBitmap(int size) {
        // TODO: 2017/12/4
        return null;
    }

    public String save(String name, int size) throws IOException {
        return CacheHelper.saveBitmap(getBitmap(size), name);
    }

}
