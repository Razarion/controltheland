package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.AutomatedBattleTask;

/**
 * User: beat
 * Date: 25.09.13
 * Time: 16:36
 */
public class AutomatedBattleTaskConfig extends AbstractTaskConfig {
    private ItemTypeAndPosition botAttacker;
    private String botName;
    private int targetItemType;
    private double attackerHealthFactor;

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

    public void setAttackerHealthFactor(double attackerHealthFactor) {
        this.attackerHealthFactor = attackerHealthFactor;
    }

    public void setBotAttacker(ItemTypeAndPosition botAttacker) {
        this.botAttacker = botAttacker;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setTargetItemType(int targetItemType) {
        this.targetItemType = targetItemType;
    }
}
