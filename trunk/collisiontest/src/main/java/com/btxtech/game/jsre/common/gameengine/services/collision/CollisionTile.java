package com.btxtech.game.jsre.common.gameengine.services.collision;

/**
 * User: beat
 * Date: 05.01.14
 * Time: 14:55
 */
public class CollisionTile {
    private boolean blocked;

    public boolean isFree() {
        return !blocked;
    }

    public void setBlocked() {
        if(blocked) {
            throw new BlockingStateException("CollisionTile already blocked");
        }
        blocked = true;
    }

    public void clearBlocked() {
        if(!blocked) {
            throw new BlockingStateException("CollisionTile is not blocked");
        }
        blocked = false;
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public String toString() {
        return "CollisionTile{" +
                "blocked=" + blocked +
                '}';
    }
}
