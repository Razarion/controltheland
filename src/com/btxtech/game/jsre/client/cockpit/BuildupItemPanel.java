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
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.territory.ClientTerritoryService;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;
import com.btxtech.game.jsre.common.tutorial.CockpitWidgetEnum;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 15.11.2009
 * Time: 14:12:18
 */
public class BuildupItemPanel extends AbsolutePanel implements HintWidgetProvider {
    private static final int SCROLL_STEP = 50;
    private static final int HEIGHT = 100;
    private static final int ARROW_L_LEFT = 0;
    private static final int ARROW_L_TOP = 0;
    private static final int ARROW_R_LEFT = 300;
    private static final int ARROW_R_TOP = 0;
    private static final int SCROLL_LEFT = 50;
    private static final int SCROLL_TOP = 0;
    private static final int SCROLL_LENGTH = 250;
    private static final int SCROLL_HEIGHT = 100;
    private ScrollPanel scrollPanel;
    private Map<Integer, Widget> builupItem = new HashMap<Integer, Widget>();

    public BuildupItemPanel() {
        setPixelSize(SelectedItemPanel.WIDTH, HEIGHT);
        ExtendedCustomButton leftArrow = new ExtendedCustomButton("/images/cockpit/leftArrowButton-up.png", "/images/cockpit/leftArrowButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                scrollPanel.setHorizontalScrollPosition(scrollPanel.getHorizontalScrollPosition() - SCROLL_STEP);
            }
        });
        add(leftArrow, ARROW_L_LEFT, ARROW_L_TOP);
        ExtendedCustomButton rightArrow = new ExtendedCustomButton("/images/cockpit/rightArrowButton-up.png", "/images/cockpit/rightArrowButton-down.png", false, new ClickHandler() {
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

    public void display(ClientSyncItem syncBaseItem) {
        try {
            if (syncBaseItem.getSyncBaseItem().hasSyncBuilder()) {
                Group group = new Group();
                group.addItem(syncBaseItem);
                setupBuildupItemsCV(group);
                setVisible(true);
            } else if (syncBaseItem.getSyncBaseItem().hasSyncFactory()) {
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
        builupItem.clear();
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
        HorizontalPanel itemsToBuild = new HorizontalPanel();
        Collection<Integer> itemTypeIDs = constructionVehicles.getFirst().getSyncBaseItem().getBaseItemType().getBuilderType().getAbleToBuild();
        for (Integer itemTypeID : itemTypeIDs) {
            final BaseItemType itemType = (BaseItemType) ClientServices.getInstance().getItemService().getItemType(itemTypeID);
            boolean enabled = ClientItemTypeAccess.getInstance().isAllowed(itemTypeID);
            itemsToBuild.add(setupBuildupBlock(itemType, enabled, new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    new PlaceablePreviewSyncItem(ImageHandler.getItemTypeImage(itemType), event, constructionVehicles, itemType);
                }
            }));
        }
        scrollPanel.setWidget(itemsToBuild);
        scrollPanel.scrollToLeft();
    }

    private void setupBuildupItemsFactory(final Group factories) throws NoSuchItemTypeException {
        HorizontalPanel itemsToBuild = new HorizontalPanel();
        Collection<Integer> itemTypeIDs = factories.getFirst().getSyncBaseItem().getBaseItemType().getFactoryType().getAbleToBuild();
        for (Integer itemTypeID : itemTypeIDs) {
            final BaseItemType itemType = (BaseItemType) ClientServices.getInstance().getItemService().getItemType(itemTypeID);
            boolean enabled = ClientItemTypeAccess.getInstance().isAllowed(itemTypeID) && ClientTerritoryService.getInstance().isAllowed(factories.getFirst().getSyncBaseItem().getPosition(), itemTypeID);
            itemsToBuild.add(setupBuildupBlock(itemType, enabled, new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    ActionHandler.getInstance().build(factories.getItems(), itemType);
                }
            }));
        }
        scrollPanel.setWidget(itemsToBuild);
        scrollPanel.scrollToLeft();
    }

    private Widget setupBuildupBlock(BaseItemType itemType, boolean enabled, MouseDownHandler mouseDownHandler) {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setWidth("64px");
        Image image = ImageHandler.getItemTypeImage(itemType);
        image.setSize("64px", "64px");
        PushButton button = new PushButton(image);
        button.setSize("64px", "64px");
        button.setEnabled(enabled);
        button.addMouseDownHandler(mouseDownHandler);
        verticalPanel.add(button);
        verticalPanel.add(new Label("$" + itemType.getPrice()));
        builupItem.put(itemType.getId(), verticalPanel);
        return verticalPanel;
    }

    @Override
    public Widget getHintWidget(CockpitSpeechBubbleHintConfig config) throws HintWidgetException {
        if (config.getCockpitWidgetEnum() != CockpitWidgetEnum.BUILDUP_ITEM) {
            throw new HintWidgetException(this + " Only BUILDUP_ITEM supported", config);
        }
        if (!isVisible()) {
            throw new HintWidgetException(this + " BuildupItemPanel not visible", config);
        }
        Widget widget = builupItem.get(config.getBaseItemTypeId());
        if (widget != null) {
            return widget;
        } else {
            throw new HintWidgetException(this + " no such item type id: " + config.getBaseItemTypeId(), config);
        }
    }
}
