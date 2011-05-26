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

import com.btxtech.game.jsre.client.ClientSyncItem;

/**
 * User: beat
 * Date: 16.11.2010
 * Time: 22:52:52
 */
public class CockpitMode implements SelectionListener {
    private boolean unloadMode = false;
    private boolean launchMode = false;

    /**
     * Singleton
     */
    public CockpitMode() {
        SelectionHandler.getInstance().addSelectionListener(this);
    }

    public boolean isUnloadMode() {
        return unloadMode;
    }

    public void setUnloadMode() {
        if (!unloadMode) {
            unloadMode = true;
            CursorHandler.getInstance().setUnloadContainer();
        }
    }

    public void clearUnloadMode() {
        if (unloadMode) {
            unloadMode = false;
            CursorHandler.getInstance().clearUnloadContainer();
        }
    }

    public boolean isLaunchMode() {
        return launchMode;
    }

    public void setLaunchMode() {
        if (!launchMode) {
            launchMode = true;
            CursorHandler.getInstance().setLaunch();
        }
    }

    public void clearLaunchMode() {
        if (launchMode) {
            launchMode = false;
            CursorHandler.getInstance().clearLaunch();
        }
    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItem selection) {
        if (selection.isSyncBaseItem()) {
            Cockpit.getInstance().getSelectedItemPanel().displayEnemyItem(selection.getSyncBaseItem());
        } else if (selection.isSyncResourceItem()) {
            Cockpit.getInstance().getSelectedItemPanel().displayResourceItem(selection.getSyncResourceItem());
        } else {
            throw new IllegalArgumentException(this + " can not set details for: " + selection);
        }
    }

    @Override
    public void onSelectionCleared() {
        Cockpit.getInstance().getSelectedItemPanel().displayNone();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        clearUnloadMode();
        clearLaunchMode();
        if (selectedGroup.getCount() == 1) {
            Cockpit.getInstance().getSelectedItemPanel().displayOwnSingleItem(selectedGroup.getFirst());
        } else {
            Cockpit.getInstance().getSelectedItemPanel().displayMultiOwnItems(selectedGroup);
        }
    }
}
