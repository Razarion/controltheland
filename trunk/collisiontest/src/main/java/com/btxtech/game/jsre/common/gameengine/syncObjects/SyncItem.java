package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Vector;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: beat
 * Date: 20.03.13
 * Time: 21:36
 */
public class SyncItem {
    public static final double MAX_VELOCITY = 1;
    public static final double MAX_FORCE = 5.4;
    public static final double SLOWING_DOWN_RADIUS = 20;
    public static final double MAX_AVOID_AHEAD = 50;
    public static final double AVOID_FORCE = 20;
    public static final double MASS = 10;
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    // General
    private int id;
    // Item type
    private int radius;
    // SyncItemArea
    private DecimalPosition decimalPosition;
    private DecimalPosition target;
    private double angel;
    private double speed;

    public SyncItem(int radius, Index position) {
        this.radius = radius;
        decimalPosition = new DecimalPosition(position);
        id = ID_GENERATOR.incrementAndGet();
    }

    public int getRadius() {
        return radius;
    }

    public Index getPosition() {
        return getDecimalPosition().getPosition();
    }

    public DecimalPosition getDecimalPosition() {
        return decimalPosition;
    }

    public int getDiameter() {
        return radius * 2;
    }

    public void moveTo(Index destination) {
        target = new DecimalPosition(destination);
    }

    public void moveTo(List<Index> wayPoint) {
    }

    public DecimalPosition getTargetPosition() {
        return target;
    }

    public void stop() {
        target = null;
    }

    public double getAngel() {
        return angel;
    }

    public void setAngel(double angel) {
        this.angel = angel;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    //public void setDecimalPosition(DecimalPosition decimalPosition) {
    //    this.decimalPosition = decimalPosition;
    //}

    public boolean isMoving() {
        return target != null;
    }

    public double calculateArea() {
        return Math.PI * Math.pow(radius, 2);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SyncItem{id=" + id + " Position: " + decimalPosition;
    }

    public DecimalPosition getVelocity() {
        return Vector.NULL_POSITION.getPointFromAngelToNord(angel, speed);
    }

    public void executeMove() {
        decimalPosition = decimalPosition.getPointFromAngelToNord(angel, speed);
    }
}
