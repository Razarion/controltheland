/*
 * Copyright (c) 2011.
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

package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.control.ClientRunner;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.dialogs.LevelTargetDialog;

/**
 * User: beat
 * Date: 09.01.2011
 * Time: 14:20:37
 */
public class ClientLevelHandler {
    private static final ClientLevelHandler INSTANCE = new ClientLevelHandler();
    private Level level;

    /**
     * Singleton
     */
    private ClientLevelHandler() {
    }

    public static ClientLevelHandler getInstance() {
        return INSTANCE;
    }

    public void setLevel(Level level) {
        this.level = level;
        Cockpit.getInstance().setLevel(level.getName());
    }


    public void onLevelChanged(Level level) {
        if (this.level == null) {
            throw new IllegalStateException("ClientLevelHandler: level has not been set before.");
        }
        Level oldLevel = this.level;
        this.level = level;
        LevelTargetDialog.showDialog(level.getHtml());
        if (oldLevel.isRealGame() && level.isRealGame()) {
            Cockpit.getInstance().setLevel(level.getName());
            // TODO set scope
            // TODO LevelTargetDialog.showDialog(level.getHtml());
            // TODO ClientBase.getInstance().setItemLimit(levelPacket.getLevel().getItemLimit());
            // TODO Cockpit.getInstance().updateItemLimit();
            // TODO loadingRequired = true;
            // TODO String oldLevel = level.getName();
            // TODO setLevel(levelPacket.getLevel());
            // TODO PromotionDialog promotionDialog = PromotionDialog.showPromotion(oldLevel, level.getName());
            // TODO promotionDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
            // TODO     @Override
            // TODO     public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
            // TODO         // TODO
            // TODO         showMissionTargetDialog();
            // TODO     }
            // TODO });
        } else {
            GameStartupSeq gameStartupSeq;
            if (level.isRealGame()) {
                gameStartupSeq = GameStartupSeq.WARM_REAL;
            } else {
                gameStartupSeq = GameStartupSeq.WARM_SIMULATED;
            }
            ClientServices.getInstance().getClientRunner().start(gameStartupSeq);
        }
    }

    public String getHtmlLevel() {
        return level.getHtml();
    }
}
