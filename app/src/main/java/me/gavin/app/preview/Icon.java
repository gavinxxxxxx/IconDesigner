package me.gavin.app.preview;

/**
 * Icon
 *
 * @author gavin.xiong 2017/12/4
 */
class Icon {

    static final float BG_L_RATIO = 176f / 192f;
    static final float BG_M_RATIO = 152f / 192f;
    static final float BG_S_RATIO = 128f / 192f;

    private final int COLOR_BG = 0xFF26A69A;

    int bgShape = 0; // 0:圆角矩形 1:圆形 2: 竖直矩形 3：水平矩形
    float bgCorner = 16f * 2 / 192f; // 背景圆角大小圆角
    Integer bgColor = COLOR_BG;
    int bgShadowLayer = 2;

    Integer iconColor;
    float iconScale = 0.5f;

    int shadowAlpha; // 0x00 ~ 0xFF;

    boolean effectScore = true;

}
