package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 23.05.2011
 * Time: 01:06:28
 */
public class MathHelper {
    // Constants
    public static double ONE_RADIANT = 2.0 * Math.PI;
    public static double HALF_RADIANT = Math.PI;
    public static double QUARTER_RADIANT = Math.PI / 2.0;
    public static double THREE_QUARTER_RADIANT = 3.0 * Math.PI / 2.0;
    public static double EIGHTH_RADIANT = Math.PI / 4.0;
    public static double NORTH = 0;
    public static double NORTH_EAST = 1.75 * Math.PI;
    public static double EAST = 1.5 * Math.PI;
    public static double SOUTH_EAST = 1.25 * Math.PI;
    public static double SOUTH = Math.PI;
    public static double SOUTH_WEST = 0.75 * Math.PI;
    public static double WEST = 0.5 * Math.PI;
    public static double NORTH_WEST = 0.25 * Math.PI;
    public static double SQRT_OF_2 = Math.sqrt(2.0);
    public static double PRECISION = 0.00001;

    /**
     * @param angel input
     * @return an angel between 0 and 2 * PI (inclusive)
     */
    static public double normaliseAngel(double angel) {
        if (angel >= ONE_RADIANT) {
            return angel - ONE_RADIANT;
        } else if (angel < 0) {
            return angel + ONE_RADIANT;
        } else {
            return angel;
        }
    }

    /**
     * @param angel input
     * @return if the angel is bigger then PI make it negative
     */
    static public double negateAngel(double angel) {
        angel = normaliseAngel(angel);
        if (angel > Math.PI) {
            angel = angel - ONE_RADIANT;
        }
        return angel;
    }

    public static double closerToAngel(double origin, double angel1, double angel2) {
        origin = normaliseAngel(origin);
        double originNegated = negateAngel(origin);
        double tmpAngel1 = normaliseAngel(angel1);
        double tmpAngel2 = normaliseAngel(angel2);
        double tmpAngelNegated1 = negateAngel(angel1);
        double tmpAngelNegated2 = negateAngel(angel2);

        double delta1 = Math.min(Math.abs(originNegated - tmpAngelNegated1), Math.abs(origin - tmpAngel1));
        double delta2 = Math.min(Math.abs(originNegated - tmpAngelNegated2), Math.abs(origin - tmpAngel2));
        if (delta1 < delta2) {
            return angel1;
        } else {
            return angel2;
        }
    }

    public static double getPythagoras(double width, double height) {
        return Math.sqrt(width * width + height * height);
    }

    public static int getSqrtOfTwo(double length) {
        return (int) Math.round(SQRT_OF_2 * length);
    }

    public static boolean isInSection(double angel, double startAngel, double deltaAngel) {
        angel = normaliseAngel(angel);
        startAngel = normaliseAngel(startAngel);

        if (deltaAngel >= 0) {
            angel = MathHelper.normaliseAngel(angel - startAngel);
            return angel <= deltaAngel || Math.abs(angel - deltaAngel) <= PRECISION;
        } else {
            startAngel = startAngel + deltaAngel;
            return isInSection(angel, startAngel, -deltaAngel);
        }
    }

    /**
     * Returns the angel from start to end
     *
     * @param startAngel   start angel
     * @param endAngel     end angel
     * @param counterClock if true the counter clockwise
     * @return resulting angel
     */
    public static double getAngel(double startAngel, double endAngel, boolean counterClock) {
        startAngel = normaliseAngel(startAngel);
        endAngel = normaliseAngel(endAngel);
        if (counterClock) {
            return normaliseAngel(endAngel - startAngel);
        } else {
            return normaliseAngel(startAngel - endAngel);
        }
    }

    public static double gradToRad(double grad) {
        return grad / 360.0 * ONE_RADIANT;
    }

    public static double radToGrad(double rad) {
        return rad / ONE_RADIANT * 360.0;
    }

    public static double signum(double value) {
        if (value < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    public static boolean compareWithPrecision(double value1, double value2) {
        if (Double.compare(value1, value2) == 0) {
            return true;
        }
        double delta = Math.abs(value1 - value2);
        return delta <= PRECISION;
    }
}
