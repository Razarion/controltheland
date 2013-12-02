/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 16:02:26
 */
public class SyncWeapon extends SyncBaseAbility {
    private static final long CHECK_DELTA = 1000;
    private WeaponType weaponType;
    private Id target;
    private boolean followTarget;
    private double reloadProgress;
    private Index targetPosition; // Not Synchronized
    private long targetPositionLastCheck; // Not Synchronized
    private List<DecimalPosition> projectilePositions; // Not Synchronized
    private Index projectileTarget; // Not Synchronized
    private SyncMovable.OverlappingHandler overlappingHandler = new SyncMovable.OverlappingHandler() {
        @Override
        public Path calculateNewPath() {
            try {
                SyncBaseItem targetItem = (SyncBaseItem) getPlanetServices().getItemService().getItem(target);
                return recalculateNewPath(weaponType.getRange(), targetItem.getSyncItemArea());
            } catch (ItemDoesNotExistException e) {
                stop();
                return null;
            }
        }
    };

    public SyncWeapon(WeaponType weaponType, SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.weaponType = weaponType;
    }

    public boolean isActive() {
        return target != null && getSyncBaseItem().isAlive();
    }

    /**
     * @param factor time in s since the last ticks
     * @return true if more tick are needed to fulfil the job
     */
    public boolean tick(double factor) {
        if (!getSyncBaseItem().isAlive()) {
            return false;
        }

        if (reloadProgress < weaponType.getReloadTime()) {
            reloadProgress += factor;
        }

        if (projectilePositions != null) {
            return tickProjectile(factor) || returnFalseIfReloaded();
        } else if (target != null) {
            return tickAttack(factor);
        } else {
            return returnFalseIfReloaded();
        }
    }

    private boolean tickProjectile(double factor) {
        for (int i = 0, projectilePositionSize = projectilePositions.size(); i < projectilePositionSize; i++) {
            DecimalPosition projectilePosition = projectilePositions.get(i);
            if (getWeaponType().getProjectileSpeed() != null) {
                projectilePosition = projectilePosition.getPointWithDistance(factor * (double) getWeaponType().getProjectileSpeed(), projectileTarget, false);
                projectilePositions.set(i, projectilePosition);
            }
            if (projectilePosition.getPosition().equals(projectileTarget)) {
                projectileDetonation();
                return false;
            }
        }
        return true;
    }

