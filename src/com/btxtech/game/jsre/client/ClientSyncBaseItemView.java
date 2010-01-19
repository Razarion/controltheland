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
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.effects.AttackEffectHandler;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * User: beat
 * Date: 05.12.2009
 * Time: 15:38:10
 */
public class ClientSyncBaseItemView extends ClientSyncItemView {
    private SyncBaseItem syncBaseItem;
    private ProgressBar healthBar;
    private ProgressBar buildProgressBar;

    public ClientSyncBaseItemView(SyncBaseItem syncBaseItem) {
        super(syncBaseItem);
        this.syncBaseItem = syncBaseItem;
        setupAbilities();
        setZIndex();
        CursorHandler.getInstance().handleCursorOnNewItems(this);
    }

    private void setupAbilities() {
        setupMarker();
        setupHealthBar();
        if (syncBaseItem.hasSyncFactory()) {
            setupFactoryProgressBar();
        }
    }

    private void setupHealthBar() {
        healthBar = new ProgressBar(0.0, syncBaseItem.getBaseItemType().getHealth());
        healthBar.setTextVisible(false);
        healthBar.getElement().getStyle().setZIndex(2);
        healthBar.getElement().getStyle().setHeight(3, Style.Unit.PX);
        healthBar.getElement().getStyle().setFontSize(0, Style.Unit.PX);
        add(healthBar);
        setWidgetPosition(healthBar, 0, syncBaseItem.getItemType().getHeight() - 3);
    }

    private void setupMarker() {
        SimplePanel marker = new SimplePanel();
        add(marker);
        marker.setPixelSize(10, 10);
        marker.getElement().getStyle().setZIndex(2);
        setWidgetPosition(marker, 0, syncBaseItem.getItemType().getHeight() - 13);
        DOM.setStyleAttribute(marker.getElement(), "background", syncBaseItem.getBase().getHtmlColor());
    }

    private void setupFactoryProgressBar() {
        buildProgressBar = new ProgressBar(0.0, 0.0);
        buildProgressBar.setTextVisible(false);
        buildProgressBar.setStyleName("gwt-DeviceBuildBar-shell");
        buildProgressBar.getElement().getStyle().setZIndex(2);
        buildProgressBar.getElement().getStyle().setHeight(4, Style.Unit.PX);
        buildProgressBar.getElement().getStyle().setFontSize(0, Style.Unit.PX);
        add(buildProgressBar);
        setWidgetPosition(buildProgressBar, 0, 2);
    }

    protected void setHealth() {
        if (!syncBaseItem.isReady() && !GwtCommon.isIe6()) {
            double factor = (double) syncBaseItem.getHealth() / (double) syncBaseItem.getBaseItemType().getHealth();
            int width = (int) (syncBaseItem.getItemType().getWidth() * factor);
            int height = (int) (syncBaseItem.getItemType().getHeight() * factor);
            getImage().setPixelSize(width, height);
            int imgX = (syncBaseItem.getItemType().getWidth() - width) / 2;
            int imgY = (syncBaseItem.getItemType().getHeight() - height) / 2;
            setWidgetPosition(getImage(), imgX, imgY);
        } else {
            getImage().setPixelSize(syncBaseItem.getItemType().getWidth(), syncBaseItem.getItemType().getHeight());
            setWidgetPosition(getImage(), 0, 0);

        }
        healthBar.setProgress(syncBaseItem.getHealth());
    }

    public void setFactoryProgress() {
        if (buildProgressBar == null) {
            throw new IllegalStateException(syncBaseItem + "does not have a buildProgressBar");
        }

        if (syncBaseItem.getSyncFactory().getToBeBuiltType() != null) {
            buildProgressBar.setMaxProgress(syncBaseItem.getSyncFactory().getToBeBuiltType().getHealth());
            buildProgressBar.setProgress(syncBaseItem.getSyncFactory().getBuildupProgress());
        } else {
            buildProgressBar.setProgress(0);
        }
    }

    @Override
    public void onItemChanged(Change change, SyncItem syncItem) {
        switch (change) {
            case ANGEL:
                setupImage();
                break;
            case FACTORY_PROGRESS:
                setFactoryProgress();
                break;
            case HEALTH:
                setHealth();
                break;
            case POSITION:
                setPosition();
                break;
            case ON_ATTACK:
                AttackEffectHandler.getInstance().onAttack(this);
                break;
        }
    }

    @Override
    public void update() {
        setupImage();
        setHealth();
        setPosition();
        if (syncBaseItem.hasSyncFactory()) {
            setFactoryProgress();
        }
    }

    private void setZIndex() {
        if (syncBaseItem.hasSyncMovable()) {
            getElement().getStyle().setZIndex(Constants.Z_INDEX_MOVABLE);
        } else {
            getElement().getStyle().setZIndex(Constants.Z_INDEX_BUILDING);
        }
    }

    public boolean isMyOwnProperty() {
        return ClientBase.getInstance().isMyOwnProperty(syncBaseItem);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (isMyOwnProperty()) {
            Group group = new Group();
            group.addItem(this);
            SelectionHandler.getInstance().setItemGroupSelected(group);
            ClientUserTracker.getInstance().clickOwnItem(syncBaseItem);
        } else {
            SelectionHandler.getInstance().setTargetSelected(this, event);
            ClientUserTracker.getInstance().clickEnemyItem(syncBaseItem);
        }
        // Just to prevent image dragging
        event.stopPropagation();
        event.preventDefault();
    }

    public SyncBaseItem getSyncBaseItem() {
        return syncBaseItem;
    }
}
