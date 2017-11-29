package me.gavin.widget.color.picker;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

/**
 * EditText 字符长度限制（字母一个字符，汉字两个字符）
 *
 * @author gavin.xiong 2017/7/12
 */
public class ColorInputFilter implements InputFilter {

    /**
     * @param source 新输入的字符串
     * @param start  新输入的字符串起始下标，一般为0
     * @param end    新输入的字符串终点下标，一般为source长度
     * @param dest   输入之前文本框内容
     * @param dstart 原内容起始坐标，一般为0
     * @param dend   原内容终点坐标，一般为dest长度
     * @return 输入内容
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 输入字符违规 -
        if (!source.toString().matches("#?[0-9a-fA-F]{0,8}"))
            return TextUtils.isEmpty(dest) ? "#" : "";

        // 拼成字符串
        String result = TextUtils.concat(dest.subSequence(0, dstart),
                source.subSequence(start, end), dest.subSequence(dend, dest.length())).toString();

        // 前端自动加 #
        if (dstart == 0 && !TextUtils.isEmpty(result) && result.matches("[0-9a-fA-F]{0,8}"))
            return "#" + source.toString().toUpperCase();

        // 双 #
        if (result.matches("##[0-9a-fA-F]{0,8}"))
            return source.subSequence(1, end).toString().toUpperCase();

        // 粘贴替换前端时保留 #
        if (!result.matches("#[0-9a-fA-F]{0,8}"))
            return dstart == 0 && dend > 0 && !TextUtils.isEmpty(source) ? "#" : "";

        return source.toString().toUpperCase();
    }
}