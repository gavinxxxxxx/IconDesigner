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

import me.gavin.icon.material.util.DisplayUtil;
import me.gavin.icon.material.util.Icon;
import me.gavin.svg.model.Drawable;
import me.gavin.svg.model.SVG;

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

    private Icon mIcon;

    private Path mBgPath = new Path();

    private Paint mBgPaint;

    private Paint mPaint;

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
        mIcon.bgShape = 1;
        mBgPaint = new Paint();
        // mBgPaint.setStyle(Paint.Style.STROKE);
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
        initBgPath();
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

        canvas.drawPath(mBgPath, mBgPaint);

        if (mSvg != null) {
            drawBefore();

            // 0.4 - 1.0
            // mMatrix.postScale(1f, 1f, mSize / 2, mSize / 2);

            canvas.setMatrix(mMatrix);

            if (mSvg.width / mSvg.height != mSvg.viewBox.width / mSvg.viewBox.height) {
                canvas.translate(
                        mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                                ? (mSvg.width - mSvg.viewBox.width / mSvg.viewBox.height * mSvg.height) / 2 / mInherentScale : 0,
                        mSvg.width / mSvg.height > mSvg.viewBox.width / mSvg.viewBox.height
                                ? 0 : (mSvg.height - mSvg.viewBox.height / mSvg.viewBox.width * mSvg.width) / 2 / mInherentScale);
            }

            for (int i = 0; i < mSvg.paths.size(); i++) {
                Drawable drawable = mSvg.drawables.get(i);
                if (drawable.getFillPaint().getColor() != 0) {
                    draw(canvas, mSvg.paths.get(i));
                    canvas.drawPath(mSvg.paths.get(i), mPaint != null ? mPaint : drawable.getFillPaint());
                }
                // canvas.drawPath(mSvg.paths.get(i), drawable.getStrokePaint());
            }
        }
    }

    private void draw(Canvas canvas, Path src) {
        Path path = new Path(src);
        for (float i = 0; i < 25; i += 0.5) {
            Path path1 = new Path(src);
            path1.offset(i, i);
            path.op(path1, Path.Op.UNION);
        }
        // path.op(mBgPath, Path.Op.INTERSECT);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(new LinearGradient(0, 0, 23, 23, 0x30000000, 0x00000000, Shader.TileMode.CLAMP));
        mPaint.setStrokeWidth(0.2f);
        mPaint.setColor(Color.YELLOW);
        canvas.drawPath(path, mPaint);
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
        invalidate();
    }

    public void setIconColor(Integer color) {
        if (color == null) {
            mPaint = null;
        } else {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(color);
        }
        invalidate();
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
