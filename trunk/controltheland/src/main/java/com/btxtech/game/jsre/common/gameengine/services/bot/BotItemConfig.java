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

    public BotItemConfig(BaseItemType baseItemType, int count, boolean createDirectly, Rectangle region) {
        this.baseItemType = baseItemType;
        this.count = count;
        this.createDirectly = createDirectly;
        this.region = region;
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
}
