package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 23.05.2011
 * Time: 01:06:28
 */
public class MathHelper {
    // Constants
    public static double ONE_RADIANT = 2.0 * Math.PI;
    public static double QUARTER_RADIANT = Math.PI / 2.0;
    public static double QUARTER_EIGHT = Math.PI / 4.0;
    public static double NORTH = 0;
    public static double NORTH_EAST = 1.75 * Math.PI;
    public static double EAST = 1.5 * Math.PI;
    public static double SOUTH_EAST = 1.25 * Math.PI;
    public static double SOUTH = Math.PI;
    public static double SOUTH_WEST = 0.75 * Math.PI;
    public static double WEST = 0.5 * Math.PI;
    public static double NORTH_WEST = 0.25 * Math.PI;

    /**
     * @param angel input
     * @return an angel between 0 and 2 * PI (inclusive)
     */
    static public double normaliseAngel(double angel) {
        if (angel > ONE_RADIANT) {
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

    public static int getPythagoras(double width, double height) {
        return (int) Math.round(Math.sqrt(width * width + height * height));
    }

    /**
     * Test if a given angel is in the specified section. The section is between the start (inclusive) and the
     * end angel (exclusive) in counter clock wise order
     *
     * @param angel      angel to test if in sector
     * @param startAngel sector begin (inclusive)
     * @param endAngel   sector end (exclusive)
     * @return returns true if the given angel is is the given sector
     */
    public static boolean isInSection(double angel, double startAngel, double endAngel) {
        angel = normaliseAngel(angel);
        startAngel = normaliseAngel(startAngel);
        endAngel = normaliseAngel(endAngel);
        if (startAngel < endAngel) {
            return startAngel <= angel && angel < endAngel;
        } else {
            angel = negateAngel(angel);
            startAngel = negateAngel(startAngel);
            endAngel = negateAngel(endAngel);
            if (startAngel < endAngel) {
                return startAngel <= angel && angel < endAngel;
            } else {
                return startAngel <= angel && angel < endAngel + ONE_RADIANT;
            }
        }
    }

    public static double gradToRad(double grad) {
        return grad / 360.0 * ONE_RADIANT;
    }
}
