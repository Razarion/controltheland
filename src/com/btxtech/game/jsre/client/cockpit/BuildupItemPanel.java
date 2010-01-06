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
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;

/**
 * User: beat
 * Date: 15.11.2009
 * Time: 14:12:18
 */
public class BuildupItemPanel extends HorizontalPanel implements SelectionListener {
    private  HorizontalPanel description;
    private  HorizontalPanel itemsToBuild;
    public static BuildupItemPanel uglyWayToRefreshGui;

    public BuildupItemPanel() {
        setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        description = new HorizontalPanel();
        add(description);
        itemsToBuild = new HorizontalPanel();
        itemsToBuild.setSpacing(10);
        itemsToBuild.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        add(itemsToBuild);

        SelectionHandler.getInstance().addSelectionListener(this);
        uglyWayToRefreshGui = this;
    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItemView selection) {
        description.clear();
        itemsToBuild.clear();
    }

    @Override
    public void onSelectionCleared() {
        description.clear();
        itemsToBuild.clear();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        description.clear();
        itemsToBuild.clear();
        try {
            if (selectedGroup.onlyConstructionVehicle()) {
                setupDescrBox("<b>Select the Factory from the right</b>");
                setupBuildupItemsCV(selectedGroup);

            } else if (selectedGroup.onlyFactories()) {
                setupDescrBox("<b>Select an item from the right</b>");
                setupBuildupItemsFactory(selectedGroup);

            }
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
    }

    private void setupBuildupItemsCV(final Group constructionVehicles) throws NoSuchItemTypeException {
        description.getElement().getStyle().setColor("darkorange");
        itemsToBuild.getElement().getStyle().setColor("darkorange");
        Collection<Integer> itemTypeIDs = constructionVehicles.getFirst().getSyncBaseItem().getBaseItemType().getBuilderType().getAbleToBuild();
        for (Integer itemTypeID : itemTypeIDs) {
            final BaseItemType itemType = (BaseItemType) ClientServices.getInstance().getItemService().getItemType(itemTypeID);
            boolean enabled = ClientItemTypeAccess.getInstance().isAllowed(itemTypeID);
            itemsToBuild.add(setupBuildupBlock(itemType, enabled, new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    new PlaceablePreviewWidget(ImageHandler.getItemTypeImage(itemType), event, constructionVehicles, itemType);
                }
            }));
        }
    }

    private void setupBuildupItemsFactory(final Group factories) throws NoSuchItemTypeException {
        Collection<Integer> itemTypeIDs = factories.getFirst().getSyncBaseItem().getBaseItemType().getFactoryType().getAbleToBuild();
        for (Integer itemTypeID : itemTypeIDs) {
            final BaseItemType itemType = (BaseItemType) ClientServices.getInstance().getItemService().getItemType(itemTypeID);
            boolean enabled = ClientItemTypeAccess.getInstance().isAllowed(itemTypeID);
            itemsToBuild.add(setupBuildupBlock(itemType, enabled, new MouseDownHandler() {
                @Override
                public void onMouseDown(MouseDownEvent event) {
                    ActionHandler.getInstance().build(factories.getItems(), itemType);
                }
            }));
        }
    }

    private Widget setupBuildupBlock(final BaseItemType itemType, boolean enabled, MouseDownHandler mouseDownHandler) {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.getElement().getStyle().setColor("darkorange");
        verticalPanel.setWidth("64px");
        verticalPanel.add(new Label(itemType.getName()));
        Image image = ImageHandler.getItemTypeImage(itemType);
        image.setSize("64px", "64px");
        PushButton button = new PushButton(image);
        button.setSize("64px", "64px");
        button.setEnabled(enabled);
        button.addMouseDownHandler(mouseDownHandler);
        verticalPanel.add(button);
        verticalPanel.add(new Label("$" + itemType.getPrice()));
        return verticalPanel;

    }

    private void setupDescrBox(String descr) {
        HTML label = new HTML(descr);
        label.setWidth("100px");
        label.getElement().getStyle().setColor("darkorange");
        description.add(label);
    }


}
