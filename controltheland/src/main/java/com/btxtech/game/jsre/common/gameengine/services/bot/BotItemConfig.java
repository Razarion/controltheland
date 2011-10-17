package com.btxtech.game.jsre.common.gameengine.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 10.10.2011
 * Time: 13:37:24
 */
public class BotItemConfig implements Serializable {
    private BaseItemType baseItemType;
    private int count;
    private boolean createDirectly;
    private Rectangle region;
    private boolean moveRealmIfIdle;
    private Integer idleTtl;

    /**
     * Used by GWT
     */
    BotItemConfig() {
    }

    public BotItemConfig(BaseItemType baseItemType, int count, boolean createDirectly, Rectangle region, boolean moveRealmIfIdle, Integer idleTtl) {
        this.baseItemType = baseItemType;
        this.count = count;
        this.createDirectly = createDirectly;
        this.region = region;
        this.moveRealmIfIdle = moveRealmIfIdle;
        this.idleTtl = idleTtl;
    }

    public BaseItemType getBaseItemType() {
        return baseItemType;
    }

    public int getCount() {
        return count;
    }

    public boolean isCreateDirectly() {
        return createDirectly;
    }

    public Rectangle getRegion() {
        return region;
    }

    public boolean isMoveRealmIfIdle() {
        return moveRealmIfIdle;
    }

    public Integer getIdleTtl() {
        return idleTtl;
    }
}
