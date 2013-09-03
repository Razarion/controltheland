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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat Date: 18.11.2010 Time: 10:51:09
 */
public class SpecialFunctionPanel extends Composite {
    private static SpecialFunctionPanelUiBinder uiBinder = GWT.create(SpecialFunctionPanelUiBinder.class);
    @UiField
    PushButton unloadButton;
    @UiField
    PushButton upgradeButton;
    @UiField
    PushButton launchButton;
    @UiField
    Label containedItems;
    private final SyncBaseItem syncBaseItem;

    interface SpecialFunctionPanelUiBinder extends UiBinder<Widget, SpecialFunctionPanel> {
    }

    public SpecialFunctionPanel(SyncBaseItem syncBaseItem) {
        this.syncBaseItem = syncBaseItem;
        initWidget(uiBinder.createAndBindUi(this));

        if (syncBaseItem.hasSyncItemContainer()) {
            unloadButton.setEnabled(!syncBaseItem.getSyncItemContainer().getContainedItems().isEmpty());
            containedItems.setText(Integer.toString(syncBaseItem.getSyncItemContainer().getContainedItems().size()));
        } else {
            unloadButton.setVisible(false);
            containedItems.setVisible(false);
        }

        if (syncBaseItem.isUpgradeable()) {
            try {
                BaseItemType upgradeTo = (BaseItemType) ItemTypeContainer.getInstance().getItemType(syncBaseItem.getBaseItemType().getUpgradeable());
                upgradeButton.setEnabled(!ClientBase.getInstance().isLevelLimitation4ItemTypeExceeded(upgradeTo, ClientBase.getInstance().getSimpleBase()));
            } catch (NoSuchItemTypeException e) {
                ClientExceptionHandler.handleException(e);
            }
        } else {
            upgradeButton.setVisible(false);

        }

        launchButton.setVisible(syncBaseItem.hasSyncLauncher());
    }

    public static boolean hasSpecialFuntion(SyncBaseItem syncBaseItem) {
        if (syncBaseItem.hasSyncItemContainer()) {
            return true;
        }
        if (syncBaseItem.isUpgradeable()) {
            return true;
        }
        return syncBaseItem.hasSyncLauncher();
    }

    @UiHandler("unloadButton")
    void onUnloadButtonClick(ClickEvent event) {
        CockpitMode.getInstance().setMode(CockpitMode.Mode.UNLOAD);
    }

    @UiHandler("upgradeButton")
    void onUpgradeButtonClick(ClickEvent event) {
        ActionHandler.getInstance().upgrade(syncBaseItem);
    }

    @UiHandler("launchButton")
    void onLaunchButtonClick(ClickEvent event) {
        CockpitMode.getInstance().setMode(CockpitMode.Mode.LAUNCH);
    }
}
