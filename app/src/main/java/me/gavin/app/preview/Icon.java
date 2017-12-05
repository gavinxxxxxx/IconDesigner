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
    static final float BG_C_RATIO = 12f / 192f;

    static final float ICON_SCALE_MIN = 0.2f;
    static final float ICON_SCALE_ADJ = 0.8f;

    private final int COLOR_BG = 0xFF26A69A;

    int bgShape = 0; // 0:圆角矩形 1:圆形 2: 竖直矩形 3：水平矩形
    float bgCorner = BG_C_RATIO; // 背景圆角大小圆角
    Integer bgColor = COLOR_BG;
    int bgShadowLayer = 2;

    Integer iconColor;
    float iconScale = 0.5f;

    int shadowAlpha; // 0x00 ~ 0xFF;

    boolean effectScore = true;
    boolean showKeyLines = false;

}
