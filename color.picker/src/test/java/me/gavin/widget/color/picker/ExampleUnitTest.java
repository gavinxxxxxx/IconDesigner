package me.gavin.widget.color.picker;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void color() {
        int color = 0xFFFF0000;
        int color2 = color & ((Math.round(0xFF * 1) << 24) + 0xFFFFFF);
        System.out.println(Integer.toHexString(color2));
    }
}