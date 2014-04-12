package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: beat
 * Date: 20.03.13
 * Time: 21:36
 */
public class SyncItem {
    private static final double SPEED = 40;
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    public enum MoveState {
        MOVING,
        STOPPED,
        BLOCKED
    }

    // General
    private String debugName;
    private int id;
    private MoveState state = MoveState.STOPPED;
    // Item type
    private int radius;
    // SyncItemArea
    private DecimalPosition decimalPosition;
    // Moving
    private Index targetPosition;
    private Path path;
    private Collection<Index> blockingCollisionTiles;

    public SyncItem(int radius, Index position, String debugName) {
        this.debugName = debugName;
        this.radius = radius;
        decimalPosition = new DecimalPosition(position);
        id = ID_GENERATOR.incrementAndGet();
    }

    public int getRadius() {
        return radius;
    }

    /**
     * @param factor 1 if last call was exactly 1 second before
     */
    public DecimalPosition calculateMoveToTarget(double factor) {
        if (state != MoveState.STOPPED) {
            return path.calculatePosition(factor, SPEED, decimalPosition);
        } else {
            throw new IllegalStateException("Item is not moving: " + this);
        }
    }

    public void executeMoveToTarget(double factor) {
        if (state != MoveState.STOPPED) {
            decimalPosition = path.moveToCurrentPosition(factor, SPEED, decimalPosition);
            if (path.isEmpty()) {
                path = null;
                targetPosition = null;
                state = MoveState.STOPPED;
            } else {
                state = MoveState.MOVING;
            }
        } else {
            throw new IllegalStateException("Item is not moving: " + this);
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

    public void setBlocked() {
        state = MoveState.BLOCKED;
        path = null;
    }

    public int getId() {
        return id;
    }

    public void setBlockingCollisionTiles(Collection<Index> blockingCollisionTiles) {
        this.blockingCollisionTiles = blockingCollisionTiles;
    }

    public Collection<Index> getBlockingCollisionTiles() {
        return blockingCollisionTiles;
    }

    public void setTargetPosition(Path pathToDestination) {
        targetPosition = pathToDestination.getAbsoluteDestination();
        this.path = pathToDestination;
        state = MoveState.MOVING;
    }

    public void stop() {
        state = MoveState.STOPPED;
        targetPosition = null;
        path = null;
    }

    public Path getPath() {
        return path;
    }

    public Index getTargetPosition() {
        return targetPosition;
    }

    @Override
    public String toString() {
        return "SyncItem{id=" + id + " Position: " + decimalPosition + " debugName: " + debugName + " targetPosition: " + targetPosition + '}';
    }
}
