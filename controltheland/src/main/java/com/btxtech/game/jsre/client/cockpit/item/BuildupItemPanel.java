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

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 15.11.2009
 * Time: 14:12:18
 */
public class BuildupItemPanel extends AbsolutePanel {
    private static final String TOOL_TIP_SCROLL_LEFT = "Scroll left";
    private static final String TOOL_TIP_SCROLL_RIGHT = "Scroll right";

    private static final int WIDTH = 355;
    private static final int HEIGHT = 120;
    private static final int SCROLL_STEP = 50;
    private static final int DESCRIPTION_LEFT = 0;
    private static final int DESCRIPTION_TOP = 0;
    private static final int ARROW_L_LEFT = 0;
    private static final int ARROW_L_TOP = 20;
    private static final int ARROW_R_LEFT = 300;
    private static final int ARROW_R_TOP = 20;
    private static final int SCROLL_LEFT = 50;
    private static final int SCROLL_TOP = 30;
    private static final int SCROLL_LENGTH = 250;
    private static final int SCROLL_HEIGHT = 70;
    private ScrollPanel scrollPanel;
    private Map<Integer, BuildupItem> buildupItem = new HashMap<Integer, BuildupItem>();
    private BuildListener buildListener;

    public interface BuildListener {
        void onBuild();
    }

    public BuildupItemPanel(BuildListener buildListener) {
        this.buildListener = buildListener;
        setPixelSize(WIDTH, HEIGHT);
        add(new Label("Build units or structures"), DESCRIPTION_LEFT, DESCRIPTION_TOP);
        ExtendedCustomButton leftArrow = new ExtendedCustomButton("leftArrowButton", false, TOOL_TIP_SCROLL_LEFT, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() - SCROLL_STEP);
            }
        });
        add(leftArrow, ARROW_L_LEFT, ARROW_L_TOP);
        ExtendedCustomButton rightArrow = new ExtendedCustomButton("rightArrowButton", false, TOOL_TIP_SCROLL_RIGHT, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() + SCROLL_STEP);
            }
        });
        add(rightArrow, ARROW_R_LEFT, ARROW_R_TOP);
        scrollPanel = new ScrollPanel();
        scrollPanel.setPixelSize(SCROLL_LENGTH, SCROLL_HEIGHT);
        scrollPanel.getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        add(scrollPanel, SCROLL_LEFT, SCROLL_TOP);
    }

    public void display(SyncBaseItem syncBaseItem) {
        try {
            if (syncBaseItem.hasSyncBuilder()) {
                Group group = new Group();
                group.addItem(syncBaseItem);
                setupBuildupItemsCV(group);
                setVisible(true);
            } else if (syncBaseItem.hasSyncFactory()) {
                Group group = new Group();
                group.addItem(syncBaseItem);
                setupBuildupItemsFactory(group);
                setVisible(true);
            } else {
                setVisible(false);
            }
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
    }

    public void display(Group selectedGroup) {
        try {
            if (selectedGroup.onlyConstructionVehicle()) {
                setupBuildupItemsCV(selectedGroup);
                setVisible(true);
            } else if (selectedGroup.onlyFactories()) {
                setupBuildupItemsFactory(selectedGroup);
                setVisible(true);
            } else {
                setVisible(false);
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
                    if (buildListener != null) {
                        buildListener.onBuild();
                    }
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
                    if (buildListener != null) {
                        buildListener.onBuild();
                    }
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
