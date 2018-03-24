package me.gavin.app.preview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.ColorUtils;

import me.gavin.svg.model.SVG;
import me.gavin.util.DisplayUtil;

/**
 * Utils
 *
 * @author gavin.xiong 2017/12/5
 */
class Utils {

    static Path getKeyLines(int size) {
        Path path = new Path();

        float half = 48 / 2f;
        float hla = Icon.BG_L_RATIO * half;
        float hma = Icon.BG_M_RATIO * half;
        float hsa = Icon.BG_S_RATIO * half;
        float c = Icon.BG_C_RATIO * 48;

        path.addRoundRect(half - hma, half - hma, half + hma, half + hma, c, c, Path.Direction.CCW);
        path.addCircle(half, half, hla, Path.Direction.CCW);
        path.addRoundRect(half - hsa, half - hla, half + hsa, half + hla, c, c, Path.Direction.CCW);
        path.addRoundRect(half - hla, half - hsa, half + hla, half + hsa, c, c, Path.Direction.CCW);

        path.moveTo(6, 6);
        path.rLineTo(36, 36);
        path.rMoveTo(-36, 0);
        path.rLineTo(36, -36);

        path.moveTo(2, 17);
        path.rLineTo(44, 0);
        path.rMoveTo(0, 7);
        path.rLineTo(-44, 0);
        path.rMoveTo(0, 7);
        path.rLineTo(44, 0);

        path.moveTo(17f, 2);
        path.rLineTo(0, 44);
        path.rMoveTo(7f, 0);
        path.rLineTo(0, -44);
        path.rMoveTo(7f, 0);
        path.rLineTo(0, 44);

        path.addCircle(half, half, 10, Path.Direction.CCW);

        path.transform(getMatrix(size));

        return path;
    }

    private static Matrix getMatrix(int size) {
        Matrix matrix = new Matrix();
        matrix.postScale(size / 48f, size / 48f, 24, 24);
        matrix.postTranslate(size / 2f - 24, size / 2f - 24);
        return matrix;
    }

    static Path getBgPath(Icon icon, int size) {
        Path mBgPath = new Path();
        float half = size / 2f;
        float corner = size * icon.bgCorner;
        if (icon.bgShape == 0) {
            float hs = half * Icon.BG_M_RATIO;
            mBgPath.addRoundRect(half - hs, half - hs, half + hs, half + hs,
                    corner, corner, Path.Direction.CCW);
        } else if (icon.bgShape == 1) {
            mBgPath.addCircle(half, half, half * Icon.BG_L_RATIO, Path.Direction.CCW);
        } else if (icon.bgShape == 2) {
            float hhs = half * Icon.BG_S_RATIO;
            float hvs = half * Icon.BG_L_RATIO;
            mBgPath.addRoundRect(half - hhs, half - hvs, half + hhs, half + hvs,
                    corner, corner, Path.Direction.CCW);
        } else if (icon.bgShape == 3) {
            float hhs = half * Icon.BG_L_RATIO;
            float hvs = half * Icon.BG_S_RATIO;
            mBgPath.addRoundRect(half - hhs, half - hvs, half + hhs, half + hvs,
                    corner, corner, Path.Direction.CCW);
        }

        if (icon.effectEar && icon.bgShape != 1) {
            Path path = new Path();
            path.moveTo(size - Icon.BG_E_RATIO * size, 0);
            path.rLineTo(Icon.BG_E_RATIO * size, Icon.BG_E_RATIO * size);
            path.lineTo(size, 0);
            path.close();
            mBgPath.op(path, Path.Op.DIFFERENCE);
        }

        return mBgPath;
    }

    static Path getEarPath(Path mBgPath, int size) {
        Path path = new Path();
        path.lineTo(Icon.BG_E_RATIO * size, 0);
        path.lineTo(0, Icon.BG_E_RATIO * size);
        path.close();
        path.op(mBgPath, Path.Op.INTERSECT);

        RectF rect = new RectF();
        path.computeBounds(rect, false);
        Matrix matrix = new Matrix();
        matrix.postRotate(-90f, rect.centerX(), rect.centerY());
        matrix.postTranslate(size - rect.left - rect.right, 0);
        path.transform(matrix);

        return path;
    }

