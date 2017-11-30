package me.gavin.widget.color.picker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Locale;

/**
 * HSLColorPicker
 *
 * @author gavin.xiong 2017/11/27
 */
public class HSLColorPicker extends View {

    private final int[] COLORS = new int[]{
            0xFFFF0000,
            0xFFFF00FF,
            0xFF0000FF,
            0xFF00FFFF,
            0xFF00FF00,
            0xFFFFFF00,
            0xFFFF0000,
    };

    private final static int TOUCH_FLAG_H = 0;
    private final static int TOUCH_FLAG_S = 1;
    private final static int TOUCH_FLAG_L = 2;
    private final static int TOUCH_FLAG_A = 3;

    private final float[] mHSL = {0f, 1f, 0.5f, 1f}, mHSL2 = {0f, 1f, 0.5f, 1f}, mHSLTemp = new float[mHSL.length];

    private float mHStoreWidth = DisplayUtil.dp2px(8), mLineWidth = DisplayUtil.dp2px(3);
    private float mCx, mCy, mOr, mIr;
    private int mLineHMargin, mLineVMargin = DisplayUtil.dp2px(42);

    private final Paint mHPaint, mSPaint, mLPaint, mAPaint, mCoverPaint, mPaint, mTextPaint;

    private boolean mWithAlpha = true;

    private int mTouchFlag = -1;

    public HSLColorPicker(Context context) {
        this(context, null);
    }

    public HSLColorPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HSLColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHPaint.setStyle(Paint.Style.STROKE);
        mHPaint.setStrokeCap(Paint.Cap.ROUND);
        mHPaint.setShader(new SweepGradient(mCx, mCy, COLORS, null));

        mSPaint = new Paint(mHPaint);
        mLPaint = new Paint(mSPaint);
        mAPaint = new Paint(mSPaint);

        mCoverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCoverPaint.setColor(0x40000000);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ColorUtils.HSLToColor(mHSL));

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = Math.min(width + mLineVMargin * (mWithAlpha ? 3 : 2), MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCx = w / 2f;
        mCy = (h - mLineVMargin * (mWithAlpha ? 3 : 2)) / 2f;
        mOr = Math.min(mCx, mCy) - mHStoreWidth * 2f + mHStoreWidth / 2f;
        mIr = mOr - mHStoreWidth;
        mLineHMargin = (int) (w / 2f - mOr + mLineWidth / 2f);
        mHPaint.setStrokeWidth(mHStoreWidth);

        mSPaint.setStrokeWidth(mLineWidth);
        mLPaint.setStrokeWidth(mLineWidth);
        mAPaint.setStrokeWidth(mLineWidth);

        resetLinePaint(null, -1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // H 渐变色圆环
        canvas.drawCircle(mCx, mCy, mOr / 2f + mIr / 2f, mHPaint);

        // 圆形填充
        mPaint.setColor(HSLToColor(mHSL));
        canvas.drawCircle(mCx, mCy, mIr + 0.5f, mPaint);

        // 圆环选择状态
        mHSL2[0] = mHSL[0];
        mPaint.setColor(ColorUtils.HSLToColor(mHSL2));
        double radian = Math.toRadians(mHSL[0]);
        float x = (float) (mCx + (mOr + mIr) / 2 * Math.cos(radian));
        float y = (float) (mCy - (mOr + mIr) / 2 * Math.sin(radian));
        canvas.drawCircle(x, y, mHStoreWidth * 2f, mCoverPaint);
        canvas.drawCircle(x, y, mHStoreWidth, mPaint);

        // 文字
        mTextPaint.setTextSize(mIr / 5f);
        int baseY = (int) (mCy - mTextPaint.descent() / 2 - mTextPaint.ascent() / 2);
        String textHex = String.format(mWithAlpha ? "#%08X" : "#%06X",
                mWithAlpha ? HSLToColor(mHSL) : ColorUtils.HSLToColor(mHSL));
        canvas.drawText(textHex, mCx, baseY, mTextPaint);
        mTextPaint.setTextSize(mTextPaint.getTextSize() / 2.5f);
        String textHSL = String.format(Locale.getDefault(),
                mWithAlpha ? "%3d° %3d%% %3d%% %3d%%" : "%3d° %3d%% %3d%%",
                Math.round(mHSL[0]), Math.round(mHSL[1] * 100), Math.round(mHSL[2] * 100),
                mWithAlpha ? Math.round(mHSL[3] * 100) : null);
        canvas.drawText(textHSL, mCx, baseY + mTextPaint.getTextSize() * 2, mTextPaint);

        // 线性调节器
        float a = mCy + mOr;
        canvas.drawLine(mLineHMargin, a + mLineVMargin, getMeasuredWidth() - mLineHMargin, a + mLineVMargin, mSPaint);
        canvas.drawCircle(mLineHMargin + mHSL[1] * (getMeasuredWidth() - mLineHMargin * 2), a + mLineVMargin, mLineWidth * 2.5f, mCoverPaint);
        canvas.drawCircle(mLineHMargin + mHSL[1] * (getMeasuredWidth() - mLineHMargin * 2), a + mLineVMargin, mLineWidth, mPaint);
        canvas.drawLine(mLineHMargin, a + mLineVMargin * 2, getMeasuredWidth() - mLineHMargin, a + mLineVMargin * 2, mLPaint);
        canvas.drawCircle(mLineHMargin + mHSL[2] * (getMeasuredWidth() - mLineHMargin * 2), a + mLineVMargin * 2, mLineWidth * 2.5f, mCoverPaint);
        canvas.drawCircle(mLineHMargin + mHSL[2] * (getMeasuredWidth() - mLineHMargin * 2), a + mLineVMargin * 2, mLineWidth, mPaint);
        if (mWithAlpha) {
            canvas.drawLine(mLineHMargin, a + mLineVMargin * 3, getMeasuredWidth() - mLineHMargin, a + mLineVMargin * 3, mAPaint);
            canvas.drawCircle(mLineHMargin + mHSL[3] * (getMeasuredWidth() - mLineHMargin * 2), a + mLineVMargin * 3, mLineWidth * 2.5f, mCoverPaint);
            canvas.drawCircle(mLineHMargin + mHSL[3] * (getMeasuredWidth() - mLineHMargin * 2), a + mLineVMargin * 3, mLineWidth, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                double distance = Math.sqrt(Math.pow(mCx - event.getX(), 2) + Math.pow(mCy - event.getY(), 2));
                if (Math.abs(distance - (mOr + mIr) / 2f) < mHStoreWidth * 3) {
                    mTouchFlag = TOUCH_FLAG_H;
                } else if (Math.abs(mCy + mOr + mLineVMargin - event.getY()) < mLineWidth * 3) {
                    mTouchFlag = TOUCH_FLAG_S;
                } else if (Math.abs(mCy + mOr + mLineVMargin * 2 - event.getY()) < mLineWidth * 3) {
                    mTouchFlag = TOUCH_FLAG_L;
                } else if (mWithAlpha && Math.abs(mCy + mOr + mLineVMargin * 3 - event.getY()) < mLineWidth * 3) {
                    mTouchFlag = TOUCH_FLAG_A;
                }
                if (mTouchFlag >= 0) {
                    resetLinePaint(event, mTouchFlag);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                resetLinePaint(event, mTouchFlag);
                return true;
            default:
                mTouchFlag = -1;
                return false;
        }
    }

    private void resetLinePaint(MotionEvent event, int index) {
        if (event != null && index == 0) {
            mHSL[0] = getAngle(event.getX() - mCx, event.getY() - mCy);
        } else if (event != null && index > 0) {
            float progress = (event.getX() - mLineHMargin) / (getMeasuredWidth() - mLineHMargin * 2);
            mHSL[index] = progress < 0 ? 0 : progress > 1 ? 1 : progress;
        }
        // TODO: 2017/11/30 H 颜色跟随？
//        for (int i = 0; i < COLORS.length; i++) {
//            COLORS[i] &= 0x00FFFFFF;
//            COLORS[i] |= (Math.round(0xFF * mHSL[3]) << 24);
//        }
//        mHPaint.setShader(new SweepGradient(mCx, mCy, COLORS, null));

        resetSLPaint(mSPaint, 1);
        resetSLPaint(mLPaint, 2);
        if (mWithAlpha) {
            System.arraycopy(mHSL, 0, mHSLTemp, 0, mHSL.length);
            int color = ColorUtils.HSLToColor(mHSLTemp);
            mAPaint.setShader(new LinearGradient(mLineHMargin, 0, getMeasuredWidth() - mLineHMargin, 0,
                    color & 0x00FFFFFF, color | 0xFF000000, Shader.TileMode.CLAMP));
        }
        mTextPaint.setColor(ColorUtils.calculateLuminance(HSLToColor(mHSL)) > 0.5f ? 0xFF000000 : 0xFFFFFFFF);
        invalidate();
    }

    private void resetSLPaint(Paint paint, int index) {
        System.arraycopy(mHSL, 0, mHSLTemp, 0, mHSL.length);
        mHSLTemp[index] = 0f;
        int startColor = HSLToColor(mHSLTemp);
        mHSLTemp[index] = .5f;
        int cColor = HSLToColor(mHSLTemp);
        mHSLTemp[index] = 1f;
        int endColor = HSLToColor(mHSLTemp);
        paint.setShader(new LinearGradient(mLineHMargin, 0, getMeasuredWidth() - mLineHMargin, 0,
                new int[]{startColor, cColor, endColor}, null, Shader.TileMode.CLAMP));
    }

    private int HSLToColor(float[] hsl) {
        return ColorUtils.HSLToColor(hsl) & ((Math.round(0xFF * hsl[3]) << 24) + 0xFFFFFF);
    }

    /**
     * 获取偏移角度
     *
     * @param dx end.x - start.x
     * @param dy end.y - start.y
     * @return [0, 360)
     */
    private int getAngle(float dx, float dy) {
        double radian = Math.atan2(dy, dx);
        double degrees = Math.toDegrees(radian);
        int angle = (int) Math.round(degrees);
        return angle > 0 ? 360 - angle : -angle;
    }

    public void setColor(int color) {
        ColorUtils.colorToHSL(color, mHSL);
        mHSL[3] = (color >>> 24) * 1f / 0xFF;
        resetLinePaint(null, -1);
    }

    public int getColor() {
        return HSLToColor(mHSL);
    }
}
