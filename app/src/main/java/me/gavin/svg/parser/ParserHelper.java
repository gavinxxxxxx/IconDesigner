package me.gavin.svg.parser;

import android.graphics.Color;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/9/8
 */
class ParserHelper {

    static String getString(XmlPullParser parser, String name) {
        return parser.getAttributeValue(XmlPullParser.NO_NAMESPACE, name);
    }

    static float getFloat(XmlPullParser parser, String name) {
        try {
            return Float.parseFloat(getString(parser, name));
        } catch (NullPointerException | NumberFormatException e) {
            return 0;
        }
    }

    static float getFloat(String floatStr) {
        return getFloat(floatStr, 0);
    }

    static float getFloat(String floatStr, float defaultValue) {
        try {
            return Float.parseFloat(floatStr);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    static int getInt(XmlPullParser parser, String name) {
        try {
            return Integer.parseInt(getString(parser, name));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static int getInt(String floatStr) {
        try {
            return Integer.parseInt(floatStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static int getColor(XmlPullParser parser, String name, int defaultValue) {
        return getColor(getString(parser, name), defaultValue);
    }

    static int getColor(String colorStr, int defaultValue) {
        if (TextUtils.isEmpty(colorStr)) {
            return defaultValue;
        } else if ("none".equalsIgnoreCase(colorStr)) {
            return 0;
        } else if (colorStr.startsWith("#")) {
            try {
                return Color.parseColor(colorStr);
            } catch (IllegalArgumentException e) {
                return defaultValue;
            }
        } else if (colorStr.startsWith("rgb(") && colorStr.endsWith(")")) {
            String values[] = colorStr.substring(4, colorStr.length() - 1).split(",");
            try {
                return getColor(getInt(values[0]), getInt(values[1]), getInt(values[2]));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                return defaultValue;
            }
        } else {
            Integer color = Colors.mapColour(colorStr);
            return color == null ? defaultValue : color;
        }
    }

    static int getColor(int r, int g, int b) {
        return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    static String commaLess(String s) {
        return s.replaceAll(",", " ").trim();
    }

    static String[] getStrings(String s) {
        return commaLess(s).split("\\s+");
    }

    static float[] getFloats(String s) {
        String[] ss = getStrings(s);
        float[] fs = new float[ss.length];
        for (int i = 0; i < ss.length; i++) {
            fs[i] = getFloat(ss[i]);
        }
        return fs;
    }

    static String pathFormat(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        // 逗号全部转换成空格
        path = path.trim().replaceAll(",", " ");
        // 法参聚合
        String rex = "[M|m|L|l|H|h|V|v|Q|q|T|t|C|c|S|s|A|a|Z|z]\\s+";
        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(path);
        while (matcher.find()) {
            String group = matcher.group();
            path = path.replaceFirst(group, group.charAt(0) + "");
        }
        // 负参分离
        rex = "[0-9|.]-";
        pattern = Pattern.compile(rex);
        matcher = pattern.matcher(path);
        while (matcher.find()) {
            String group = matcher.group();
            path = path.replaceFirst(group, group.charAt(0) + " -");
        }
        // 点参分离
        rex = "\\.[0-9]*\\.";
        pattern = Pattern.compile(rex);
        matcher = pattern.matcher(path);
        while (matcher.find()) {
            String group = matcher.group();
            path = path.replaceFirst("\\." + group.substring(1, group.length() - 1) + "\\.", group.substring(0, group.length() - 1) + " .");
            matcher.reset(path);
        }
        // 去除多余空格
        path = path.replaceAll("\\s+", " ");
        return path;
    }

    /**
     * 路径方法分离
     */
    static List<String> matches(String path) {
        String rex = "[M|m|L|l|H|h|V|v|Q|q|T|t|C|c|S|s|A|a|Z|z][0-9|\\-.\\s]*";
        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(path);
        List<String> functions = new ArrayList<>();
        while (matcher.find()) {
            functions.add(matcher.group());
        }
        return functions;
    }
}
