package com.btxtech.game.jsre.client.common;

/**
 * Created by beat
 * on 01.06.2014.
 */
public class Vector {
    public static final Vector NULL_VECTOR = new Vector(0, 0);
    public static final DecimalPosition NULL_POSITION = new DecimalPosition(0, 0);
    private double angel;
    private double distance;

    public static Vector fromDecimalPosition(DecimalPosition decimalPosition) {
        double angel = NULL_POSITION.getAngleToNord(decimalPosition);
        double distance = NULL_POSITION.getDistance(decimalPosition);
        return new Vector(angel, distance);
    }

    public Vector(double angel, double distance) {
        this.angel = angel;
        this.distance = distance;
    }

    public Vector add(Vector vector) {
        DecimalPosition result = toDecimalPosition().add(vector.toDecimalPosition());
        return fromDecimalPosition(result);
    }

    private DecimalPosition toDecimalPosition() {
        return NULL_POSITION.getPointFromAngelToNord(angel, distance);
    }

    public double getAngel() {
        return angel;
    }

    public double getDistance() {
        return distance;
    }

    public void incrementBy(Vector vector) {

    }
}
