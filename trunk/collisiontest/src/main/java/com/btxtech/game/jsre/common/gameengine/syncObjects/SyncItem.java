package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: beat
 * Date: 20.03.13
 * Time: 21:36
 */
public class SyncItem {
    public static final double SPEED = 40;
    private static final double PRECISION_ANGEL = MathHelper.gradToRad(1);
    public static final double TURN_SPEED = MathHelper.gradToRad(180); // Grad per second
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
    private double speed;
    private double aimAngel;
    // Moving
    private Path path;

    public SyncItem(int radius, Index position, String debugName) {
        this.debugName = debugName;
        this.radius = radius;
        decimalPosition = new DecimalPosition(position);
        id = ID_GENERATOR.incrementAndGet();
    }

    public int getRadius() {
        return radius;
    }

    private boolean aimAngelReached() {
        return angelReached(aimAngel);
    }

    public boolean angelReached(double targetAngel) {
        return MathHelper.getAngel(angel, targetAngel) <= PRECISION_ANGEL;
    }

    /**
     * @param factor 1 if last call was exactly 1 second before
     */
    public DecimalPosition calculateMoveToTarget(double factor) {
        if (aimAngelReached()) {
            return decimalPosition.getPointFromAngelToNord(angel, speed * factor);
        } else {
            return decimalPosition;
        }
    }

    public void executeMoveToTarget(double factor) {
        if (aimAngelReached()) {
            decimalPosition = decimalPosition.getPointFromAngelToNord(angel, speed * factor);
            state = MoveState.MOVING;
            if (decimalPosition.getPosition().equals(path.getNextWayPosition())) {
                wayPointReached();
            }
        } else {
            double factorAngel = TURN_SPEED * factor;
            double actualDeltaAngel = MathHelper.getAngel(angel, aimAngel);
            if (factorAngel >= actualDeltaAngel) {
                // reached
                angel = aimAngel;
            } else {
                if (MathHelper.isCounterClock(angel, aimAngel)) {
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

    public void moveTo(Index destination) {
        path = new Path(destination);
        executeMove();
    }

    public void moveTo(List<Index> wayPoint) {
        path = new Path(wayPoint);
        executeMove();
    }

    private void executeMove() {
        aimAngel = decimalPosition.getAngleToNord(new DecimalPosition(path.getNextWayPosition()));
        speed = SPEED;
        state = MoveState.MOVING;
    }

    public Index getTargetPosition() {
        return path.getNextWayPosition();
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

    public double getAimAngel() {
        return aimAngel;
    }

    public void setTargetAngel(double targetAngel) {
        this.aimAngel = targetAngel;
    }

    public double getTargetAngel() {
        return decimalPosition.getAngleToNord(new DecimalPosition(path.getNextWayPosition()));
    }

    public void wayPointReached() {
        path.wayPointReached();
        if(path.isEmpty()) {
            stop();
        }
    }

    public void stop() {
        path = null;
        speed = 0;
        state = MoveState.STOPPED;
    }

    public double calculateArea() {
        return Math.PI * Math.pow(radius, 2);
    }

    @Override
    public String toString() {
        return "SyncItem{id=" + id + " Position: " + decimalPosition + " debugName: " + debugName;
    }
}
