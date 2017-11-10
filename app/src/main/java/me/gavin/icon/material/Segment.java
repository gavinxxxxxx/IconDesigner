package me.gavin.icon.material;

import java.util.ArrayList;
import java.util.List;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/10
 */
public class Segment {

    boolean startWithOffset;
    List<TanPos> tanPosList = new ArrayList<>();

    @Override
    public String toString() {
        return "Segment{" +
                "startWithOffset=" + startWithOffset +
                ", tanPosList=" + tanPosList +
                '}';
    }
}
