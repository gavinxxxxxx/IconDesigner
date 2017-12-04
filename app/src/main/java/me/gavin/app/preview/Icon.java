package me.gavin.app.preview;

/**
 * Icon
 *
 * @author gavin.xiong 2017/12/4
 */
public class Icon {

    public static final float BG_L_RATIO = 176f / 192f;
    public static final float BG_M_RATIO = 152f / 192f;
    public static final float BG_S_RATIO = 128f / 192f;

    public final int COLOR_BG = 0xFF26A69A;

    public int bgShape = 0; // 0:圆角矩形 1:圆形 2: 竖直矩形 3：水平矩形
    public int bgCorner = 8; // 背景圆角大小圆角 （dp）
    public Integer bgColor = COLOR_BG;
    public int bgShadowLayer = 2;

    public Integer iconColor;
    public float iconScale = 0.5f;

    public int shadowAlpha; // 0x00 ~ 0xFF;

    public boolean effectScore = true;

}
