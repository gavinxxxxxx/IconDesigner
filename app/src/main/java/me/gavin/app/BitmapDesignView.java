package me.gavin.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
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
public class BitmapDesignView extends View {

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

    private Bitmap mBitmap, mShadowBitmap, mScoreBitmap;

    public BitmapDesignView(Context context, @Nullable AttributeSet attrs) {
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
        mShadowPaint.setColor(0x88000000);

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
        onDraw2(canvas);
    }

    private void onDraw2(Canvas canvas) {
        drawReset();

        canvas.drawCircle(mSize / 2f, mSize / 2f, mSize / 2f * BG_L_RATIO, mBgPaint);

        // mShadowPaint.setColorFilter(new PorterDuffColorFilter(mShadowPaint.getColor(), PorterDuff.Mode.SRC_IN));
        // canvas.drawBitmap(mShadowBitmap, 0, 0, mShadowPaint);

        canvas.drawBitmap(mBitmap, 0, 0, mIconPaint);

        if (mIcon.effectScore){
            canvas.drawArc(0, 0, mSize, mSize, 180f, 180f, true, mScorePaint);
        }
    }

    private void drawReset() {
        if (newTag) {
            newTag = false;

            mBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
            Canvas iconCanvas = new Canvas(mBitmap);

            mMatrix.reset();
            mInherentScale = mSvg.getInherentScale();
            // 比例同步
            mMatrix.postScale(mInherentScale, mInherentScale);
            mMatrix.postTranslate((mSize - mSvg.width) / 2f, (mSize - mSvg.height) / 2f);
            float scale = Math.min(mSize / mSvg.width, mSize / mSvg.height);
            mMatrix.postScale(scale, scale, mSize / 2f, mSize / 2f);
            // 当前缩放比
            mMatrix.postScale(scaleNow, scaleNow, mSize / 2f, mSize / 2f);

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


//            mShadowBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
//            Canvas shadowCanvas = new Canvas(mShadowBitmap);
//            Path path = new Path();
//            path.addCircle(mSize / 2f, mSize / 2f, mSize / 2f * BG_L_RATIO, Path.Direction.CCW);
//            shadowCanvas.clipPath(path);
//            // mShadowPaint.setColorFilter(new PorterDuffColorFilter(0xFF000000, PorterDuff.Mode.SRC_IN));
//            for (int i = 1; i <= 1024; i++) {
//                shadowCanvas.drawBitmap(mBitmap, i, i, mShadowPaint);
//            }


//            mScoreBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
//            Canvas scoreCanvas = new Canvas(mScoreBitmap);
//            scoreCanvas.drawA

        }
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
            mIconPaint.setStyle(Paint.Style.FILL);
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
        // TODO: 2017/12/4 此处应该为生成bitmap时缩放 不然质量很差
        canvas.scale(size * 1f / mSize, size * 1f / mSize, 0, 0);
        onDraw2(canvas);
        return bitmap;
    }

    public String save(String name, int size) throws IOException {
        return CacheHelper.saveBitmap(getBitmap(size), name);
    }

}
