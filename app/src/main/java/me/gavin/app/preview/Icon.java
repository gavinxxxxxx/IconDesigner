package me.gavin.app.preview;

import me.gavin.util.SPUtil;

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

    int bgShape; // 0:圆角矩形 1:圆形 2: 竖直矩形 3：水平矩形
    float bgCorner; // 背景圆角大小圆角
    Integer bgColor;
    int bgShadowLayer;

    Integer iconColor;
    float iconScale;

    int shadowAlpha; // 0x00 ~ 0xFF;

    boolean effectScore;
    boolean showKeyLines;

    Icon() {
        this.bgShape = SPUtil.getInt("bgShape", 0);
        this.bgCorner = SPUtil.getFloat("bgCorner", BG_C_RATIO);
        this.bgColor = SPUtil.getInt("bgColor", 0xFF26A69A);
        this.bgShadowLayer = SPUtil.getInt("bgShadowLayer", 2);

        this.iconColor = SPUtil.getInt("iconColor");
        this.iconScale = SPUtil.getFloat("iconScale", 0.5f);

        this.shadowAlpha = SPUtil.getInt("shadowAlpha", 100);

        this.effectScore = SPUtil.getBoolean("effectScore", true);
        this.showKeyLines = SPUtil.getBoolean("showKeyLines", false);
    }

    void put() {
        SPUtil.putInt("bgShape", this.bgShape);
        SPUtil.putFloat("bgCorner", this.bgCorner);
        SPUtil.putInt("bgColor", this.bgColor);
        SPUtil.putInt("bgShadowLayer", this.bgShadowLayer);

        SPUtil.putInt("iconColor", this.iconColor);
        SPUtil.putFloat("iconScale", this.iconScale);

        SPUtil.putInt("shadowAlpha", this.shadowAlpha);

        SPUtil.putBoolean("effectScore", this.effectScore);
        SPUtil.putBoolean("showKeyLines", this.showKeyLines);
    }
}
