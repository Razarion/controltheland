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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.AttackFormation;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;

/**
 * User: beat
 * Date: 18.11.2009
 * Time: 16:02:26
 */
public class SyncWeapon extends SyncBaseAbility {
    private WeaponType weaponType;
    private Id target;
    private Index destinationHint;
    private Double destinationAngel;
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
            return returnFalseIfReloaded();
        }

    }

    private boolean tickAttack(double factor) {
        try {
            SyncBaseItem targetItem = (SyncBaseItem) getServices().getItemService().getItem(target);
            if (isInRange(targetItem) && destinationHint == null) {
                getSyncItemArea().turnTo(targetItem);
                if (!getServices().getTerritoryService().isAllowed(targetItem.getSyncItemArea().getPosition(), getSyncBaseItem())) {
                    throw new IllegalArgumentException(this + " Weapon not allowed to attack item on territory: " + targetItem.getSyncItemArea().getPosition() + "  " + getSyncBaseItem());
                }
                if (!getServices().getTerritoryService().isAllowed(getSyncItemArea().getPosition(), getSyncBaseItem())) {
                    throw new IllegalArgumentException(this + " Weapon not allowed to attack on territory: " + getSyncItemArea().getPosition() + "  " + getSyncBaseItem());
                }
                if (reloadProgress >= weaponType.getReloadTime()) {
                    handleAttackState();
                    targetItem.decreaseHealth(weaponType.getDemage() * reloadProgress / weaponType.getReloadTime(), getSyncBaseItem().getBase());
                    targetItem.onAttacked(getSyncBaseItem());
                    reloadProgress = 0;
                }
            } else {
                if (followTarget && getSyncBaseItem().hasSyncMovable()) {
                    if (getSyncItemArea().positionReached(destinationHint)) {
                        // Target has moved away
                        SyncItemArea targetSyncItemArea;
                        if (targetItem.hasSyncMovable() && targetItem.getSyncMovable().getDestination() != null) {
                            targetSyncItemArea = targetItem.getBaseItemType().getBoundingBox().createSyntheticSyncItemArea(targetItem.getSyncMovable().getDestination());
                        } else {
                            targetSyncItemArea = targetItem.getSyncItemArea();
                        }
                        AttackFormation.AttackFormationItem formation = getServices().getCollisionService().getDestinationHint(getSyncBaseItem(), weaponType.getRange(), targetSyncItemArea, targetItem.getBaseItemType().getTerrainType());
                        if (formation != null) {
                            destinationHint = formation.getDestinationHint();
                            destinationAngel = formation.getDestinationAngel();
                        }
                        if (destinationHint != null) {
                            boolean moreTicksNeeded = getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, destinationHint, destinationAngel, targetItem.getSyncItemArea().getPosition());
                            if (!moreTicksNeeded) {
                                destinationHint = null;
                            }
                        } else {
                            stop();
                        }
                    } else {
                        boolean moreTicksNeeded = getSyncBaseItem().getSyncMovable().tickMoveToTarget(factor, destinationHint, destinationAngel, targetItem.getSyncItemArea().getPosition());
                        if (!moreTicksNeeded) {
                            destinationHint = null;
                        }
                    }
                } else {
                    stop();
                    return returnFalseIfReloaded();
                }
            }
            return true;
        } catch (ItemDoesNotExistException ignore) {
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
        destinationAngel = null;
        destinationHint = null;
        if (getSyncBaseItem().hasSyncMovable()) {
            getSyncBaseItem().getSyncMovable().stop();
        }
    }

    @Override
    public void synchronize(SyncItemInfo syncItemInfo) {
        target = syncItemInfo.getTarget();
        followTarget = syncItemInfo.isFollowTarget();
        reloadProgress = syncItemInfo.getReloadProgress();
        destinationAngel = syncItemInfo.getDestinationAngel();
        destinationHint = syncItemInfo.getDestinationHint();
    }

    @Override
    public void fillSyncItemInfo(SyncItemInfo syncItemInfo) {
        syncItemInfo.setTarget(target);
        syncItemInfo.setFollowTarget(followTarget);
        syncItemInfo.setReloadProgress(reloadProgress);
        syncItemInfo.setDestinationAngel(destinationAngel);
        syncItemInfo.setDestinationHint(destinationHint);
    }

    public void executeCommand(AttackCommand attackCommand) throws ItemDoesNotExistException {
        SyncBaseItem target = (SyncBaseItem) getServices().getItemService().getItem(attackCommand.getTarget());
        if (!getSyncBaseItem().isEnemy(target)) {
            throw new IllegalArgumentException(this + " can not attack own base");
        }

        if (!isItemTypeAllowed(target)) {
            throw new IllegalArgumentException(this + " Weapon not allowed to attack item type: " + target);
        }

        if (!getServices().getTerritoryService().isAllowed(target.getSyncItemArea().getPosition(), getSyncBaseItem())) {
            throw new IllegalArgumentException(this + " Weapon not allowed to attack item on territory: " + target.getSyncItemArea() + "  " + getSyncBaseItem());
        }

        this.target = attackCommand.getTarget();
        destinationHint = attackCommand.getDestinationHint();
        destinationAngel = attackCommand.getDestinationAngel();
        followTarget = attackCommand.isFollowTarget();
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
