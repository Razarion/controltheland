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

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 18.11.2010
 * Time: 10:51:09
 */
public class SpecialFunctionPanel extends VerticalPanel {
    private static final int WIDTH = 92;
    private static final int HEIGHT = 76;

    public SpecialFunctionPanel() {
        setPixelSize(WIDTH, HEIGHT);
    }

    public void display(SyncBaseItem syncBaseItem) {
        clear();
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
        ExtendedCustomButton button = new ExtendedCustomButton("/images/cockpit/upgradeButton-up.png", "/images/cockpit/upgradeButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ActionHandler.getInstance().upgrade(upgradeable);
            }
        });
        button.getUpDisabledFace().setImage(new Image("/images/cockpit/upgradeButton-disabled-up.png"));
        button.setEnabled(ClientServices.getInstance().getItemTypeAccess().isAllowed(upgradeable.getBaseItemType().getUpgradeable()));
        add(button);
    }

    private void addSyncItemContainer(SyncItemContainer syncItemContainer) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        ExtendedCustomButton button = new ExtendedCustomButton("/images/cockpit/unloadButton-up.png", "/images/cockpit/unloadButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CockpitNew.getInstance().getCockpitMode().setUnloadMode();
            }
        });
        horizontalPanel.add(button);
        horizontalPanel.add(new HTML("&nbsp;Items: " + syncItemContainer.getContainedItems().size()));
        add(horizontalPanel);
    }

    private void addSyncLauncher() {
        ExtendedCustomButton button = new ExtendedCustomButton("/images/cockpit/launchButton-up.png", "/images/cockpit/launchButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CockpitNew.getInstance().getCockpitMode().setUnloadMode();
            }
        });
        add(button);
    }
}
