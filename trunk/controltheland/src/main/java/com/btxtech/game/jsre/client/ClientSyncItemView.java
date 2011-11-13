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

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.cockpit.CursorHandler;
import com.btxtech.game.jsre.client.cockpit.CursorItemState;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.effects.AttackEffectHandler;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.SpeechBubbleHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemListener;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
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
public class ClientSyncItemView extends AbsolutePanel implements MouseDownHandler, MouseOverHandler, MouseOutHandler {
    private Image image;
    private ClientSyncItem clientSyncItem;
    private CursorItemState cursorItemState;
    private ExtendedProgressBar healthBar;
    private ProgressBar factorizeBar;
    private ProgressBar projectileBar;
    private SimplePanel marker;

    public ClientSyncItemView() {
        sinkEvents(Event.ONMOUSEMOVE);
        addDomHandler(this, MouseDownEvent.getType());
        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
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
            this.clientSyncItem.setClientSyncItemListener(null);
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
            this.clientSyncItem.setClientSyncItemListener(null);
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
        } else if (clientSyncItem.isSyncProjectileItem()) {
            if (oldClientSyncItem == null || !oldClientSyncItem.isSyncProjectileItem()) {
                pupateToSyncProjectileItem();
            }
        } else {
            throw new IllegalArgumentException(this + " transformTo(): SyncItem not supported: " + clientSyncItem);
        }
        this.clientSyncItem.setClientSyncItemListener(this);
        displayState();
    }

    private void displayState() {
        setPosition();
        if (clientSyncItem.isSyncBaseItem()) {
            setFactorizeProgress();
            setProjectileProgress();
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
        if (factorizeBar != null) {
            factorizeBar.setVisible(false);
        }
        if (projectileBar != null) {
            projectileBar.setVisible(false);
        }
        if (marker != null) {
            marker.setVisible(false);
        }
    }

    private void pupateToSyncProjectileItem() {
        pupateCommon();
        cursorItemState = new CursorItemState();
        getElement().getStyle().setZIndex(Constants.Z_INDEX_PROJECTILE);
        if (healthBar != null) {
            healthBar.setVisible(false);
        }
        if (factorizeBar != null) {
            factorizeBar.setVisible(false);
        }
        if (projectileBar != null) {
            projectileBar.setVisible(false);
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
        if (clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
            setupFactorizeBar();
        } else if (factorizeBar != null) {
            factorizeBar.setVisible(false);
        }
        if (clientSyncItem.getSyncBaseItem().hasSyncLauncher()) {
            setupProjectileBar();
        } else if (projectileBar != null) {
            projectileBar.setVisible(false);
        }
        // Cursor
        cursorItemState = new CursorItemState();
        if (clientSyncItem.isMyOwnProperty()) {
            if (clientSyncItem.getSyncBaseItem().hasSyncItemContainer()) {
                cursorItemState.setLoadTarget();
            }
            cursorItemState.setFinalizeBuild(!clientSyncItem.getSyncBaseItem().isReady());
        } else {
            cursorItemState.setAttackTarget();
        }
    }

    private void setupSize() {
        setPixelSize(clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageWidth(),
                clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageHeight());
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
        setupImageSizeAndPos();
    }

    public void setPosition() {
        if (!clientSyncItem.getSyncItem().getSyncItemArea().hasPosition()) {
            return;
        }
        int x = toRelativePosition(clientSyncItem.getSyncItem().getSyncItemArea().getPosition().getX(),
                TerrainView.getInstance().getViewOriginLeft(),
                clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageWidth());
        int y = toRelativePosition(clientSyncItem.getSyncItem().getSyncItemArea().getPosition().getY(),
                TerrainView.getInstance().getViewOriginTop(),
                clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageHeight());
        MapWindow.getAbsolutePanel().setWidgetPosition(this, x, y);
    }

    private int toRelativePosition(int pos, int viewOrigin, int itemImageSize) {
        return pos - viewOrigin - itemImageSize / 2;
    }

    public void onModelChange(SyncItemListener.Change change) {
        switch (change) {
            case BUILD:
                cursorItemState.setFinalizeBuild(!clientSyncItem.getSyncBaseItem().isReady());
                setupImageSizeAndPos();
                break;
            case ANGEL:
                setupImage();
                break;
            case FACTORY_PROGRESS:
                setFactorizeProgress();
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
                setFactorizeProgress();
                setProjectileProgress();
                setupImage();
                SelectionHandler.getInstance().refresh();
                ItemContainer.getInstance().handleSpecial(clientSyncItem);
                break;
            case LAUNCHER_PROGRESS:
                setProjectileProgress();
                break;
            case UPGRADE_PROGRESS_CHANGED:
                setFactorizeProgress();
                setProjectileProgress();
                break;
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        if (SelectionHandler.getInstance().isSellMode()) {
            if (clientSyncItem.isMyOwnProperty()) {
                Connection.getInstance().sendSellItem(clientSyncItem.getSyncItem());
                SelectionHandler.getInstance().setSellMode(false);
            }
        } else if (Cockpit.getInstance().getCockpitMode().isLaunchMode() && !clientSyncItem.isMyOwnProperty()) {
            int x = mouseDownEvent.getRelativeX(TerrainView.getInstance().getCanvas().getElement()) + TerrainView.getInstance().getViewOriginLeft();
            int y = mouseDownEvent.getRelativeY(TerrainView.getInstance().getCanvas().getElement()) + TerrainView.getInstance().getViewOriginTop();
            ActionHandler.getInstance().executeLaunchCommand(x, y);
        } else {
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
        }
        GwtCommon.preventDefault(mouseDownEvent);
    }

    private void setupHealthBar() {
        if (healthBar == null) {
            healthBar = new ExtendedProgressBar(1.0, clientSyncItem.getSyncBaseItem().getBaseItemType().getHealth());
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
        setWidgetPosition(healthBar, 0, clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageHeight() - 3);
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
        setWidgetPosition(marker, 0, clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageHeight() - 13);
    }


    private void setupImageSizeAndPos() {
        if (clientSyncItem.isSyncBaseItem() && !clientSyncItem.getSyncBaseItem().isReady() && !GwtCommon.isIe6()) {
            SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
            int width = (int) (syncBaseItem.getItemType().getBoundingBox().getImageWidth() * syncBaseItem.getBuildup());
            if (width < 1) {
                width = 1;
            }
            int height = (int) (syncBaseItem.getItemType().getBoundingBox().getImageHeight() * syncBaseItem.getBuildup());
            if (height < 1) {
                height = 1;
            }
            image.setPixelSize(width, height);
            int imgX = (syncBaseItem.getItemType().getBoundingBox().getImageWidth() - width) / 2;
            int imgY = (syncBaseItem.getItemType().getBoundingBox().getImageHeight() - height) / 2;
            setWidgetPosition(image, imgX, imgY);
        } else {
            image.setPixelSize(clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageWidth(),
                    clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageHeight());
            setWidgetPosition(image, 0, 0);
        }
    }


    protected void setHealth() {
        healthBar.setProgress(clientSyncItem.getSyncBaseItem().getHealth());
    }

    private void setupFactorizeBar() {
        if (factorizeBar == null) {
            factorizeBar = new ProgressBar(0.0, 0.0);
            factorizeBar.setTextVisible(false);
            factorizeBar.setStyleName("gwt-DeviceBuildBar-shell");
            factorizeBar.getElement().getStyle().setZIndex(2);
            factorizeBar.getElement().getStyle().setHeight(4, Style.Unit.PX);
            factorizeBar.getElement().getStyle().setFontSize(0, Style.Unit.PX);
            add(factorizeBar);
        } else {
            factorizeBar.setVisible(true);
        }
        setWidgetPosition(factorizeBar, 0, 2);
    }

    private void setFactorizeProgress() {
        if (clientSyncItem.getSyncBaseItem().hasSyncFactory()) {
            factorizeBar.setMaxProgress(1.0);
            factorizeBar.setProgress(clientSyncItem.getSyncBaseItem().getSyncFactory().getBuildupProgress());
        } else if (clientSyncItem.getSyncBaseItem().isUpgrading()) {
            factorizeBar.setMaxProgress(clientSyncItem.getSyncBaseItem().getFullUpgradeProgress());
            factorizeBar.setProgress(clientSyncItem.getSyncBaseItem().getUpgradeProgress());
        }
    }

    private void setupProjectileBar() {
        if (projectileBar == null) {
            projectileBar = new ProgressBar(0.0, 0.0);
            projectileBar.setTextVisible(false);
            projectileBar.setStyleName("gwt-ProjectileBar-shell");
            projectileBar.getElement().getStyle().setZIndex(2);
            projectileBar.getElement().getStyle().setHeight(4, Style.Unit.PX);
            projectileBar.getElement().getStyle().setFontSize(0, Style.Unit.PX);
            add(projectileBar);
        } else {
            projectileBar.setVisible(true);
        }
        setWidgetPosition(projectileBar, 0, 2);
    }

    private void setProjectileProgress() {
        if (clientSyncItem.getSyncBaseItem().hasSyncLauncher()) {
            projectileBar.setMaxProgress(1.0);
            projectileBar.setProgress(clientSyncItem.getSyncBaseItem().getSyncLauncher().getBuildup());
        }
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        CursorHandler.getInstance().setItemCursor(this, cursorItemState);
        SpeechBubbleHandler.getInstance().show(getClientSyncItem().getSyncItem());
        GwtCommon.preventDefault(event);
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        SpeechBubbleHandler.getInstance().hide();
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
                setFactorizeProgress();
            }
            if (clientSyncItem.getSyncBaseItem().hasSyncLauncher()) {
                setProjectileProgress();
            }
            setVisible(!clientSyncItem.getSyncBaseItem().isContainedIn());
        }
    }

    public void setSelected(boolean selected) {
        if (clientSyncItem.isSyncBaseItem()) {
            if (selected) {
                healthBar.setColor("#00FF00", "#FF0000");
            } else {
                healthBar.setColor("#006600", "#660000");
            }
        }
    }
}