package me.gavin.svg.parser;

/**
 * 工具类
 *
 * @author gavin.xiong 2017/9/14
 */
public class Util {

    /**
     * 保留 n 位有效数字
     *
     * @link {http://www.jianshu.com/p/09ddca8b17ef}
     * @link {https://stackoverflow.com/questions/202302/rounding-to-an-arbitrary-number-of-significant-digits}
     */
    public static double roundToSignificantFigures(double num, int n) {
        if (num == 0) {
            return 0;
        }
        double d = Math.ceil(Math.log10(num < 0 ? -num : num));
        int power = n - (int) d;
        double magnitude = Math.pow(10, power);
        long shifted = Math.round(num * magnitude);
        return shifted / magnitude;
    }
}
