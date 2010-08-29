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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemContainer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: May 20, 2009
 * Time: 7:50:02 PM
 */
public class CockpitPanel extends TopMapPanel implements SelectionListener {
    private VerticalPanel detailPanel;
    private BuildupItemPanel buildupItemPanel;
    private boolean unloadMode = false;

    @Override
    protected Widget createBody() {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        horizontalPanel.setSpacing(10);
        // SelectionPanel
        horizontalPanel.add(new SelectionPanel());
        // Detail Panel
        detailPanel = new VerticalPanel();
        horizontalPanel.add(detailPanel);
        // Build up panel
        buildupItemPanel = new BuildupItemPanel();
        horizontalPanel.add(buildupItemPanel);

        SelectionHandler.getInstance().addSelectionListener(this);
        setVisible(false);
        return horizontalPanel;
    }

    public void onSelectionCleared() {
        detailPanel.clear();
        setVisible(false);
    }

    public void onTargetSelectionChanged(ClientSyncItem selection) {
        detailPanel.clear();
        if (selection.isSyncBaseItem()) {
            StringBuilder builder = new StringBuilder();
            builder.append("This item belongs to <b>");
            builder.append(ClientBase.getInstance().getBaseName(selection.getSyncBaseItem().getBase()));
            builder.append("</b>. This is your enemy!<br/></b>Attack it!</b>");
            if (Game.isDebug()) {
                builder.append("<br/>ID: ");
                builder.append(selection.getSyncItem().getId());
            }
            setupDescrBox(builder.toString(), null, null);
        } else if (selection.isSyncResourceItem()) {
            if (Game.isDebug()) {
                setupDescrBox(selection.getSyncItem().getItemType().getDescription() + "<br/>ID: " + selection.getSyncItem().getId(), null, null);
            } else {
                setupDescrBox(selection.getSyncItem().getItemType().getDescription(), null, null);
            }
        } else {
            throw new IllegalArgumentException(this + " can not set details for: " + selection);
        }
        setVisible(true);
    }

    public void onOwnSelectionChanged(Group selectedGroup) {
        detailPanel.clear();
        clearUnloadMode();
        if (selectedGroup.count() == 1) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) selectedGroup.getFirst().getSyncItem();
            SyncBaseItem upgradeable = null;
            SyncItemContainer syncItemContainer = null;
            if (syncBaseItem.isUpgradeable()) {
                upgradeable = syncBaseItem;
            }
            if (syncBaseItem.hasSyncItemContainer()) {
                syncItemContainer = syncBaseItem.getSyncItemContainer();
            }
            String descr = syncBaseItem.getItemType().getDescription();
            if (Game.isDebug()) {
                descr += "<br/>ID: " + syncBaseItem.getId();
            }
            setupDescrBox(descr, upgradeable, syncItemContainer);
        } else if (selectedGroup.canAttack()) {
            setupDescrBox(selectedGroup.getFirst().getSyncItem().getItemType().getDescription(), null, null);
        } else if (selectedGroup.canCollect()) {
            setupDescrBox(selectedGroup.getFirst().getSyncItem().getItemType().getDescription(), null, null);
        }
        setVisible(true);
    }

    private void setupDescrBox(String descr, final SyncBaseItem upgradeable, SyncItemContainer syncItemContainer) {
        HTML label = new HTML(descr);
        label.setWidth("100px");
        label.getElement().getStyle().setColor("darkorange");
        detailPanel.add(label);
        if (upgradeable != null) {
            detailPanel.add(new HTML("<br>"));
            Button button = new Button("Upgrade");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ActionHandler.getInstance().upgrade(upgradeable);
                }
            });
            button.setEnabled(ClientServices.getInstance().getItemTypeAccess().isAllowed(upgradeable.getBaseItemType().getUpgradeable()));
            detailPanel.add(button);
        }

        if (syncItemContainer != null) {
            Label containedItems = new Label("Contained item: " + syncItemContainer.getContainedItems().size());
            containedItems.getElement().getStyle().setColor("darkorange");
            detailPanel.add(containedItems);

            detailPanel.add(new HTML("<br>"));
            Button button = new Button("Unload");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setUnloadMode();
                }
            });
            detailPanel.add(button);
        }
    }

    public BuildupItemPanel getBuildupItemPanel() {
        return buildupItemPanel;
    }

    public boolean isUnloadMode() {
        return unloadMode;
    }

    private void setUnloadMode() {
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

}