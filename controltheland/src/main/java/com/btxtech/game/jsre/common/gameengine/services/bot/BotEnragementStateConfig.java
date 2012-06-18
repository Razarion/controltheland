package com.btxtech.game.jsre.common.gameengine.services.bot;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 14.06.12
 * Time: 12:41
 */
public class BotEnragementStateConfig implements Serializable {
    private String name;
    private Collection<BotItemConfig> botItems;
    private Integer enrageUpKills;

    /**
     * Used by GWT
     */
    BotEnragementStateConfig() {
    }

    public BotEnragementStateConfig(String name, Collection<BotItemConfig> botItems, Integer enrageUpKills) {
        this.name = name;
        this.botItems = botItems;
        this.enrageUpKills = enrageUpKills;
    }

    public String getName() {
        return name;
    }

    public Collection<BotItemConfig> getBotItems() {
        return botItems;
    }

    public boolean hasMaxKillsPerBase() {
        return enrageUpKills != null;
    }

    public int getEnrageUpKills() {
        return enrageUpKills;
    }
}
