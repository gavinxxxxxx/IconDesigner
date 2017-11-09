package me.gavin.svg.parser;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * 路径转换器
 *
 * @author gavin.xiong 2017/8/26
 */
class PathParser {

    /**
     * 移动画笔
     */
    static boolean m(String fun) {
        return fun.startsWith("M") || fun.startsWith("m");
    }

    /**
     * 移动画笔
     */
    static void m(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("path parse error for M(m): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 2) {
            if (f.startsWith("M")) {
                points[1].x = Float.parseFloat(args[i]);
                points[1].y = Float.parseFloat(args[i + 1]);
            } else if (f.startsWith("m")) {
                points[1].x += Float.parseFloat(args[i]);
                points[1].y += Float.parseFloat(args[i + 1]);
            }
            points[0].x = points[1].x;
            points[0].y = points[1].y;
            path.moveTo(points[1].x, points[1].y);
        }
        points[2].x = points[1].x;
        points[2].y = points[1].y;
    }

    /**
     * 线段
     */
    static boolean l(String fun) {
        return fun.startsWith("L") || fun.startsWith("l");
    }

    /**
     * 线段
     */
    static void l(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("path parse error for L(l): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 2) {
            if (f.startsWith("L")) {
                points[1].x = Float.parseFloat(args[i]);
                points[1].y = Float.parseFloat(args[i + 1]);
            } else if (f.startsWith("l")) {
                points[1].x += Float.parseFloat(args[i]);
                points[1].y += Float.parseFloat(args[i + 1]);
            }
            path.lineTo(points[1].x, points[1].y);
        }
        points[2].x = points[1].x;
        points[2].y = points[1].y;
    }

    /**
     * 水平线段
     */
    static boolean h(String fun) {
        return fun.startsWith("H") || fun.startsWith("h");
    }

    /**
     * 水平线段
     */
    static void h(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        for (String s : args) {
            if (f.startsWith("H")) {
                points[1].x = Float.parseFloat(s);
            } else if (f.startsWith("h")) {
                points[1].x += Float.parseFloat(s);
            }
            path.lineTo(points[1].x, points[1].y);
        }
        points[2].x = points[1].x;
        points[2].y = points[1].y;
    }

    /**
     * 垂直线段
     */
    static boolean v(String fun) {
        return fun.startsWith("V") || fun.startsWith("v");
    }

    /**
     * 垂直线段
     */
    static void v(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        for (String s : args) {
            if (f.startsWith("V")) {
                points[1].y = Float.parseFloat(s);
            } else if (f.startsWith("v")) {
                points[1].y += Float.parseFloat(s);
            }
            path.lineTo(points[1].x, points[1].y);
        }
        points[2].x = points[1].x;
        points[2].y = points[1].y;
    }

    /**
     * 二次贝塞尔曲线
     */
    static boolean q(String fun) {
        return fun.startsWith("Q") || fun.startsWith("q");
    }

    /**
     * 二次贝塞尔曲线
     */
    static void q(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 4 != 0) {
            throw new IllegalArgumentException("path parse error for Q(q): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 4) {
            if (f.startsWith("Q")) {
                points[2].x = Float.parseFloat(args[i]);
                points[2].y = Float.parseFloat(args[i + 1]);
                points[1].x = Float.parseFloat(args[i + 2]);
                points[1].y = Float.parseFloat(args[i + 3]);
            } else if (f.startsWith("q")) {
                points[2].x += Float.parseFloat(args[i]);
                points[2].y += Float.parseFloat(args[i + 1]);
                points[1].x += Float.parseFloat(args[i + 2]);
                points[1].y += Float.parseFloat(args[i + 3]);
            }
            path.quadTo(points[2].x, points[2].y, points[1].x, points[1].y);
        }
    }

    /**
     * 二次贝塞尔曲线 - 缩写
     */
    static boolean t(String fun) {
        return fun.startsWith("T") || fun.startsWith("t");
    }

    /**
     * 二次贝塞尔曲线 - 缩写
     */
    static void t(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("path parse error for T(t): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 2) {
            points[2].x = points[1].x + points[1].x - points[2].x;
            points[2].y = points[1].y + points[1].y - points[2].y;
            if (f.startsWith("T")) {
                points[1].x = Float.parseFloat(args[i]);
                points[1].y = Float.parseFloat(args[i + 1]);
            } else if (f.startsWith("t")) {
                points[1].x += Float.parseFloat(args[i]);
                points[1].y += Float.parseFloat(args[i + 1]);
            }
            path.quadTo(points[2].x, points[2].y, points[1].x, points[1].y);
        }
    }

