package com.btxtech.game.jsre.common.gameengine.services.bot;

import com.btxtech.game.jsre.common.Region;
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
    private Region region;
    private boolean moveRealmIfIdle;
    private Integer idleTtl;
    private boolean noRebuild;
    private Long rePopTime;

    /**
     * Used by GWT
     */
    BotItemConfig() {
    }

    public BotItemConfig(BaseItemType baseItemType, int count, boolean createDirectly, Region region, boolean moveRealmIfIdle, Integer idleTtl, boolean noRebuild, Long rePopTime) {
        this.baseItemType = baseItemType;
        this.count = count;
        this.createDirectly = createDirectly;
        this.region = region;
        this.moveRealmIfIdle = moveRealmIfIdle;
        this.idleTtl = idleTtl;
        this.noRebuild = noRebuild;
        this.rePopTime = rePopTime;
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

    public Region getRegion() {
        return region;
    }

    public boolean isMoveRealmIfIdle() {
        return moveRealmIfIdle;
    }

    public Integer getIdleTtl() {
        return idleTtl;
    }

    public boolean isNoRebuild() {
        return noRebuild;
    }

    public boolean hasRePopTime() {
        return rePopTime != null;
    }

    public long getRePopTime() {
        return rePopTime;
    }
}
