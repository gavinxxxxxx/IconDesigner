package me.gavin.svg.parser;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * SVG a -> Path arcTo
 *
 * @author gavin.xiong 2017/8/29
 */
class ArcHelper {

    private static final RectF arcRectF = new RectF();
    private static final Matrix arcMatrix = new Matrix();
    private static final Matrix arcMatrix2 = new Matrix();

    static void drawArc(Path path, float lastX, float lastY, float x, float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
        // http://www.w3.org/TR/SVG/implnote.html#ArcImplementationNotes

        if (x == lastX && y == lastY) {
            return; // nothing to draw
        }

        if (rx == 0 || ry == 0) {
            path.lineTo(x, y);
            return;
        }

        rx = Math.abs(rx);
        ry = Math.abs(ry);

        final float thrad = theta * (float) Math.PI / 180;
        final float st = (float) Math.sin(thrad);
        final float ct = (float) Math.cos(thrad);

        final float xc = (lastX - x) / 2;
        final float yc = (lastY - y) / 2;
        final float x1t = ct * xc + st * yc;
        final float y1t = -st * xc + ct * yc;

        final float x1ts = x1t * x1t;
        final float y1ts = y1t * y1t;
        float rxs = rx * rx;
        float rys = ry * ry;

        float lambda = (x1ts / rxs + y1ts / rys) * 1.001f; // add 0.1% to be sure that no out of range occurs due to
        // limited precision
        if (lambda > 1) {
            float lambdasr = (float) Math.sqrt(lambda);
            rx *= lambdasr;
            ry *= lambdasr;
            rxs = rx * rx;
            rys = ry * ry;
        }

        final float R = (float) Math.sqrt((rxs * rys - rxs * y1ts - rys * x1ts) / (rxs * y1ts + rys * x1ts)) * ((largeArc == sweepArc) ? -1 : 1);
        final float cxt = R * rx * y1t / ry;
        final float cyt = -R * ry * x1t / rx;
        final float cx = ct * cxt - st * cyt + (lastX + x) / 2;
        final float cy = st * cxt + ct * cyt + (lastY + y) / 2;

        final float th1 = angle(1, 0, (x1t - cxt) / rx, (y1t - cyt) / ry);
        float dth = angle((x1t - cxt) / rx, (y1t - cyt) / ry, (-x1t - cxt) / rx, (-y1t - cyt) / ry);

        if (sweepArc == 0 && dth > 0) {
            dth -= 360;
        } else if (sweepArc != 0 && dth < 0) {
            dth += 360;
        }

        // draw
        if ((theta % 360) == 0) {
            // no rotate and translate need
            arcRectF.set(cx - rx, cy - ry, cx + rx, cy + ry);
            path.arcTo(arcRectF, th1, dth);
        } else {
            // this is the hard and slow part :-)
            arcRectF.set(-rx, -ry, rx, ry);

            arcMatrix.reset();
            arcMatrix.postRotate(theta);
            arcMatrix.postTranslate(cx, cy);
            arcMatrix.invert(arcMatrix2);

            path.transform(arcMatrix2);
            path.arcTo(arcRectF, th1, dth);
            path.transform(arcMatrix);
        }
    }

    private static float angle(float x1, float y1, float x2, float y2) {
        return (float) Math.toDegrees(Math.atan2(x1, y1) - Math.atan2(x2, y2)) % 360;
    }
}
