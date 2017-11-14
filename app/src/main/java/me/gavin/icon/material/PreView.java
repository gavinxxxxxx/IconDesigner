package me.gavin.icon.material;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.io.OutputStream;

import me.gavin.svg.model.SVG;
import me.gavin.util.DisplayUtil;
import me.gavin.util.L;

/**
 * 预览
 *
 * @author gavin.xiong 2017/11/8
 */
public class PreView extends View {

    private final float BG_CIRCLE_RA = 176f / 192f;
    private final float BG_RECT_RA = 152f / 192f;

    private int mSize;

    private final Paint mBgPaintLight, mBgPaintDark;

    public SVG mSvg;
    private boolean newTag;
    private final Matrix mMatrix = new Matrix();
    private float mInherentScale;


    private Icon mIcon;

    private Path mBgPath = new Path(), mShadowPath, mShadowPath2 = new Path();

    private Matrix mBgMatrix = new Matrix();

    private Paint mBgPaint, mShadowPaint, mIconPaint;

    private float scaleNow = 0.65f;

    public PreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mBgPaintLight = new Paint();
        mBgPaintLight.setColor(0xff888888);
        mBgPaintDark = new Paint();
        mBgPaintDark.setColor(0xff555555);

        mIcon = new Icon();
        mIcon.bgShape = 1;
        mBgPaint = new Paint();
        // mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mIcon.bgColor);
        mBgPaint.setShadowLayer(5, 0, 2, 0x55555555);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        // mShadowPaint.setShader(new LinearGradient(0, 0, 23, 23, 0x50000000, 0x10000000, Shader.TileMode.CLAMP));
        mShadowPaint.setShader(new LinearGradient(0, 0, 23, 23, 0xFFFFFF00, 0x20FFFF00, Shader.TileMode.CLAMP));
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
//        initBgPath();
    }

    private void initBgPath() {
        mBgPath.reset();
        if (mIcon.bgShape == 0) {
            float padding = mSize / 2 - mSize * BG_RECT_RA / 2;
            mBgPath.addRoundRect(padding, padding, mSize - padding, mSize - padding, mSize / 20, mSize / 20, Path.Direction.CCW);
        } else {
            mBgPath.addCircle(mSize / 2, mSize / 2, mSize / 2 * BG_CIRCLE_RA, Path.Direction.CCW);
        }
    }

    // TODO: 2017/11/8 canvas -> bitmap -> png
    @Override
    protected void onDraw(Canvas canvas) {
        // canvas.drawColor(0xffffffff);
        drawBackground(canvas);

        if (mSvg == null) return;

        drawBefore();

        canvas.setMatrix(mMatrix);

        if (mSvg.width / mSvg.height != mSvg.viewBox.width / mSvg.viewBox.height) {
            canvas.translate(
                    mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                            ? (mSvg.width - mSvg.viewBox.width / mSvg.viewBox.height * mSvg.height) / 2 / mInherentScale : 0,
                    mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                            ? 0 : (mSvg.height - mSvg.viewBox.height / mSvg.viewBox.width * mSvg.width) / 2 / mInherentScale);
        }

        canvas.drawPath(mBgPath, mBgPaint);

        mShadowPath2.set(mShadowPath);
        mShadowPath2.op(mBgPath, Path.Op.INTERSECT);
        canvas.drawPath(mShadowPath2, mShadowPaint);

        for (int i = 0; i < mSvg.paths.size(); i++) {
            canvas.drawPath(mSvg.paths.get(i), mIconPaint != null ? mIconPaint : mSvg.drawables.get(i).getFillPaint());
            // canvas.drawPath(mSvg.paths.get(i), mSvg.drawables.get(i).getStrokePaint());
        }
    }

    private void drawBackground(Canvas canvas) {
        int cw = 33;
        for (int x = 0; x < mSize; x += cw) {
            for (int y = 0; y < mSize; y += cw) {
                canvas.drawRect(x, y, x + cw, y + cw, ((x ^ y) & 1) == 1 ? mBgPaintDark : mBgPaintLight);
            }
        }
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

            mBgPath.reset();
            mBgPath.addCircle(mSvg.viewBox.width / 2f, mSvg.viewBox.height / 2f, mSvg.viewBox.width / 2f / 0.8f * BG_CIRCLE_RA / scaleNow, Path.Direction.CCW);
            // 当前缩放比
            mMatrix.postScale(scaleNow, scaleNow, mSize / 2f, mSize / 2f);
            // 阴影初始化
            mShadowPath = new Path();
            for (int i = 0; i < mSvg.paths.size(); i++) {
                if (mSvg.drawables.get(i).getFillPaint().getColor() != 0) {
                    for (float j = 0; j < 40; j += 1.1) {
                        Path path = new Path(mSvg.paths.get(i));
                        path.offset(j, j);
                        mShadowPath.op(path, Path.Op.UNION);
                    }
                }
            }
            // mShadowPath.op(mBgPath, Path.Op.INTERSECT);
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

    public void setScale(int progress) {
        L.e(progress);
        float aimScale = 0.3f + progress / 100f * 0.7f;
        mMatrix.postScale(aimScale / scaleNow, aimScale / scaleNow, mSize / 2f, mSize / 2f);
        mBgMatrix.reset();
        mBgMatrix.postScale(scaleNow / aimScale, scaleNow / aimScale, mSvg.viewBox.width / 2f, mSvg.viewBox.width / 2f);
        mBgPath.transform(mBgMatrix);
        invalidate();
        scaleNow = aimScale;
    }

    public void save(OutputStream outputStream) {
        Bitmap bitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
//        try (fos) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//            L.e(e);
//        }
    }

}
