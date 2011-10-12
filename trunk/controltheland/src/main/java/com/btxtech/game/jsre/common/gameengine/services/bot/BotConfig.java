package com.btxtech.game.jsre.common.gameengine.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 10.10.2011
 * Time: 13:36:14
 */
public class BotConfig implements Serializable {
    private int id;
    private int actionDelay;
    private Collection<BotItemConfig> botItems;
    private Rectangle realm;
    private String name;
    private Long minInactiveMs;
    private Long maxInactiveMs;
    private Long minActiveMs;
    private Long maxActiveMs;

    /**
     * Used by GWT
     */
    BotConfig() {
    }

    public BotConfig(int id, int actionDelay, Collection<BotItemConfig> botItems, Rectangle realm, String name, Long minInactiveMs, Long maxInactiveMs, Long minActiveMs, Long maxActiveMs) {
        this.id = id;
        this.actionDelay = actionDelay;
        this.botItems = botItems;
        this.realm = realm;
        this.name = name;
        this.minInactiveMs = minInactiveMs;
        this.maxInactiveMs = maxInactiveMs;
        this.minActiveMs = minActiveMs;
        this.maxActiveMs = maxActiveMs;
    }

    public int getActionDelay() {
        return actionDelay;
    }

    public Collection<BotItemConfig> getBotItems() {
        return botItems;
    }

    public Rectangle getRealm() {
        return realm;
    }

    public String getName() {
        return name;
    }

    public Long getMinInactiveMs() {
        return minInactiveMs;
    }

    public Long getMaxInactiveMs() {
        return maxInactiveMs;
    }

    public Long getMinActiveMs() {
        return minActiveMs;
    }

    public Long getMaxActiveMs() {
        return maxActiveMs;
    }

    public boolean isIntervalBot() {
        return minInactiveMs != null || maxInactiveMs != null || minActiveMs != null || maxActiveMs != null;
    }

    public boolean isIntervalValid() {
        return !(minInactiveMs == null || maxInactiveMs == null || minActiveMs == null || maxActiveMs == null)
                && !(minInactiveMs <= 0 || maxInactiveMs <= 0 || minActiveMs <= 0 || maxActiveMs <= 0)
                && minInactiveMs <= maxInactiveMs
                && minActiveMs <= maxActiveMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BotConfig botConfig = (BotConfig) o;

        return id == botConfig.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
