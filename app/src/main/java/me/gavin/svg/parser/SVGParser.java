package me.gavin.svg.parser;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.XmlRes;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import me.gavin.svg.model.Drawable;
import me.gavin.svg.model.ICircle;
import me.gavin.svg.model.IEllipse;
import me.gavin.svg.model.IPath;
import me.gavin.svg.model.IRect;
import me.gavin.svg.model.SVG;
import me.gavin.svg.model.ViewBox;

import static me.gavin.svg.parser.ParserHelper.getFloat;
import static me.gavin.svg.parser.ParserHelper.getFloats;
import static me.gavin.svg.parser.ParserHelper.getString;
import static me.gavin.svg.parser.ParserHelper.pathFormat;

/**
 * SVG 解析器
 *
 * @author gavin.xiong 2017/9/4
 */
public class SVGParser {

    public static SVG parse(Resources resources, @XmlRes int resId)
            throws Resources.NotFoundException, XmlPullParserException, IOException {
        try (XmlResourceParser parser = resources.getXml(resId)) {
            return parse(parser);
        }
    }

    public static SVG parse(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "UTF-8");
        return parse(parser);
    }

    private static SVG parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        SVG svg = null;
        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                switch (parser.getName()) {
                    case "svg":
                        svg = new SVG(getFloat(parser, "width"), getFloat(parser, "height"));
                        String viewBox = getString(parser, "viewBox");
                        if (viewBox != null) {
                            float[] fs = getFloats(viewBox);
                            svg.viewBox = new ViewBox(fs);
                        }
                        svg.preserveAspectRatio = getString(parser, "preserveAspectRatio");
                        svg.style = getString(parser, "style");
                        break;
                    case "path":
                        if (svg == null) {
                            break;
                        }
                        IPath iPath = new IPath(pathFormat(getString(parser, "d")));
                        transformDrawable(svg, iPath, parser);
                        break;
                    case "rect":
                        if (svg == null) {
                            break;
                        }
                        IRect iRect = new IRect(getFloat(parser, "x"), getFloat(parser, "y"),
                                getFloat(parser, "width"), getFloat(parser, "height"),
                                getFloat(parser, "rx"), getFloat(parser, "ry"));
                        transformDrawable(svg, iRect, parser);
                        break;
                    case "circle":
                        if (svg == null) {
                            break;
                        }
                        ICircle iCircle = new ICircle(getFloat(parser, "cx"), getFloat(parser, "cy"),
                                getFloat(parser, "r"));
                        transformDrawable(svg, iCircle, parser);
                        break;
                    case "ellipse":
                        if (svg == null) {
                            break;
                        }
                        IEllipse iEllipse = new IEllipse(getFloat(parser, "cx"), getFloat(parser, "cy"),
                                getFloat(parser, "rx"), getFloat(parser, "ry"));
                        transformDrawable(svg, iEllipse, parser);
                        break;
                    case "line":
                        if (svg == null) {
                            break;
                        }
                        String linePath = "M" + getFloat(parser, "x1") + "," + getFloat(parser, "y1")
                                + "L" + getFloat(parser, "x2") + "," + getFloat(parser, "y2");
                        IPath line = new IPath(pathFormat(linePath));
                        transformDrawable(svg, line, parser);
                        break;
                    case "polyline":
                        if (svg == null) {
                            break;
                        }
                        float[] polylinePoints = getFloats(getString(parser, "points"));
                        String polylinePath = "M" + polylinePoints[0] + " " + polylinePoints[1];
                        for (int i = 0; i < polylinePoints.length; i += 2) {
                            polylinePath += "L" + polylinePoints[i] + " " + polylinePoints[i + 1];
                        }
                        IPath polyline = new IPath(pathFormat(polylinePath));
                        transformDrawable(svg, polyline, parser);
                        break;
                    case "polygon":
                        if (svg == null) {
                            break;
                        }
                        float[] polygonPoints = getFloats(getString(parser, "points"));
                        String polygonPath = "M" + polygonPoints[0] + " " + polygonPoints[1];
                        for (int i = 0; i < polygonPoints.length; i += 2) {
                            polygonPath += "L" + polygonPoints[i] + " " + polygonPoints[i + 1];
                        }
                        polygonPath += "z";
                        IPath polygon = new IPath(pathFormat(polygonPath));
                        transformDrawable(svg, polygon, parser);
                        break;
                    case "defs":
                        if (!parser.isEmptyElementTag()) {
                            parser.nextTag();
                        }
                        break;
                    case "title":
                        if (svg != null) {
                            svg.title = parser.getText();
                        }
                        break;
                    case "desc":
                        if (svg != null) {
                            svg.desc = parser.getText();
                        }
                        break;
                    case "linearGradient":
                    case "filter":
                        break;
                    default:
                        break;
                }
            }
            parser.next();
        }
        return svg;
    }

    private static void transformDrawable(SVG svg, Drawable drawable, XmlPullParser parser) {
        drawable.setFillPaint(parseFill(parser));
        drawable.setStrokePaint(parseStroke(parser));
        drawable.setStrokeWidth(getFloat(parser, "stroke-width"));
        svg.drawables.add(drawable);

        Path path = new Path();
        FigureHelper.transform(path, drawable);
        svg.paths.add(path);
    }

    private static Paint parseStroke(XmlPullParser parser) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(ParserHelper.getColor(parser, "stroke", 0x00000000));
        paint.setStrokeWidth(getFloat(parser, "stroke-width"));

        String cap = getString(parser, "stroke-linecap");
        if ("round".equals(cap)) {
            paint.setStrokeCap(Paint.Cap.ROUND);
        } else if ("square".equals(cap)) {
            paint.setStrokeCap(Paint.Cap.SQUARE);
        } else if ("butt".equals(cap)) {
            paint.setStrokeCap(Paint.Cap.BUTT);
        }

        String join = getString(parser, "stroke-linejoin");
        if ("miter".equals(join)) {
            paint.setStrokeJoin(Paint.Join.MITER);
        } else if ("round".equals(join)) {
            paint.setStrokeJoin(Paint.Join.ROUND);
        } else if ("bevel".equals(join)) {
            paint.setStrokeJoin(Paint.Join.BEVEL);
        }

        String dashStyle = getString(parser, "stroke-dasharray");
        if (dashStyle != null) {
            if ("none".equalsIgnoreCase(dashStyle)) {
                paint.setPathEffect(null);
            } else {
                String offset = getString(parser, "stroke-dashoffset");

                StringTokenizer st = new StringTokenizer(dashStyle, " ,");
                int count = st.countTokens();
                float[] intervals = new float[(count & 1) == 1 ? count * 2 : count];
                float max = 0;
                float current = 1f;
                int i = 0;
                while (st.hasMoreTokens()) {
                    intervals[i++] = current = getFloat(st.nextToken(), current);
                    max += current;
                }

                // in svg speak, we double the intervals on an odd count
                for (int start = 0; i < intervals.length; i++, start++) {
                    max += intervals[i] = intervals[start];
                }

                float off = 0f;
                if (offset != null) {
                    try {
                        off = Float.parseFloat(offset) % max;
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }

                paint.setPathEffect(new DashPathEffect(intervals, off));
            }
        }

        // TODO: 2017/9/11 path衔接问题解决方案 - 待改善
//        if (paint.getPathEffect() == null) {
//            paint.setPathEffect(new CornerPathEffect(0.001f));
//        } else {
//            paint.setPathEffect(new ComposePathEffect(paint.getPathEffect(), new CornerPathEffect(0.001f)));
//        }

        return paint;
    }

    private static Paint parseFill(XmlPullParser parser) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(ParserHelper.getColor(parser, "fill", 0xff000000));

        String fillOpacity = getString(parser, "fill-opacity");
        if (fillOpacity != null) {
            float alpha = getFloat(fillOpacity);
            paint.setAlpha((int) (255f * alpha));
        }

        return paint;
    }

}
