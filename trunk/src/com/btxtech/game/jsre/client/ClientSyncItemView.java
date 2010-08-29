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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.cockpit.CursorItemState;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.effects.AttackEffectHandler;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * User: beat
 * Date: May 20, 2009
 * Time: 2:48:36 PM
 */
public class ClientSyncItemView extends AbsolutePanel implements MouseDownHandler, MouseOverHandler, SyncItemListener {
    private Image image;
    private ClientSyncItem clientSyncItem;
    private CursorItemState cursorItemState;
    private ProgressBar healthBar;
    private ProgressBar progressBar;
    private SimplePanel marker;


    public ClientSyncItemView() {
        sinkEvents(Event.ONMOUSEMOVE);
        addDomHandler(this, MouseDownEvent.getType());
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GwtCommon.preventDefault(event);
            }
        }, MouseUpEvent.getType());
    }

    public void transform(ClientSyncItem clientSyncItem) {
        if (clientSyncItem == null && this.clientSyncItem != null) {
            MapWindow.getAbsolutePanel().remove(this);
            this.clientSyncItem.getSyncItem().removeSyncItemListener(this);
            this.clientSyncItem = null;
        } else if (clientSyncItem != null && this.clientSyncItem == null) {
            MapWindow.getAbsolutePanel().add(this, 0, 0);
            transformTo(clientSyncItem);
        } else if (clientSyncItem != null) {
            transformTo(clientSyncItem);
        }
    }

    private void transformTo(ClientSyncItem clientSyncItem) {
        if (clientSyncItem.equals(this.clientSyncItem)) {
            return;
        }
        if (this.clientSyncItem != null) {
            this.clientSyncItem.getSyncItem().removeSyncItemListener(this);
        }
        ClientSyncItem oldClientSyncItem = this.clientSyncItem;
        this.clientSyncItem = clientSyncItem;
        if (clientSyncItem.isSyncBaseItem()) {
            if (oldClientSyncItem == null || !oldClientSyncItem.isSyncBaseItem()) {
                pupateToSyncBaseItem();
            }
        } else if (clientSyncItem.isSyncResourceItem()) {
            if (oldClientSyncItem == null || !oldClientSyncItem.isSyncResourceItem()) {
                pupateToSyncResourceItem();
            }
        } else {
            throw new IllegalArgumentException(this + " transformTo(): SyncItem not supported: " + clientSyncItem);
        }
        this.clientSyncItem.getSyncItem().addSyncItemListener(this);
        displayState();
    }

    private void displayState() {
        setPosition();
        if (clientSyncItem.isSyncBaseItem()) {
            setProgress();
        }
        setSelected(clientSyncItem.isSelected());
    }

    private void pupateCommon() {
        setupSize();
        setupImage();
    }

    private void pupateToSyncResourceItem() {
        pupateCommon();
        getElement().getStyle().setZIndex(Constants.Z_INDEX_MONEY);
        cursorItemState = new CursorItemState();
        cursorItemState.setCollectTarget();
        if (healthBar != null) {
            healthBar.setVisible(false);
        }
        if (progressBar != null) {
            progressBar.setVisible(false);
        }
        if (marker != null) {
            marker.setVisible(false);
        }
    }

    private void pupateToSyncBaseItem() {
        pupateCommon();
        // Z Index
        if (clientSyncItem.getSyncBaseItem().hasSyncMovable()) {
            getElement().getStyle().setZIndex(Constants.Z_INDEX_MOVABLE);
        } else {
            getElement().getStyle().setZIndex(Constants.Z_INDEX_BUILDING);
        }
        // Abilities
        setupMarker();
        setupHealthBar();
        setupProgressBar();
        // Cursor
        cursorItemState = new CursorItemState();
        if (clientSyncItem.isMyOwnProperty()) {
            if (clientSyncItem.getSyncBaseItem().hasSyncItemContainer()) {
                cursorItemState.setLoadTarget();
            }
        } else {
            cursorItemState.setAttackTarget();
        }
    }

    private void setupSize() {
        setPixelSize(clientSyncItem.getSyncItem().getItemType().getWidth(), clientSyncItem.getSyncItem().getItemType().getHeight());
    }

    public void setupImage() {
        if (image != null) {
            remove(image);
        }
        image = ImageHandler.getItemTypeImage(clientSyncItem.getSyncItem());
        image.addMouseDownHandler(this);
        image.sinkEvents(Event.ONMOUSEMOVE);
        image.getElement().getStyle().setZIndex(1);
        add(image);
        setWidgetPosition(image, 0, 0);
    }

    public void setPosition() {
        if (clientSyncItem.getSyncItem().getPosition() == null) {
            return;
        }
        int x = toRelativePosition(clientSyncItem.getSyncItem().getPosition().getX(),
                TerrainView.getInstance().getViewOriginLeft(),
                clientSyncItem.getSyncItem().getItemType().getWidth());
        int y = toRelativePosition(clientSyncItem.getSyncItem().getPosition().getY(),
                TerrainView.getInstance().getViewOriginTop(),
                clientSyncItem.getSyncItem().getItemType().getHeight());
        MapWindow.getAbsolutePanel().setWidgetPosition(this, x, y);
    }

    private int toRelativePosition(int pos, int viewOrigin, int itemSize) {
        return pos - viewOrigin - itemSize / 2;
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        switch (change) {
            case BUILD:
                // TODO PlayerSimulation.getInstance().onItemBuilt(this);
                Simulation.getInstance().onItemBuilt(clientSyncItem.getSyncBaseItem());
                break;
            case ANGEL:
                setupImage();
                break;
            case FACTORY_PROGRESS:
                setProgress();
                break;
            case HEALTH:
                setHealth();
                break;
            case POSITION:
                setPosition();
                break;
            case ON_ATTACK:
                AttackEffectHandler.getInstance().onAttack(clientSyncItem);
                break;
            case ITEM_TYPE_CHANGED:
                setupSize();
                setupMarkerPos();
                setupHealthBarPos();
                setProgress();
                setupImage();
                SelectionHandler.getInstance().refresh();
                ItemContainer.getInstance().handleSpecial(clientSyncItem);
                break;
            case UPGRADE_PROGRESS_CHANGED:
                setProgress();
                break;
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        if (clientSyncItem.isSyncResourceItem()) {
            SelectionHandler.getInstance().setTargetSelected(this, mouseDownEvent);
        } else if (clientSyncItem.isSyncBaseItem()) {
            if (clientSyncItem.isMyOwnProperty()) {
                Group group = new Group();
                group.addItem(clientSyncItem);
                SelectionHandler.getInstance().setItemGroupSelected(group);
            } else {
                SelectionHandler.getInstance().setTargetSelected(this, mouseDownEvent);
            }
        } else {
            throw new IllegalArgumentException(this + " onMouseDown: SyncItem not supported: " + clientSyncItem);
        }
        GwtCommon.preventDefault(mouseDownEvent);
    }

    private void setupHealthBar() {
        if (healthBar == null) {
            healthBar = new ProgressBar(0.0, clientSyncItem.getSyncBaseItem().getBaseItemType().getHealth());
            healthBar.setTextVisible(false);
            healthBar.getElement().getStyle().setZIndex(2);
            healthBar.getElement().getStyle().setHeight(3, Style.Unit.PX);
            healthBar.getElement().getStyle().setFontSize(0, Style.Unit.PX);
            add(healthBar);
        } else {
            healthBar.setVisible(true);
            healthBar.setMaxProgress(clientSyncItem.getSyncBaseItem().getBaseItemType().getHealth());
        }
        setupHealthBarPos();
        setHealth();
    }

    private void setupHealthBarPos() {
        setWidgetPosition(healthBar, 0, clientSyncItem.getSyncItem().getItemType().getHeight() - 3);
    }

    private void setupMarker() {
        if (marker == null) {
            marker = new SimplePanel();
            add(marker);
            marker.setPixelSize(10, 10);
            marker.getElement().getStyle().setZIndex(2);
        } else {
            marker.setVisible(true);
        }
        setupMarkerPos();
        updateMarker();
    }

    public void updateMarker() {
        if (clientSyncItem.isSyncBaseItem() && marker != null) {
            DOM.setStyleAttribute(marker.getElement(), "background", ClientBase.getInstance().getBaseHtmlColor(clientSyncItem.getSyncBaseItem().getBase()));
        }
    }

    private void setupMarkerPos() {
        setWidgetPosition(marker, 0, clientSyncItem.getSyncItem().getItemType().getHeight() - 13);
    }

    protected void setHealth() {
        if (!clientSyncItem.getSyncBaseItem().isReady() && !GwtCommon.isIe6()) {
            double factor = clientSyncItem.getSyncBaseItem().getHealth() / (double) clientSyncItem.getSyncBaseItem().getBaseItemType().getHealth();
            int width = (int) (clientSyncItem.getSyncBaseItem().getItemType().getWidth() * factor);
            int height = (int) (clientSyncItem.getSyncBaseItem().getItemType().getHeight() * factor);
            image.setPixelSize(width, height);
            int imgX = (clientSyncItem.getSyncBaseItem().getItemType().getWidth() - width) / 2;
            int imgY = (clientSyncItem.getSyncBaseItem().getItemType().getHeight() - height) / 2;
            setWidgetPosition(image, imgX, imgY);
        } else {
            image.setPixelSize(clientSyncItem.getSyncBaseItem().getItemType().getWidth(), clientSyncItem.getSyncBaseItem().getItemType().getHeight());
            setWidgetPosition(image, 0, 0);
        }
        healthBar.setProgress(clientSyncItem.getSyncBaseItem().getHealth());
    }

    private void setupProgressBar() {
        if (progressBar == null) {
            progressBar = new ProgressBar(0.0, 0.0);
            progressBar.setTextVisible(false);
            progressBar.setStyleName("gwt-DeviceBuildBar-shell");
            progressBar.getElement().getStyle().setZIndex(2);
            progressBar.getElement().getStyle().setHeight(4, Style.Unit.PX);
            progressBar.getElement().getStyle().setFontSize(0, Style.Unit.PX);
            add(progressBar);
        } else {
            progressBar.setVisible(true);
        }
        setWidgetPosition(progressBar, 0, 2);
    }

    private void setProgress() {
        if (clientSyncItem.getSyncBaseItem().hasSyncFactory() && clientSyncItem.getSyncBaseItem().getSyncFactory().getToBeBuiltType() != null) {
            progressBar.setMaxProgress(clientSyncItem.getSyncBaseItem().getSyncFactory().getToBeBuiltType().getHealth());
            progressBar.setProgress(clientSyncItem.getSyncBaseItem().getSyncFactory().getBuildupProgress());
        } else if (clientSyncItem.getSyncBaseItem().isUpgrading()) {
            progressBar.setMaxProgress(clientSyncItem.getSyncBaseItem().getFullUpgradeProgress());
            progressBar.setProgress(clientSyncItem.getSyncBaseItem().getUpgradeProgress());
        } else {
            progressBar.setProgress(0);
        }
    }

    public void onMouseOver(MouseOverEvent event) {
        CursorHandler.getInstance().setItemCursor(this, cursorItemState);
        GwtCommon.preventDefault(event);
    }

    public ClientSyncItem getClientSyncItem() {
        return clientSyncItem;
    }

    public void update() {
        setupImage();
        setPosition();

        if (clientSyncItem.isSyncBaseItem()) {
            setHealth();
            setPosition();
            if (clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
                setProgress();
            }
            setVisible(!clientSyncItem.getSyncBaseItem().isContainedIn());
        }
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setStyleName("gwt-marked");
        } else {
            setStyleName("gwt-unmarked");
        }
    }
}