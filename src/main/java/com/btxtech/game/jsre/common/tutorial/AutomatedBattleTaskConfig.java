package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.AutomatedBattleTask;
import com.btxtech.game.jsre.client.utg.tip.PraiseSplashPopupInfo;

/**
 * User: beat
 * Date: 25.09.13
 * Time: 16:36
 */
public class AutomatedBattleTaskConfig extends AbstractAutomatedTaskConfig {
    private ItemTypeAndPosition botAttacker;
    private String botName;
    private int targetItemType;
    private double attackerHealthFactor;


    AutomatedBattleTaskConfig() {
    }

    public AutomatedBattleTaskConfig(PraiseSplashPopupInfo praiseSplashPopupInfo, ItemTypeAndPosition botAttacker, double attackerHealthFactor, String botName, int targetItemType) {
        super(null, praiseSplashPopupInfo);
        this.botAttacker = botAttacker;
        this.attackerHealthFactor = attackerHealthFactor;
        this.botName = botName;
        this.targetItemType = targetItemType;
    }

    @Override
    public AbstractTask createTask() {
        return new AutomatedBattleTask(this);
    }

    public ItemTypeAndPosition getBotAttacker() {
        return botAttacker;
    }

    public String getBotName() {
        return botName;
    }

    public int getTargetItemType() {
        return targetItemType;
    }

    public double getAttackerHealthFactor() {
        return attackerHealthFactor;
    }
}
