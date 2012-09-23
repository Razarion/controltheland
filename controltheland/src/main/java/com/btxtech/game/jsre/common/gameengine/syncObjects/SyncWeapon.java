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

import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 16:02:26
 */
public class SyncWeapon extends SyncBaseAbility {
    private WeaponType weaponType;
    private Id target;
    private boolean followTarget;
    private double reloadProgress;

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

        if (target != null) {
            return tickAttack(factor);
        } else {
            return returnFalseIfReloaded();
        }
    }

    private boolean tickAttack(double factor) {
        try {
            if (followTarget && !getSyncBaseItem().hasSyncMovable()) {
                throw new IllegalArgumentException("Weapon is followTarget but has now SyncMovable: " + getSyncBaseItem());
            }

            if (followTarget && getSyncBaseItem().hasSyncMovable() && getSyncBaseItem().getSyncMovable().tickMove(factor)) {
                return true;
            }

            SyncBaseItem targetItem = (SyncBaseItem) getPlanetServices().getItemService().getItem(target);
            if (!followTarget && !isInRange(targetItem)) {
                stop();
                return returnFalseIfReloaded();
            }

            if (!isInRange(targetItem)) {
                if (isNewPathRecalculationAllowed()) {
                    // Destination place was may be taken. Calculate a new one or target has moved away
                    recalculateNewPath(weaponType.getRange(), targetItem.getSyncItemArea(), targetItem.getTerrainType());
                    getPlanetServices().getConnectionService().sendSyncInfo(getSyncBaseItem());
                    return true;
                } else {
                    return false;
                }
            }

            getSyncItemArea().turnTo(targetItem);
            if (reloadProgress >= weaponType.getReloadTime()) {
                handleAttackState();
                targetItem.decreaseHealth(weaponType.getDemage() * reloadProgress / weaponType.getReloadTime(), getSyncBaseItem().getBase());
                targetItem.onAttacked(getSyncBaseItem());
                reloadProgress = 0;
            }
            return true;
        }
        catch (ItemDoesNotExistException ignore) {
            // It has may be killed
            stop();
            return returnFalseIfReloaded();
        }
    }

    private boolean returnFalseIfReloaded() {
        return reloadProgress < weaponType.getReloadTime();

    }

    private void handleAttackState() {
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.ON_ATTACK);
    }

    public void stop() {
        target = null;
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
        SyncBaseItem target = (SyncBaseItem) getPlanetServices().getItemService().getItem(attackCommand.getTarget());
        if (!getSyncBaseItem().isEnemy(target)) {
            throw new IllegalArgumentException(this + " can not attack own base");
        }

        if (!isItemTypeAllowed(target)) {
            throw new IllegalArgumentException(this + " Weapon not allowed to attack item type: " + target);
        }

        this.target = attackCommand.getTarget();
        followTarget = attackCommand.isFollowTarget();
        setPathToDestinationIfSyncMovable(attackCommand.getPathToDestination());
    }

    public boolean isItemTypeAllowed(SyncBaseItem target) {
        return weaponType.isItemTypeAllowed(target.getBaseItemType().getId());
    }

    public boolean isAttackAllowedWithoutMoving(SyncItem target) {
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

    public boolean isInRange(SyncBaseItem target) {
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
}
