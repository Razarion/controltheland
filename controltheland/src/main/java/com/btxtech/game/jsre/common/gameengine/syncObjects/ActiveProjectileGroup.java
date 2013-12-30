package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 18.10.13
 * Time: 09:04
 */
public class ActiveProjectileGroup {
    private Collection<ActiveProjectile> projectiles = new ArrayList<ActiveProjectile>();
    private boolean alive;

    public ActiveProjectileGroup(SyncBaseItem syncBaseItem, WeaponType weaponType, Index projectileTarget) {
        int angleIndex = syncBaseItem.getSyncItemArea().getAngelIndex();
        for (int muzzleFlashNr = 0; muzzleFlashNr < weaponType.getMuzzleFlashCount(); muzzleFlashNr++) {
            ActiveProjectile activeProjectile = new ActiveProjectile(this, syncBaseItem, projectileTarget, angleIndex, weaponType, muzzleFlashNr);
            projectiles.add(activeProjectile);
        }
        alive = true;
        syncBaseItem.fireItemChanged(SyncItemListener.Change.PROJECTILE_LAUNCHED, this);
    }

    public void tick(double factor, WeaponType weaponType) {
        for (ActiveProjectile activeProjectile : projectiles) {
            if (weaponType.getProjectileSpeed() != null) {
                activeProjectile.tick(factor);
                if (activeProjectile.isTargetReached()) {
                    alive = false;
                }
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void clearActive() {
        alive = false;
    }

    public Collection<ActiveProjectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public String toString() {
        return "ActiveProjectileGroup{" +
                "alive=" + alive +
                ", projectiles=" + projectiles +
                '}';
    }
}
