package trueparallel.timeline;


public class Utils {

    public static double[] calculate(double maxPoint, double minPoint) {

        double range = niceNum(maxPoint - minPoint, false);
        double tickSpacing = niceNum(range / (10 - 1), true);
        double niceMin =
                Math.floor(minPoint / tickSpacing) * tickSpacing;

        System.out.println("Nice Min is :" + niceMin + "tick:" + tickSpacing);
        double niceMax =
                Math.ceil(maxPoint / tickSpacing) * tickSpacing;

        System.out.println("Nice Max is :" + niceMax);
        return new double[]{niceMax, niceMin, tickSpacing};
    }

    public static double niceNum(double range, boolean round) {
        double exponent; /** exponent of range */
        double fraction; /** fractional part of range */
        double niceFraction; /** nice, rounded fraction */

        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);

        if (round) {
            if (fraction < 1.5)
                niceFraction = 1;
            else if (fraction < 3)
                niceFraction = 2;
            else if (fraction < 7)
                niceFraction = 5;
            else
                niceFraction = 10;
        } else {
            if (fraction <= 1)
                niceFraction = 1;
            else if (fraction <= 2)
                niceFraction = 2;
            else if (fraction <= 5)
                niceFraction = 5;
            else
                niceFraction = 10;
        }

        return niceFraction * Math.pow(10, exponent);
    }

}