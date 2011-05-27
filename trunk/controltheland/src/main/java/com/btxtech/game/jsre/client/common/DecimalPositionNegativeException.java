package com.btxtech.game.jsre.client.common;

/**
 * User: beat
 * Date: 27.05.2011
 * Time: 15:08:06
 */
public class DecimalPositionNegativeException extends RuntimeException {
    public DecimalPositionNegativeException(double x, double y) {
        super("Decimal position is not allowed to be negative: x=" + x + " y=" + y);
    }
}
