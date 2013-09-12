package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.ConditionTask;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.StorySplashPopupInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 15:17
 */
public class ConditionTaskConfig extends AbstractTaskConfig {
    private ConditionConfig conditionConfig;

    /**
     * Used by GWT
     */
    ConditionTaskConfig() {
    }

    public ConditionTaskConfig(List<ItemTypeAndPosition> ownItems, Index scroll, ConditionConfig conditionConfig, int houseCount, int money, int maxMoney, String name, Collection<BotConfig> botConfigs, Map<Integer, Integer> itemTypeLimitation, RadarMode radarMode, GameTipConfig gameTipConfig, boolean clearGame, StorySplashPopupInfo storySplashPopupInfo, StorySplashPopupInfo praiseSplash) {
        super(ownItems, scroll, houseCount, money, maxMoney, name, botConfigs, itemTypeLimitation, radarMode, gameTipConfig, clearGame, storySplashPopupInfo, praiseSplash);
        this.conditionConfig = conditionConfig;
    }

    @Override
    public AbstractTask createTask() {
        return new ConditionTask(this);
    }

    public ConditionConfig getConditionConfig() {
        return conditionConfig;
    }


}
