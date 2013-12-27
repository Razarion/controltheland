package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 17.10.13
 * Time: 02:28
 */
public class ActiveProjectileContainer {
    private SyncBaseItem syncBaseItem;
    private WeaponType weaponType;
    private List<ActiveProjectileGroup> projectiles = new ArrayList<ActiveProjectileGroup>();
    private Index projectileTarget;
    private SyncBaseItem target;

    public ActiveProjectileContainer(WeaponType weaponType, SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
        this.weaponType = weaponType;
    }

    public void createProjectile(SyncBaseItem target) {
        this.target = target;
        projectileTarget = target.getSyncItemArea().getPosition();
        ActiveProjectileGroup projectileGroup = new ActiveProjectileGroup(syncBaseItem, weaponType);
        projectiles.add(projectileGroup);
        if (weaponType.getProjectileSpeed() == null) {
            projectileDetonation(projectileGroup);
        }
    }

    public boolean tick(double factor) {
        Collection<ActiveProjectileGroup> detonation = new ArrayList<ActiveProjectileGroup>();
        for (ActiveProjectileGroup projectileGroup : projectiles) {
            projectileGroup.tick(factor, weaponType, projectileTarget);
            if(!projectileGroup.isAlive())  {
                detonation.add(projectileGroup);
            }
        }

        for (ActiveProjectileGroup projectileGroup : detonation) {
            projectileDetonation(projectileGroup);
        }

        return !projectiles.isEmpty();
    }

    public void clear() {
        for (ActiveProjectileGroup projectile : projectiles) {
            projectile.clearActive();
        }
        projectiles.clear();
        projectileTarget = null;
    }

    private void projectileDetonation(ActiveProjectileGroup projectileGroup) {
        syncBaseItem.fireItemChanged(SyncItemListener.Change.PROJECTILE_DETONATION, null);
        if (target != null && target.isAlive() && target.getSyncItemArea().hasPosition()) {
            target.decreaseHealth(weaponType.getDamage(), syncBaseItem.getBase());
            try {
                target.onAttacked(syncBaseItem);
            } catch (TargetHasNoPositionException e) {
                // Ignore
            }
        }
        projectiles.remove(projectileGroup);
        if (projectiles.isEmpty()) {
            projectileTarget = null;
        }
    }

    public Index getProjectileTarget() {
        return projectileTarget;
    }
}
