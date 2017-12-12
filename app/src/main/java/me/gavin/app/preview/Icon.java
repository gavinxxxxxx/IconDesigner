package me.gavin.app.preview;

import me.gavin.util.SPUtil;

/**
 * Icon
 *
 * @author gavin.xiong 2017/12/4
 */
class Icon {

    static final float BG_L_RATIO = 44f / 48f;
    static final float BG_M_RATIO = 38f / 48f;
    static final float BG_S_RATIO = 32f / 48f;
    static final float BG_C_RATIO = 03f / 48f; // 背景圆角
    static final float BG_E_RATIO = 20f / 48f; // 背景狗耳 - 22f / 48f
    static final float BG_SL_RATIO = 1f / 48f; // 背景阴影

    static final float ICON_SCALE_MIN = 0.2f;
    static final float ICON_SCALE_ADJ = 0.8f;

    int bgShape; // 0:圆角矩形 1:圆形 2: 竖直矩形 3：水平矩形
    float bgCorner; // 背景圆角大小圆角
    Integer bgColor;

    Integer iconColor;
    float iconScale;

    float shadowLength; // [0, 1]
    int shadowAlpha; // [0x00, 0xFF]

    boolean effectScore;
    boolean effectEar;
    boolean showKeyLines;

    Icon() {
        this.bgShape = SPUtil.getInt("bgShape", 0);
        this.bgCorner = SPUtil.getFloat("bgCorner", BG_C_RATIO);
        this.bgColor = SPUtil.getInt("bgColor", 0xFFFFFFFF);

        this.iconColor = SPUtil.getInt("iconColor");
        this.iconScale = SPUtil.getFloat("iconScale", 0.5f);

        this.shadowLength = SPUtil.getFloat("shadowLength", 1f);
        this.shadowAlpha = SPUtil.getInt("shadowAlpha", 30);

        this.effectScore = SPUtil.getBoolean("effectScore", false);
        this.effectEar = SPUtil.getBoolean("effectEar", false);
        this.showKeyLines = SPUtil.getBoolean("showKeyLines", false);
    }

    void put() {
        SPUtil.putInt("bgShape", this.bgShape);
        SPUtil.putFloat("bgCorner", this.bgCorner);
        SPUtil.putInt("bgColor", this.bgColor);

        SPUtil.putInt("iconColor", this.iconColor);
        SPUtil.putFloat("iconScale", this.iconScale);

        SPUtil.putFloat("shadowLength", this.shadowLength);
        SPUtil.putInt("shadowAlpha", this.shadowAlpha);

        SPUtil.putBoolean("effectScore", this.effectScore);
        SPUtil.putBoolean("effectEar", this.effectEar);
        SPUtil.putBoolean("showKeyLines", this.showKeyLines);
    }
}
