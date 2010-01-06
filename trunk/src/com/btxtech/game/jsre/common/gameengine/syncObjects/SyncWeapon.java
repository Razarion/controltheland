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
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;

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
            return retrunFalseIfReloaded();
        }

    }

    private boolean tickAttack(double factor) {
        try {
            SyncBaseItem targetItem = (SyncBaseItem) getServices().getItemService().getItem(target);
            if (isTargetInRange(targetItem.getPosition(), weaponType.getRange())) {
                if (getSyncBaseItem().hasSyncTurnable()) {
                    getSyncBaseItem().getSyncTurnable().turnTo(targetItem.getPosition());
                }
                if (reloadProgress >= weaponType.getReloadTime()) {
                    handleAttackState();
                    targetItem.decreaseHealth((int) Math.ceil(weaponType.getDemage() * reloadProgress / weaponType.getReloadTime()), getSyncBaseItem());
                    reloadProgress = 0;
                }
            } else {
                if (followTarget && getSyncBaseItem().hasSyncMovable()) {
                    getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, weaponType.getRange(), targetItem.getPosition());
                } else {
                    stop();
                    return retrunFalseIfReloaded();
                }
            }
            return true;
        } catch (ItemDoesNotExistException ignore) {
            // It has may be killed
            stop();
            return retrunFalseIfReloaded();
        }
    }

    private boolean retrunFalseIfReloaded() {
        return reloadProgress < weaponType.getReloadTime();

    }

    private void handleAttackState() {
        getSyncBaseItem().fireItemChanged(SyncItemListener.Change.ON_ATTACK);
    }

    public void stop() {
        target = null;
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
        SyncBaseItem target = (SyncBaseItem) getServices().getItemService().getItem(attackCommand.getTarget());
        if (!getSyncBaseItem().isEnemy(target)) {
            throw new IllegalArgumentException(this + " can not attack own base");
        }

        this.target = attackCommand.getTarget();
        followTarget = attackCommand.isFollowTarget();
    }

    public boolean inAttackRange(SyncItem target) {
        return getSyncBaseItem().getPosition().isInRadius(target.getPosition(), weaponType.getRange());
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
