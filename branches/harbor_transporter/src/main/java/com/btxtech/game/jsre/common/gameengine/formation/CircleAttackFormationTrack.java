package com.btxtech.game.jsre.common.gameengine.formation;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

/**
 * User: beat
 * Date: 19.08.2011
 * Time: 11:32:28
 */

public class CircleAttackFormationTrack {
    private Index middle;
    private int radius;
    private boolean counterClock;
    private double currentAngel;
    private SyncItemArea last;


    public CircleAttackFormationTrack(double startAngel, SyncItemArea target, int range, boolean counterClock) {
        this.counterClock = counterClock;
        middle = target.getPosition();
        radius = range;
        currentAngel = startAngel;
    }

    public SyncItemArea getStartPoint(AttackFormationItem attackFormationItem) {
        SyncItemArea startPoint =  createSyncItemArea(attackFormationItem);
        BoundingBox boundingBox = attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox();
        double deltaAngel = Math.asin(1.0 / (double) (radius + boundingBox.getRadius()));
        if (counterClock) {
            currentAngel += deltaAngel;
        } else {
            currentAngel -= deltaAngel;
        }
        return startPoint;
    }

    public SyncItemArea getNextPoint(AttackFormationItem attackFormationItem) {
        if (last == null) {
            throw new IllegalStateException("CircleAttackFormationTrack: last == null");
        }
        double deltaAngelLast = Math.asin(((double) last.getBoundingBox().getRadius() + 1.0) / (double) (radius + last.getBoundingBox().getRadius()));
        BoundingBox boundingBox = attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox();
        double deltaAngelCurrent = Math.asin(((double) boundingBox.getRadius() + 1.0) / ((double) radius + boundingBox.getRadius()));
        double deltaAngel = deltaAngelLast + deltaAngelCurrent;
        if (counterClock) {
            currentAngel += deltaAngel;
        } else {
            currentAngel -= deltaAngel;
        }
        last = createSyncItemArea(attackFormationItem);
        return last;
    }

    private SyncItemArea createSyncItemArea(AttackFormationItem attackFormationItem) {
        BoundingBox boundingBox = attackFormationItem.getSyncBaseItem().getSyncItemArea().getBoundingBox();
        Index center = middle.getPointFromAngelToNord(currentAngel, radius + boundingBox.getRadius());
        SyncItemArea syncItemArea = boundingBox.createSyntheticSyncItemArea(center);
        syncItemArea.turnTo(middle);
        return syncItemArea;
    }

    public void setLast(SyncItemArea last) {
        currentAngel = middle.getAngleToNord(last.getPosition());
        this.last = last;
    }

    public SyncItemArea getLast() {
        return last;
    }
}
