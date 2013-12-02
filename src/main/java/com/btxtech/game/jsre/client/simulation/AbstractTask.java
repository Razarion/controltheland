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

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;
import com.btxtech.game.jsre.client.utg.tip.PraiseSplashPopupInfo;
import com.btxtech.game.jsre.client.utg.tip.StorySplashPopup;
import com.btxtech.game.jsre.client.utg.tip.tiptask.AbstractTipTask;
import com.btxtech.game.jsre.common.tutorial.AbstractTaskConfig;

/**
 * User: beat Date: 18.07.2010 Time: 13:36:19
 */
abstract public class AbstractTask {
    private AbstractTaskConfig abstractTaskConfig;
    private Simulation simulation;
    private StorySplashPopup storySplashPopup;

    public AbstractTask(AbstractTaskConfig abstractTaskConfig) {
        this.abstractTaskConfig = abstractTaskConfig;
    }

    protected abstract void internStart();

    protected abstract void internCleanup();

    public final void start() {
        ClientBase.getInstance().setAccountBalance(abstractTaskConfig.getMoney());
        startSplashDialog();
        internStart();
        GameTipManager.getInstance().start(abstractTaskConfig.getGameTipConfig());
    }

    public final void cleanup() {
        hideTipSplashPopup(); // TODO make a clever hide mechnism
        internCleanup();
        GameTipManager.getInstance().stop();
    }

    public AbstractTaskConfig getAbstractTaskConfig() {
        return abstractTaskConfig;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    protected void onTaskSucceeded() {
        simulation.onTaskSucceeded();
    }

    public void onTaskConversion() {
        // TODO move to ScrollTipTask
        if (storySplashPopup != null) {
            storySplashPopup.fadeOut();
        }
    }

    public void onTaskPoorConversion() {
        // TODO move to ScrollTipTask
        if (storySplashPopup != null) {
            storySplashPopup.fadeIn();
        }
    }

    private void startSplashDialog() {
        if (abstractTaskConfig.getStorySplashPopupInfo() == null) {
            return;
        }
        storySplashPopup = new StorySplashPopup(abstractTaskConfig.getStorySplashPopupInfo());
    }

    private void hideTipSplashPopup() {
        if (storySplashPopup != null) {
            storySplashPopup.fadeOut();
            storySplashPopup = null;
        }
    }

    public PraiseSplashPopupInfo getPraiseSplashPopupInfo() {
        return abstractTaskConfig.getPraiseSplashPopupInfo();
    }

    public void onTipTaskChanged(AbstractTipTask currentTipTask) {
        if (storySplashPopup != null) {
            if (currentTipTask.getTaskText() != null) {
                storySplashPopup.fadeIn();
                storySplashPopup.setTaskText(currentTipTask.getTaskText());
            } else {
                storySplashPopup.fadeOut();
            }
        }
    }

    public void onTipTaskConversion() {
        if (storySplashPopup != null) {
            storySplashPopup.fadeOut();
        }
    }

}
