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

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.tutorial.CockpitSpeechBubbleHintConfig;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: May 20, 2009
 * Time: 7:50:02 PM
 */
public class SelectedItemPanel extends AbsolutePanel implements HintWidgetProvider {
    public static final int WIDTH = 344;
    public static final int HEIGHT = 190;
    private EnemySelectedPanel enemySelectedPanel;
    private ResourceSelectedPanel resourceSelectedPanel;
    private OwnSingleSelectedPanel ownSingleSelectedPanel;
    private OwnMultiSelectedPanel ownMultiSelectedPanel;
    private HintWidgetProvider activeHintWidgetProvider;

    public SelectedItemPanel() {
        setPixelSize(WIDTH, HEIGHT);
        enemySelectedPanel = new EnemySelectedPanel();
        add(enemySelectedPanel, 0, 0);
        resourceSelectedPanel = new ResourceSelectedPanel();
        add(resourceSelectedPanel, 0, 0);
        ownSingleSelectedPanel = new OwnSingleSelectedPanel();
        add(ownSingleSelectedPanel, 0, 0);
        ownMultiSelectedPanel = new OwnMultiSelectedPanel();
        add(ownMultiSelectedPanel, 0, 0);
        displayNone();
    }

    public void displayEnemyItem(SyncBaseItem syncBaseItem) {
        displayNone();
        enemySelectedPanel.display(syncBaseItem);
        enemySelectedPanel.setVisible(true);
    }

    public void displayResourceItem(SyncResourceItem syncResourceItem) {
        displayNone();
        resourceSelectedPanel.display(syncResourceItem);
        resourceSelectedPanel.setVisible(true);
    }

    public void displayOwnSingleItem(ClientSyncItem clientSyncItem) {
        displayNone();
        ownSingleSelectedPanel.display(clientSyncItem);
        ownSingleSelectedPanel.setVisible(true);
        activeHintWidgetProvider = ownSingleSelectedPanel;
    }

    public void displayMultiOwnItems(Group selectedGroup) {
        displayNone();
        ownMultiSelectedPanel.display(selectedGroup);
        ownMultiSelectedPanel.setVisible(true);
    }

    public void displayNone() {
        enemySelectedPanel.setVisible(false);
        resourceSelectedPanel.setVisible(false);
        ownSingleSelectedPanel.setVisible(false);
        ownMultiSelectedPanel.setVisible(false);
        activeHintWidgetProvider = null;
    }

    @Override
    public Widget getHintWidgetAndEnsureVisible(CockpitSpeechBubbleHintConfig config) throws HintWidgetException {
        if (activeHintWidgetProvider != null) {
            return activeHintWidgetProvider.getHintWidgetAndEnsureVisible(config);
        } else {
            throw new HintWidgetException(this + " activeHintWidgetProvider == null", config);
        }
    }


    public void onMoneyChanged(double accountBalance) {
        if (ownSingleSelectedPanel.isVisible()) {
            ownSingleSelectedPanel.onMoneyChanged(accountBalance);
        } else if (ownMultiSelectedPanel.isVisible()) {
            ownMultiSelectedPanel.onMoneyChanged(accountBalance);
        }
    }

    public void onStateChanged() {
        if (ownSingleSelectedPanel.isVisible()) {
            ownSingleSelectedPanel.onStateChanged();
        } else if (ownMultiSelectedPanel.isVisible()) {
            ownMultiSelectedPanel.onStateChanged();
        }
    }

}