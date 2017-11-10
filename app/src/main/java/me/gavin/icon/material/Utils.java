package me.gavin.icon.material;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import java.util.ArrayList;
import java.util.List;

import me.gavin.icon.material.util.L;

/**
 * 工具
 *
 * @author gavin.xiong 2017/11/10
 */
class Utils {

    private static PathMeasure measure = new PathMeasure();

    /**
     * 当前是否该偏移
     * todo 起点没有前一斜率 终点没有后一斜率
     *
     * @param t  阴影斜率
     * @param lt 当前点前斜率
     * @param nt 当前点后斜率
     */
    private static boolean shouldOffset(float t, float lt, float nt) {
        return (lt < t - 180 || lt > t) && (nt < t && nt > t - 180);
    }

    /**
     * 当前是否该复位
     *
     * @param t  阴影斜率
     * @param lt 当前点前斜率
     * @param nt 当前点后斜率
     */
    private static boolean shouldBack(float t, float lt, float nt) {
        return (lt < t && lt > t - 180) && (nt < t - 180 || nt > t);
    }

    /**
     * 这里是萌萌哒注释君
     */
    static List<Segment> getTanPos(Path path, float t) {
        measure.setPath(path, false);
        List<Segment> listOfPath = new ArrayList<>();
        float[] pos = new float[2], tan = new float[2];

        List<Path> segmentList = new ArrayList<>();
        List<PointF> startPoint = new ArrayList<>();

        while (measure.nextContour()) {
            Segment segment = new Segment();
            float length = measure.getLength();

            Path segmentPath = new Path();
            measure.getSegment(0, length, segmentPath, true);
            segmentList.add(segmentPath);

            measure.getPosTan(0, pos, tan);
            startPoint.add(new PointF(pos[0], pos[1]));

            float lt = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);
            // 初始偏移状态
            segment.startWithOffset = lt < t && lt > t - 180;
            for (float i = 0.01f; i < 1; i += 0.01f) {
                float dis = length * i;

                measure.getPosTan(dis, pos, tan);
                float nt = (float) (Math.atan2(tan[1], tan[0]) * 180 / Math.PI);

                if (shouldOffset(t, lt, nt)) {
                    segment.tanPosList.add(new TanPos(dis, true));
                }
                if (shouldBack(t, lt, nt)) {
                    segment.tanPosList.add(new TanPos(dis, false));
                }

                lt = nt;
            }

            listOfPath.add(segment);
        }

        // 奇偶性判断（不准 实际规则为非零环绕？）
        for (int i = 0; i < startPoint.size(); i++) {
            int count = 0;
            for (int j = 0; j < segmentList.size(); j++) {
                PointF point = startPoint.get(i);
                if (i != j && contains(segmentList.get(j), (int) point.x, (int) point.y)) {
                    count++;
                }
            }
            boolean reverse = count % 2 == 1;
            for (TanPos tanPos : listOfPath.get(i).tanPosList) {
                tanPos.reverse = reverse;
            }
        }

        return listOfPath;
    }

    private static boolean contains(Path path, int x, int y) {
        RectF r = new RectF();
        path.computeBounds(r, true);
        Region region = new Region();
        region.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        // region.getBoundaryPath(); // TODO: 2017/11/10 这货好像是另一种方法
        return region.contains(x, y);
    }


}
