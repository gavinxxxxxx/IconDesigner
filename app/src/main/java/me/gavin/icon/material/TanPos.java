package me.gavin.icon.material;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/11/10
 */
public class TanPos {

    public float distance;
    public boolean offset;
    public boolean reverse;

    public TanPos(float distance, boolean offset) {
        this.distance = distance;
        this.offset = offset;
    }

    public TanPos(float distance, boolean offset, boolean reverse) {
        this.distance = distance;
        this.offset = offset;
        this.reverse = reverse;
    }

    @Override
    public String toString() {
        return "TanPos{" +
                "distance=" + distance +
                ", offset=" + offset +
                ", reverse=" + reverse +
                '}';
    }
}