    static int getEarColor(Integer bgColor) {
        if (bgColor == null) return 0;
        float[] hsl = new float[3];
        ColorUtils.colorToHSL(bgColor, hsl);
        hsl[2] = Math.min(hsl[2] + 0.1f, 1f);
        return ColorUtils.HSLToColor(hsl);
    }

    static Bitmap getEarShadow(@NonNull Path mEarPath, int mSize, @NonNull Path mBgPath, boolean preview) {
        Bitmap mShadowBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas shadowCanvas = new Canvas(mShadowBitmap);
        shadowCanvas.clipPath(mBgPath);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);

        Bitmap earBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas earCanvas = new Canvas(earBitmap);
        earCanvas.drawPath(mEarPath, paint);

        for (int i = 1; i <= mSize; i += preview ? 2 : 1) {
            shadowCanvas.drawBitmap(earBitmap, i, i, paint);
        }
        return mShadowBitmap;
    }

    static Path getBgLayerPath(Path mBgPath, int mSize) {
        Path path = new Path(mBgPath);
        Matrix matrix = new Matrix();
        matrix.postScale(1 - Icon.BG_SL_RATIO, 1 + Icon.BG_SL_RATIO / 4f, mSize / 2f, mSize / 2f);
        path.transform(matrix);
        return path;
    }

    static void setBgShadowLayer(Paint bgLayerPaint, int size, int bgColor) {
        bgLayerPaint.setShadowLayer(size * Icon.BG_SL_RATIO, 0,
                DisplayUtil.dp2px(size * Icon.BG_SL_RATIO / 4f),
                (bgColor >>> 24) * 0x50 / 0xFF << 24);
    }

    static Bitmap getShadow(@NonNull Bitmap mIconBitmap, int mSize, float length, @NonNull Path mBgPath, boolean preview) {
        Bitmap mShadowBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas shadowCanvas = new Canvas(mShadowBitmap);
        shadowCanvas.clipPath(mBgPath);
        Paint mIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mIconPaint.setStyle(Paint.Style.FILL);
        float a = mSize * length;
        for (int i = 1; i <= a; i += preview ? 2 : 1) {
            shadowCanvas.drawBitmap(mIconBitmap, i, i, mIconPaint);
        }
        return mShadowBitmap;
    }

    static Path getScorePath(int mSize, @NonNull Path mBgPath) {
        Path path = new Path();
        path.addRect(0, 0, mSize, mSize / 2f, Path.Direction.CCW);
        path.op(mBgPath, Path.Op.INTERSECT);
        return path;
    }

    static Bitmap getBitmap(@NonNull SVG mSvg, int mSize, float iconScale, @NonNull Path mBgPath) {
        Bitmap mIconBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
        Canvas iconCanvas = new Canvas(mIconBitmap);
        iconCanvas.clipPath(mBgPath);

        Matrix mMatrix = new Matrix();
        float mInherentScale = mSvg.getInherentScale();
        // 比例同步
        mMatrix.postScale(mInherentScale, mInherentScale);
        mMatrix.postTranslate((mSize - mSvg.width) / 2f, (mSize - mSvg.height) / 2f);
        float scale = Math.min(mSize / mSvg.width, mSize / mSvg.height);
        mMatrix.postScale(scale, scale, mSize / 2f, mSize / 2f);
        // 当前缩放比
        mMatrix.postScale(iconScale, iconScale, mSize / 2f, mSize / 2f);
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
                iconCanvas.drawPath(mSvg.paths.get(i), mSvg.drawables.get(i).getFillPaint());
            }
        }
        return mIconBitmap;
    }

    static Bitmap getBitmap(Drawable drawable, int size, float scale, Path mBgPath) {
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.clipPath(mBgPath);
        int a = (int) (size * scale / 2f);
        drawable.setBounds(size / 2 - a, size / 2 - a, size / 2 + a, size / 2 + a);
        drawable.draw(canvas);
        return result;
    }

    static Bitmap getBitmap(Bitmap bitmap, int size, float iconScale, Path mBgPath) {
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.clipPath(mBgPath);
        Matrix matrix = new Matrix();
        matrix.postTranslate((size - bitmap.getWidth()) / 2f, (size - bitmap.getHeight()) / 2f);
        float scale = size * 1f / Math.min(bitmap.getWidth(), bitmap.getHeight());
        matrix.postScale(scale, scale, size / 2f, size / 2f);
        matrix.postScale(iconScale, iconScale, size / 2f, size / 2f);
        canvas.drawBitmap(bitmap, matrix, new Paint());
        return result;
    }

    static Bitmap getBitmap(String text, int size, float iconScale, Path mBgPath) {
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.clipPath(mBgPath);
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xFF000000);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        mTextPaint.setTextSize(size * iconScale / text.length());
        int baseY = (int) (size / 2 - mTextPaint.descent() / 2 - mTextPaint.ascent() / 2);
        canvas.drawText(text, size / 2, baseY, mTextPaint);
        return result;
    }

    static Bitmap getBitmap(SVG mSvg, Drawable mSrcDrawable, Bitmap mSrcBitmap, String mSrcText, Icon mIcon, int size,
                            Paint mBgPaint, Paint mBgLayerPaint, Paint mShadowPaint, Paint mIconPaint, Paint mScorePaint, Paint mEarPaint) {
        // 创建 bitmap
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 背景 Path
        Path mBgPath = Utils.getBgPath(mIcon, size);
        // 背景阴影 Path
        Path mBgLayerPath = Utils.getBgLayerPath(mBgPath, size);

        // 背景阴影画笔
        Paint mBgLayerPaint2 = new Paint(mBgLayerPaint);
        Utils.setBgShadowLayer(mBgLayerPaint2, size, mIcon.bgColor);
        // 画背景阴影
        canvas.drawPath(mBgLayerPath, mBgLayerPaint2);
        // 画背景
        canvas.drawPath(mBgPath, mBgPaint);

        // 前景
        Bitmap mIconBitmap;
        if (mSvg != null) {
            mIconBitmap = Utils.getBitmap(mSvg, size, mIcon.iconScale, mBgPath);
        } else if (mSrcDrawable != null) {
            mIconBitmap = Utils.getBitmap(mSrcDrawable, size, mIcon.iconScale, mBgPath);
        } else if (mSrcBitmap != null) {
            mIconBitmap = Utils.getBitmap(mSrcBitmap, size, mIcon.iconScale, mBgPath);
        } else if (mSrcText != null) {
            mIconBitmap = Utils.getBitmap(mSrcText, size, mIcon.iconScale, mBgPath);
        } else {
            bitmap.recycle();
            return null;
        }

        // 前景阴影
        Bitmap mShadowBitmap = Utils.getShadow(mIconBitmap, size, mIcon.shadowLength, mBgPath, false);
        // 画前景阴影
        canvas.drawBitmap(mShadowBitmap, 0, 0, mShadowPaint);
        // 画前景
        canvas.drawBitmap(mIconBitmap, 0, 0, mIconPaint);
        mIconBitmap.recycle();
        mShadowBitmap.recycle();

        // 狗耳
        if (mIcon.bgShape != 1 && mIcon.effectEar) {
            Path mEarPath = Utils.getEarPath(mBgPath, size);
            Bitmap mEarShadow = Utils.getEarShadow(mEarPath, size, mBgPath, false);
            canvas.drawBitmap(mEarShadow, 0, 0, mShadowPaint);
            canvas.drawPath(mEarPath, mEarPaint);
        }

        // 画折痕
        if (mIcon.effectScore) {
            Path mScorePath = Utils.getScorePath(size, mBgPath);
            canvas.drawPath(mScorePath, mScorePaint);
        }

        return bitmap;
    }
}
