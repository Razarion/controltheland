package com.btxtech.game.jsre.common.tutorial;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.simulation.AbstractTask;
import com.btxtech.game.jsre.client.simulation.GuiEventTask;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.PraiseSplashPopupInfo;
import com.btxtech.game.jsre.client.utg.tip.StorySplashPopupInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 11.09.13
 * Time: 15:38
 */
public class GuiEventTaskConfig extends AbstractTaskConfig {
    private Rectangle scrollTargetRectangle;

    /**
     * Used by GWT
     */
    GuiEventTaskConfig() {
    }

    public GuiEventTaskConfig(List<ItemTypeAndPosition> ownItems, Index scroll, int houseCount, int money, int maxMoney, String name, Collection<BotConfig> botConfigs, Map<Integer, Integer> itemTypeLimitation, RadarMode radarMode, GameTipConfig gameTipConfig, boolean clearGame, Rectangle scrollTargetRectangle, StorySplashPopupInfo storySplashPopupInfo, PraiseSplashPopupInfo praiseSplashPopupInfo) {
        super(ownItems, scroll, houseCount, money, maxMoney, name, botConfigs, itemTypeLimitation, radarMode, gameTipConfig, clearGame, storySplashPopupInfo, praiseSplashPopupInfo);
        this.scrollTargetRectangle = scrollTargetRectangle;
    }

    @Override
    public AbstractTask createTask() {
        return new GuiEventTask(this);
    }

    public Rectangle getScrollTargetRectangle() {
        return scrollTargetRectangle;
    }
}