    /**
     * 三次贝塞尔曲线
     */
    static boolean c(String fun) {
        return fun.startsWith("C") || fun.startsWith("c");
    }

    /**
     * 三次贝塞尔曲线
     */
    static void c(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 6 != 0) {
            throw new IllegalArgumentException("path parse error for C(c): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 6) {
            float x1 = 0, y1 = 0;
            if (f.startsWith("C")) {
                x1 = Float.parseFloat(args[i]);
                y1 = Float.parseFloat(args[i + 1]);
                points[2].x = Float.parseFloat(args[i + 2]);
                points[2].y = Float.parseFloat(args[i + 3]);
                points[1].x = Float.parseFloat(args[i + 4]);
                points[1].y = Float.parseFloat(args[i + 5]);
            } else if (f.startsWith("c")) {
                x1 = points[1].x + Float.parseFloat(args[i]);
                y1 = points[1].y + Float.parseFloat(args[i + 1]);
                points[2].x = points[1].x + Float.parseFloat(args[i + 2]);
                points[2].y = points[1].y + Float.parseFloat(args[i + 3]);
                points[1].x += Float.parseFloat(args[i + 4]);
                points[1].y += Float.parseFloat(args[i + 5]);
            }
            path.cubicTo(x1, y1, points[2].x, points[2].y, points[1].x, points[1].y);
        }
    }

    /**
     * 三次贝塞尔曲线 - 缩写
     */
    static boolean s(String fun) {
        return fun.startsWith("S") || fun.startsWith("s");
    }

    /**
     * 三次贝塞尔曲线 - 缩写
     */
    static void s(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 4 != 0) {
            throw new IllegalArgumentException("path parse error for C(c): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 4) {
            float x1 = points[1].x + points[1].x - points[2].x;
            float y1 = points[1].y + points[1].y - points[2].y;
            if (f.startsWith("S")) {
                points[2].x = Float.parseFloat(args[i]);
                points[2].y = Float.parseFloat(args[i + 1]);
                points[1].x = Float.parseFloat(args[i + 2]);
                points[1].y = Float.parseFloat(args[i + 3]);
            } else if (f.startsWith("s")) {
                points[2].x = points[1].x + Float.parseFloat(args[i]);
                points[2].y = points[1].y + Float.parseFloat(args[i + 1]);
                points[1].x += Float.parseFloat(args[i + 2]);
                points[1].y += Float.parseFloat(args[i + 3]);
            }
            path.cubicTo(x1, y1, points[2].x, points[2].y, points[1].x, points[1].y);
        }
    }

    /**
     * 弧形
     */
    static boolean a(String fun) {
        return fun.startsWith("A") || fun.startsWith("a");
    }

    /**
     * 弧形
     */
    static void a(Path path, String f, PointF[] points) {
        String[] args = f.substring(1).split(" ");
        if (args.length % 7 != 0) {
            throw new IllegalArgumentException("path parse error for A(a): parameter error @" + f);
        }
        for (int i = 0; i < args.length; i += 7) {
            if (f.startsWith("A")) {
                ArcHelper.drawArc(path,
                        points[1].x,
                        points[1].y,
                        Float.parseFloat(args[5]),
                        Float.parseFloat(args[6]),
                        Float.parseFloat(args[0]),
                        Float.parseFloat(args[1]),
                        Float.parseFloat(args[2]),
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]));
                points[1].x = Float.parseFloat(args[5]);
                points[1].y = Float.parseFloat(args[6]);
            } else if (f.startsWith("a")) {
                ArcHelper.drawArc(path,
                        points[1].x,
                        points[1].y,
                        points[1].x += Float.parseFloat(args[5]),
                        points[1].x += Float.parseFloat(args[6]),
                        Float.parseFloat(args[0]),
                        Float.parseFloat(args[1]),
                        Float.parseFloat(args[2]),
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]));
                points[1].x += Float.parseFloat(args[5]);
                points[1].y += Float.parseFloat(args[6]);
            }
        }
        points[2].x = points[1].x;
        points[2].y = points[1].y;
    }

    /**
     * 闭合
     */
    static boolean z(String fun) {
        return fun.startsWith("Z") || fun.startsWith("z");
    }

    /**
     * 闭合
     */
    static void z(Path path, PointF[] points) {
        points[1].x = points[0].x;
        points[1].y = points[0].y;
        path.close();
    }

}
