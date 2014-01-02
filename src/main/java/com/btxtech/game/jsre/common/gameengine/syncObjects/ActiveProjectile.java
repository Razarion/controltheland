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
    private Index projectileTarget;
    private WeaponType weaponType;
    private int muzzleNr;
    private ActiveProjectileGroup activeProjectileGroup;
    private long lastTick;

    public ActiveProjectile(ActiveProjectileGroup activeProjectileGroup, SyncBaseItem syncBaseItem, Index projectileTarget, int angleIndex, WeaponType weaponType, int muzzleNr) {
        this.activeProjectileGroup = activeProjectileGroup;
        this.projectileTarget = projectileTarget;
        this.weaponType = weaponType;
        this.muzzleNr = muzzleNr;
        decimalPosition = new DecimalPosition(syncBaseItem.getSyncItemArea().getPosition().add(weaponType.getMuzzleFlashPosition(muzzleNr, angleIndex)));
    }

    public void tick(double factor) {
        decimalPosition = decimalPosition.getPointWithDistance(factor * (double) weaponType.getProjectileSpeed(), projectileTarget, false);
        lastTick = System.currentTimeMillis();
    }

    public boolean isTargetReached() {
        return decimalPosition.getPosition().equals(projectileTarget);
    }

    public Index getPosition() {
        return decimalPosition.getPosition();
    }

    public Index getInterpolatedPosition(long timeStamp) {
        if (lastTick == 0) {
            return getPosition();
        } else {
            double factor = (double)(timeStamp - lastTick) / 1000.0;
            return decimalPosition.getPointWithDistance(factor * (double) weaponType.getProjectileSpeed(), projectileTarget, false).getPosition();
        }
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

    public long getLastTick() {
        return lastTick;
    }
}
