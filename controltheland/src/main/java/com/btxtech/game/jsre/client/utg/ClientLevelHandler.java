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
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;

/**
 * User: beat
 * Date: 09.01.2011
 * Time: 14:20:37
 */
public class ClientLevelHandler implements CommonUserGuidanceService {
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
        SideCockpit.getInstance().setLevel(level);
    }


    public void onLevelChanged(Level level) {
        if (this.level == null) {
            throw new IllegalStateException("ClientLevelHandler: level has not been set before.");
        }
        Level oldLevel = this.level;
        this.level = level;
        if (oldLevel.isRealGame() && level.isRealGame()) {
            SideCockpit.getInstance().setLevel(level);
            SideCockpit.getInstance().updateItemLimit();
        } else {
            StartupScreen.getInstance().fadeOut(new Runnable() {
                @Override
                public void run() {
                    GameStartupSeq gameStartupSeq;
                    if (ClientLevelHandler.this.level.isRealGame()) {
                        gameStartupSeq = GameStartupSeq.WARM_REAL;
                    } else {
                        gameStartupSeq = GameStartupSeq.WARM_SIMULATED;
                    }
                    ClientServices.getInstance().getClientRunner().start(gameStartupSeq);
                }
            });
        }
    }

    public String getHtmlLevel() {
        return level.getHtml();
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public double getItemSellFactor() {
        return 0.5;
    }
}
