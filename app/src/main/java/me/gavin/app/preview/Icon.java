package me.gavin.app.preview;

import android.graphics.Bitmap;

import me.gavin.app.App;
import me.gavin.svg.model.Drawable;
import me.gavin.svg.model.SVG;
import me.gavin.util.Base64Helper;
import me.gavin.util.L;
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

    SVG svg;
    Drawable drawable;
    Bitmap bitmap;
    String text = "熊文强~";
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

        try {
//            this.svg = (SVG) Base64Helper.fromBytes(Base64Helper.fromBase64(SPUtil.getString("svg")));
            Object obj = Base64Helper.fromBytes(Base64Helper.fromBase64(SPUtil.getString("svg")));
            L.e(obj);
            if (obj != null) {
                this.text = (String)obj ;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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

        try {
            L.e(text);
            String str = Base64Helper.toBase64(Base64Helper.toBytes(text));
            L.e(str);
            SPUtil.putString("svg", str);
//            Base64Helper.saveObject(App.get(), "svg2", text);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        SPUtil.putInt("iconColor", this.iconColor);
        SPUtil.putFloat("iconScale", this.iconScale);

        SPUtil.putInt("shadowAlpha", this.shadowAlpha);

        SPUtil.putBoolean("effectScore", this.effectScore);
        SPUtil.putBoolean("showKeyLines", this.showKeyLines);
    }
}
