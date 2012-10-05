package com.btxtech.game.jsre.common.gameengine.formation;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
* User: beat
* Date: 07.09.2011
* Time: 21:03:39
*/
public class AttackFormationItem {
    private SyncBaseItem syncBaseItem;
    private int range;
    private Index destinationHint;
    private double destinationAngel;
    private boolean inRange;

    public AttackFormationItem(SyncBaseItem syncBaseItem, int range, Index destinationHint, double destinationAngel, boolean inRange) {
        this.syncBaseItem = syncBaseItem;
        this.range = range;
        this.destinationHint = destinationHint;
        this.destinationAngel = destinationAngel;
        this.inRange = inRange;
    }

    public AttackFormationItem(SyncBaseItem syncBaseItem, int range) {
        this.syncBaseItem = syncBaseItem;
        this.range = range;
    }

    public Index getDestinationHint() {
        return destinationHint;
    }

    public double getDestinationAngel() {
        return destinationAngel;
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }

    public boolean isInRange() {
        return inRange;
    }

    public void setInRange(boolean inRange) {
        this.inRange = inRange;
    }

    public int getRange() {
        return range;
    }

    void setRange(int range) {
        this.range = range;
    }

    void setDestinationHint(Index destinationHint) {
        this.destinationHint = destinationHint;
    }

    void setDestinationAngel(double destinationAngel) {
        this.destinationAngel = destinationAngel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttackFormationItem)) return false;

        AttackFormationItem that = (AttackFormationItem) o;

        return syncBaseItem.equals(that.syncBaseItem);
    }

    @Override
    public int hashCode() {
        return syncBaseItem.hashCode();
    }

    @Override
    public String toString() {
        return "AttackFormationItem destinationHint: " + destinationHint + " inRange: " + inRange + " destinationAngel: " + destinationAngel;
    }
}
