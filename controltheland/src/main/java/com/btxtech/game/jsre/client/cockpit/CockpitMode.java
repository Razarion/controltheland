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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemPlacer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 16.11.2010
 * Time: 22:52:52
 */
public class CockpitMode implements SelectionListener {
    public enum Mode {
        UNLOAD,
        LAUNCH,
        SELL
    }

    private static final CockpitMode INSTANCE = new CockpitMode();
    private Mode mode;
    private boolean isMovePossible;
    private boolean isLoadPossible;
    private boolean isAttackPossible;
    private boolean isCollectPossible;
    private boolean isFinalizeBuildPossible;
    private GroupSelectionFrame groupSelectionFrame;
    private InventoryItemPlacer inventoryItemPlacer;
    private ToBeBuildPlacer toBeBuildPlacer;

    public static CockpitMode getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private CockpitMode() {
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            if (mode != null) {
                SelectionHandler.getInstance().clearSelection();
                groupSelectionFrame = null;
                inventoryItemPlacer = null;
            }
            if (mode != Mode.SELL) {
                SideCockpit.getInstance().clearSellMode();
            }
        }
    }

    public void reset() {
        setMode(null);
        clearPossibilities();
        groupSelectionFrame = null;
        inventoryItemPlacer = null;
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        ItemCockpit.getInstance().deActivate();
    }

    @Override
    public void onSelectionCleared() {
        clearPossibilities();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        setMode(null);

        if (selectedGroup.getCount() == 1) {
            if (ItemCockpit.hasItemCockpit(selectedGroup.getFirst())) {
                ItemCockpit.getInstance().activate(selectedGroup.getFirst());
            } else {
                ItemCockpit.getInstance().deActivate();
            }
        } else {
            ItemCockpit.getInstance().deActivate();
        }

        if (selectedGroup.canMove()) {
            isMovePossible = true;
            isLoadPossible = true;
        } else {
            isMovePossible = false;
            isLoadPossible = false;
        }
        isAttackPossible = selectedGroup.canAttack();
        isCollectPossible = selectedGroup.canCollect();
        isFinalizeBuildPossible = selectedGroup.canFinalizeBuild();
    }

    public boolean isMovePossible() {
        return isMovePossible;
    }

    public boolean isLoadPossible() {
        return isLoadPossible;
    }

    public boolean isAttackPossible() {
        return isAttackPossible;
    }

    public boolean isCollectPossible() {
        return isCollectPossible;
    }

    public boolean isFinalizeBuildPossible() {
        return isFinalizeBuildPossible;
    }

    public void setGroupSelectionFrame(GroupSelectionFrame groupSelectionFrame) {
        this.groupSelectionFrame = groupSelectionFrame;
        inventoryItemPlacer = null;
        toBeBuildPlacer = null;
    }

    public boolean hasGroupSelectionFrame() {
        return groupSelectionFrame != null;
    }

    public GroupSelectionFrame getGroupSelectionFrame() {
        return groupSelectionFrame;
    }

    public void setInventoryItemPlacer(InventoryItemPlacer inventoryItemPlacer) {
        SelectionHandler.getInstance().clearSelection();
        this.inventoryItemPlacer = inventoryItemPlacer;
        groupSelectionFrame = null;
        toBeBuildPlacer = null;
    }

    public boolean hasInventoryItemPlacer() {
        return inventoryItemPlacer != null;
    }

    public InventoryItemPlacer getInventoryItemPlacer() {
        return inventoryItemPlacer;
    }

    public ToBeBuildPlacer getToBeBuildPlacer() {
        return toBeBuildPlacer;
    }

    public boolean hasToBeBuildPlacer() {
        return toBeBuildPlacer != null;
    }

    public void setToBeBuildPlacer(ToBeBuildPlacer toBeBuildPlacer) {
        SelectionHandler.getInstance().clearSelection();
        this.toBeBuildPlacer = toBeBuildPlacer;
        groupSelectionFrame = null;
        inventoryItemPlacer = null;
    }

    private void clearPossibilities() {
        isMovePossible = false;
        isLoadPossible = false;
        isAttackPossible = false;
        isCollectPossible = false;
        isFinalizeBuildPossible = false;
        groupSelectionFrame = null;
        toBeBuildPlacer = null;
    }
}
