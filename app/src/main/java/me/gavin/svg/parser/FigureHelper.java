package me.gavin.svg.parser;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

import me.gavin.svg.model.Drawable;
import me.gavin.svg.model.ICircle;
import me.gavin.svg.model.IEllipse;
import me.gavin.svg.model.IPath;
import me.gavin.svg.model.IRect;

import static me.gavin.svg.parser.ParserHelper.matches;


/**
 * 图形解析
 *
 * @author gavin.xiong 2017/9/8
 */
public class FigureHelper {

    public static void transform(Path path, Drawable drawable) {
        if (drawable instanceof IPath) {
            transform(path, ((IPath) drawable).path);
        } else if (drawable instanceof IRect) {
            IRect rect = (IRect) drawable;
            path.addRoundRect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, rect.rx, rect.ry, Path.Direction.CCW);
        } else if (drawable instanceof ICircle) {
            ICircle circle = (ICircle) drawable;
            path.addCircle(circle.cx, circle.cy, circle.r, Path.Direction.CCW);
        } else if (drawable instanceof IEllipse) {
            IEllipse ellipse = (IEllipse) drawable;
            path.addOval(ellipse.cx - ellipse.rx, ellipse.cy - ellipse.ry, ellipse.cx + ellipse.rx, ellipse.cy + ellipse.ry, Path.Direction.CCW);
        }
    }

    private static void transform(Path path, String d) {
        // points[0]: 路径初始点 points[1]: 图形/方法初始点 points[2]: 控制点
        PointF[] points = new PointF[]{new PointF(), new PointF(), new PointF()};
        List<String> functions = matches(d);
        for (String fun : functions) {
            if (PathParser.m(fun)) {
                PathParser.m(path, fun, points);
            } else if (PathParser.l(fun)) {
                PathParser.l(path, fun, points);
            } else if (PathParser.h(fun)) {
                PathParser.h(path, fun, points);
            } else if (PathParser.v(fun)) {
                PathParser.v(path, fun, points);
            } else if (PathParser.q(fun)) {
                PathParser.q(path, fun, points);
            } else if (PathParser.t(fun)) {
                PathParser.t(path, fun, points);
            } else if (PathParser.c(fun)) {
                PathParser.c(path, fun, points);
            } else if (PathParser.s(fun)) {
                PathParser.s(path, fun, points);
            } else if (PathParser.a(fun)) {
                PathParser.a(path, fun, points);
            } else if (PathParser.z(fun)) {
                PathParser.z(path, points);
            }
        }
    }

}
