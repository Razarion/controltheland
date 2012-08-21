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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat Date: 15.11.2009 Time: 14:12:18
 */
public class BuildupItemPanel extends Composite {
    private static BuildupItemPanelUiBinder uiBinder = GWT.create(BuildupItemPanelUiBinder.class);
    private static final int SCROLL_STEP = 20;
    @UiField
    PushButton scrollLeftButton;
    @UiField
    PushButton scrollRightButton;
    @UiField
    ScrollPanel scrollPanel;

    private Map<Integer, BuildupItem> buildupItem = new HashMap<Integer, BuildupItem>();

    interface BuildupItemPanelUiBinder extends UiBinder<Widget, BuildupItemPanel> {
    }

    public BuildupItemPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        scrollPanel.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
    }

    @UiHandler("scrollLeftButton")
    void onScrollLeftButtonClick(ClickEvent event) {
        scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() - SCROLL_STEP);
   }

    @UiHandler("scrollRightButton")
    void onScrollRightButtonClick(ClickEvent event) {
        scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() + SCROLL_STEP);
   }

    public void display(SyncBaseItem syncBaseItem) {
        try {
            if (syncBaseItem.hasSyncBuilder()) {
                Group group = new Group();
                group.addItem(syncBaseItem);
                setupBuildupItemsCV(group);
            } else if (syncBaseItem.hasSyncFactory()) {
                Group group = new Group();
                group.addItem(syncBaseItem);
                setupBuildupItemsFactory(group);
            }
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
    }

    public void display(Group selectedGroup) {
        try {
            if (selectedGroup.onlyConstructionVehicle()) {
                setupBuildupItemsCV(selectedGroup);
            } else if (selectedGroup.onlyFactories()) {
                setupBuildupItemsFactory(selectedGroup);
            }
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
    }

    private void setupBuildupItemsCV(final Group constructionVehicles) throws NoSuchItemTypeException {
        buildupItem.clear();
        HorizontalPanel itemsToBuild = new HorizontalPanel();
        Collection<Integer> itemTypeIDs = constructionVehicles.getFirst().getBaseItemType().getBuilderType().getAbleToBuild();
        for (Integer itemTypeID : itemTypeIDs) {
            final BaseItemType itemType = (BaseItemType) ClientServices.getInstance().getItemService().getItemType(itemTypeID);
            itemsToBuild.add(setupBuildupBlock(itemType, new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    CockpitMode.getInstance().setToBeBuildPlacer(new ToBeBuildPlacer(itemType, constructionVehicles));
                }
            }));
        }
        scrollPanel.setWidget(itemsToBuild);
        scrollPanel.scrollToLeft();
    }

    private void setupBuildupItemsFactory(final Group factories) throws NoSuchItemTypeException {
        buildupItem.clear();
        HorizontalPanel itemsToBuild = new HorizontalPanel();
        Collection<Integer> itemTypeIDs = factories.getFirst().getBaseItemType().getFactoryType().getAbleToBuild();
        for (Integer itemTypeID : itemTypeIDs) {
            final BaseItemType itemType = (BaseItemType) ClientServices.getInstance().getItemService().getItemType(itemTypeID);
            itemsToBuild.add(setupBuildupBlock(itemType, new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    try {
                        ActionHandler.getInstance().fabricate(factories.getItems(), itemType);
                    } catch (NoSuchItemTypeException e) {
                        GwtCommon.handleException(e);
                    }
                }
            }));
        }
        scrollPanel.setWidget(itemsToBuild);
        scrollPanel.scrollToLeft();
    }

    private Widget setupBuildupBlock(BaseItemType itemType, MouseDownHandler mouseDownHandler) {
        BuildupItem buildupItem = new BuildupItem(itemType, mouseDownHandler);
        this.buildupItem.put(itemType.getId(), buildupItem);
        return buildupItem;
    }

    public void onMoneyChanged(double accountBalance) {
        for (BuildupItem buildupItem : this.buildupItem.values()) {
            buildupItem.onMoneyChanged(accountBalance);
        }
    }

    public void onStateChanged() {
        for (BuildupItem buildupItem : this.buildupItem.values()) {
            buildupItem.onStateChanged();
        }
    }
}
