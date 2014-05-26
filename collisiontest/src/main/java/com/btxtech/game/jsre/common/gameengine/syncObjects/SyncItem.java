package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: beat
 * Date: 20.03.13
 * Time: 21:36
 */
public class SyncItem {
    public static final double SPEED = 40;
    private static final double PRECISION_ANGEL = MathHelper.gradToRad(1);
    private static final double TURN_SPEED = MathHelper.gradToRad(360 / 5); // Grad per second
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    public enum MoveState {
        MOVING,
        STOPPED,
        TURNING,
        //BLOCKED
    }

    // General
    private String debugName;
    private int id;
    private MoveState state = MoveState.STOPPED;
    // Item type
    private int radius;
    // SyncItemArea
    private DecimalPosition decimalPosition;
    private double angel = 0;
    private double targetAngel;
    private double speed;
    // Moving
    private Index targetPosition;

    public SyncItem(int radius, Index position, String debugName) {
        this.debugName = debugName;
        this.radius = radius;
        decimalPosition = new DecimalPosition(position);
        id = ID_GENERATOR.incrementAndGet();
    }

    public int getRadius() {
        return radius;
    }

    private boolean targetAngelReached() {
        return angelReached(targetAngel);
    }

    public boolean angelReached(double targetAngel) {
        return MathHelper.getAngel(angel, targetAngel) <= PRECISION_ANGEL;
    }

    /**
     * @param factor 1 if last call was exactly 1 second before
     */
    public DecimalPosition calculateMoveToTarget(double factor) {
        if (targetAngelReached()) {
            return decimalPosition.getPointFromAngelToNord(angel, speed * factor);
        } else {
            return decimalPosition;
        }
    }

    public void executeMoveToTarget(double factor) {
        if (targetAngelReached()) {
            decimalPosition = decimalPosition.getPointFromAngelToNord(angel, speed * factor);
            state = MoveState.MOVING;
            if (decimalPosition.getPosition().equals(targetPosition)) {
                stop();
            }
        } else {
            double factorAngel = TURN_SPEED * factor;
            double actualDeltaAngel = MathHelper.getAngel(angel, targetAngel);
            if (factorAngel >= actualDeltaAngel) {
                // reached
                angel = targetAngel;
            } else {
                if (MathHelper.isCounterClock(angel, targetAngel)) {
                    angel = MathHelper.normaliseAngel(angel + factorAngel);
                } else {
                    angel = MathHelper.normaliseAngel(angel - factorAngel);
                }
            }
            state = MoveState.TURNING;
        }
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

    public MoveState getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public void setTargetPosition(Index targetPosition) {
        this.targetPosition = targetPosition;
        targetAngel = decimalPosition.getAngleToNord(new DecimalPosition(targetPosition));
        speed = SPEED;
        state = MoveState.MOVING;
    }

    public Index getTargetPosition() {
        return targetPosition;
    }

    public double getAngel() {
        return angel;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getTargetAngel() {
        return targetAngel;
    }

    public void setTargetAngel(double targetAngel) {
        this.targetAngel = targetAngel;
    }

    public void stop() {
        targetPosition = null;
        speed = 0;
        state = MoveState.STOPPED;
    }

    public double calculateArea() {
        return Math.PI * Math.pow(radius, 2);
    }

    @Override
    public String toString() {
        return "SyncItem{id=" + id + " Position: " + decimalPosition + " debugName: " + debugName + " targetPosition: " + targetPosition + '}';
    }
}
