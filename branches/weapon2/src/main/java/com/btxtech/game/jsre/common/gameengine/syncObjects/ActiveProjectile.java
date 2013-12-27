package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;

/**
 * User: beat
 * Date: 18.10.13
 * Time: 08:08
 */
public class ActiveProjectile {
    private DecimalPosition decimalPosition;
    private int muzzleNr;
    private ActiveProjectileGroup activeProjectileGroup;

    public ActiveProjectile(ActiveProjectileGroup activeProjectileGroup, SyncBaseItem syncBaseItem, int angleIndex, WeaponType weaponType, int muzzleNr) {
        this.activeProjectileGroup = activeProjectileGroup;
        this.muzzleNr = muzzleNr;
        decimalPosition = new DecimalPosition(syncBaseItem.getSyncItemArea().getPosition().add(weaponType.getMuzzleFlashPosition(muzzleNr, angleIndex)));
    }

    public void tick(double factor, Integer projectileSpeed, Index projectileTarget) {
        decimalPosition = decimalPosition.getPointWithDistance(factor * (double) projectileSpeed, projectileTarget, false);
    }

    public boolean isTargetReached(Index projectileTarget) {
        return decimalPosition.getPosition().equals(projectileTarget);
    }

    public Index getPosition() {
        return decimalPosition.getPosition();
    }

    public int getMuzzleNr() {
        return muzzleNr;
    }

    public boolean isAlive() {
        return activeProjectileGroup.isAlive();
    }

    @Override
    public String toString() {
        return "ActiveProjectile{" +
                "decimalPosition=" + decimalPosition +
                ", muzzleNr=" + muzzleNr +
                '}';
    }
}
