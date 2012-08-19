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

package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.ToolTips;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 18.11.2010
 * Time: 10:51:09
 */
public class SpecialFunctionPanel extends VerticalPanel {

    public SpecialFunctionPanel(SyncBaseItem syncBaseItem) {
        setHeight("100%");
        if (syncBaseItem.isUpgradeable()) {
            addUpgradeable(syncBaseItem);
        }
        if (syncBaseItem.hasSyncItemContainer()) {
            addSyncItemContainer(syncBaseItem.getSyncItemContainer());
        }
        if (syncBaseItem.hasSyncLauncher()) {
            addSyncLauncher();
        }
    }

    private void addUpgradeable(final SyncBaseItem upgradeable) {
        ExtendedCustomButton button = new ExtendedCustomButton("upgradeButton", false, ToolTips.TOOL_TIP_UPGRADE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ActionHandler.getInstance().upgrade(upgradeable);
            }
        });
        button.getUpDisabledFace().setImage(new Image("/images/cockpit/upgradeButton-disabled-up.png"));
        if (ClientLevelHandler.getInstance().isItemTypeAllowed(upgradeable.getBaseItemType().getUpgradeable())) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
            button.setTitle(ToolTips.TOOL_TIP_UPGRADE_NO);
        }
        add(button);
    }

    private void addSyncItemContainer(SyncItemContainer syncItemContainer) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        ExtendedCustomButton button = new ExtendedCustomButton("unloadButton", false, ToolTips.TOOL_TIP_UNLOAD, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CockpitMode.getInstance().setMode(CockpitMode.Mode.UNLOAD);
            }
        });
        button.ensureDebugId("unloadButton");
        button.setEnabled(!syncItemContainer.getContainedItems().isEmpty());
        horizontalPanel.add(button);
        horizontalPanel.add(new HTML("&nbsp;Items: " + syncItemContainer.getContainedItems().size()));
        add(horizontalPanel);
    }

    private void addSyncLauncher() {
        ExtendedCustomButton button = new ExtendedCustomButton("launchButton", false, ToolTips.TOOL_TIP_LAUNCH, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CockpitMode.getInstance().setMode(CockpitMode.Mode.LAUNCH);
            }
        });
        add(button);
    }
}