    private void projectileDetonation() {
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.PROJECTILE_DETONATION);
        try {
            SyncBaseItem targetItem = (SyncBaseItem) getPlanetServices().getItemService().getItem(target);
            targetItem.decreaseHealth(weaponType.getDamage() * reloadProgress / weaponType.getReloadTime(), getSyncBaseItem().getBase());
            targetItem.onAttacked(getSyncBaseItem());
        } catch (ItemDoesNotExistException e) {
            // Ignore
        } catch (TargetHasNoPositionException e) {
            // Ignore
        }
        reloadProgress = 0;
        projectilePositions = null;
        projectileTarget = null;
    }

    private boolean tickAttack(double factor) {
        try {
            if (followTarget && !getSyncBaseItem().hasSyncMovable()) {
                throw new IllegalArgumentException("Weapon is followTarget but has now SyncMovable: " + getSyncBaseItem());
            }

            SyncBaseItem targetItem = (SyncBaseItem) getPlanetServices().getItemService().getItem(target);

            if (!getPlanetServices().getBaseService().isEnemy(getSyncBaseItem(), targetItem)) {
                // May the guild member state has changed
                return false;
            }

            // Check if target has moved away
            if (targetPositionLastCheck + CHECK_DELTA < System.currentTimeMillis() && followTarget && getSyncBaseItem().hasSyncMovable() && isNewPathRecalculationAllowed()) {
                if (targetPosition != null) {
                    if (!targetPosition.equals(targetItem.getSyncItemArea().getPosition())) {
                        targetPosition = targetItem.getSyncItemArea().getPosition();
                        targetPositionLastCheck = System.currentTimeMillis();
                        if (isInRange(targetItem)) {
                            doAttack(targetItem);
                            return true;
                        } else {
                            recalculateAndSetNewPath(weaponType.getRange(), targetItem.getSyncItemArea());
                            getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                            return getSyncBaseItem().getSyncMovable().tickMove(factor, overlappingHandler);
                        }
                    }
                }
                targetPosition = targetItem.getSyncItemArea().getPosition();
                targetPositionLastCheck = System.currentTimeMillis();
            }

            if (followTarget && getSyncBaseItem().hasSyncMovable() && getSyncBaseItem().getSyncMovable().tickMove(factor, overlappingHandler)) {
                return true;
            }


            if (!followTarget && !isInRange(targetItem)) {
                stop();
                return returnFalseIfReloaded();
            }

            if (!isInRange(targetItem)) {
                if (isNewPathRecalculationAllowed()) {
                    // Destination place was may be taken. Calculate a new one or target has moved away
                    recalculateAndSetNewPath(weaponType.getRange(), targetItem.getSyncItemArea());
                    getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                    return true;
                } else {
                    return false;
                }
            }

            doAttack(targetItem);
            return true;
        } catch (ItemDoesNotExistException ignore) {
            // It has may be killed
            stop();
            return returnFalseIfReloaded();
        } catch (TargetHasNoPositionException e) {
            // Target may moved to a container
            stop();
            return returnFalseIfReloaded();
        }
    }

    private void doAttack(SyncBaseItem targetItem) {
        getSyncItemArea().turnTo(targetItem);
        if (reloadProgress >= weaponType.getReloadTime()) {
            projectilePositions = new ArrayList<DecimalPosition>();
            int angleIndex = getSyncItemArea().getAngelIndex();
            for (Index[] indexes : weaponType.getMuzzleFlashPositions()) {
                projectilePositions.add(new DecimalPosition(getSyncItemArea().getPosition().add(indexes[angleIndex])));
            }
            projectileTarget = targetItem.getSyncItemArea().getPosition();
            getSyncBaseItem().fireItemChanged(SyncItemListener.Change.ON_FIRING);
            if (weaponType.getProjectileSpeed() == null) {
                projectileDetonation();
            }
        }
    }

    private boolean returnFalseIfReloaded() {
        return reloadProgress < weaponType.getReloadTime();

    }

    public void stop() {
        target = null;
        targetPosition = null;
        targetPositionLastCheck = 0;
        projectilePositions = null;
        projectileTarget = null;
        if (getSyncBaseItem().hasSyncMovable()) {
            getSyncBaseItem().getSyncMovable().stop();
        }
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        target = syncItemInfo.getTarget();
        followTarget = syncItemInfo.isFollowTarget();
        reloadProgress = syncItemInfo.getReloadProgress();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setTarget(target);
        syncItemInfo.setFollowTarget(followTarget);
        syncItemInfo.setReloadProgress(reloadProgress);
    }

    public void executeCommand(AttackCommand attackCommand) throws ItemDoesNotExistException {
        if (!getSyncBaseItem().isReady()) {
            return;
        }
        SyncBaseItem target = (SyncBaseItem) getPlanetServices().getItemService().getItem(attackCommand.getTarget());
        if (!getSyncBaseItem().isEnemy(target)) {
            throw new IllegalArgumentException("Can not attack friendly target. Own: " + getSyncBaseItem() + " target: " + target);
        }

        if (!isItemTypeAllowed(target)) {
            throw new IllegalArgumentException(this + " Weapon not allowed to attack item type: " + target);
        }

        this.target = attackCommand.getTarget();
        followTarget = attackCommand.isFollowTarget();
        setPathToDestinationIfSyncMovable(attackCommand.getPathToDestination());
        targetPosition = null;
        targetPositionLastCheck = 0;
        projectilePositions = null;
        projectileTarget = null;
    }

    public boolean isItemTypeAllowed(SyncBaseItem target) {
        return weaponType.isItemTypeAllowed(target.getBaseItemType().getId());
    }

    public boolean isAttackAllowedWithoutMoving(SyncItem target) throws TargetHasNoPositionException {
        if (!(target instanceof SyncBaseItem)) {
            return false;
        }
        SyncBaseItem baseTarget = (SyncBaseItem) target;
        return isItemTypeAllowed(baseTarget) && isInRange(baseTarget);

    }

    public boolean isAttackAllowed(SyncItem target) {
        return target instanceof SyncBaseItem
                && getSyncItemArea().hasPosition()
                && target.getSyncItemArea().hasPosition()
                && isItemTypeAllowed((SyncBaseItem) target);
    }

    public boolean isInRange(SyncBaseItem target) throws TargetHasNoPositionException {
        return getSyncItemArea().isInRange(weaponType.getRange(), target);
    }

    public Id getTarget() {
        return target;
    }

    public void setTarget(Id target) {
        this.target = target;
    }

    public boolean isFollowTarget() {
        return followTarget;
    }

    public void setFollowTarget(boolean followTarget) {
        this.followTarget = followTarget;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public double getReloadProgress() {
        return reloadProgress;
    }

    public void setReloadProgress(double reloadProgress) {
        this.reloadProgress = reloadProgress;
    }

    public Index getProjectilePosition(int muzzleFlashNr) {
        if (projectilePositions != null) {
            return projectilePositions.get(muzzleFlashNr).getPosition();
        } else {
            return null;
        }
    }

    public Index getProjectileTarget() {
        return projectileTarget;
    }
}
