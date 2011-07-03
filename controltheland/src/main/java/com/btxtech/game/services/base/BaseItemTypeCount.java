package com.btxtech.game.services.base;

import com.btxtech.game.services.common.SimpleCrudChild;
import com.btxtech.game.services.item.itemType.DbBaseItemType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 03.07.2011
 * Time: 11:15:24
 */
public class BaseItemTypeCount extends SimpleCrudChild {
    private int count = 0;
    private DbBaseItemType dbBaseItemType;

    public BaseItemTypeCount(DbBaseItemType dbBaseItemType) {
        this.dbBaseItemType = dbBaseItemType;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count++;
    }

    public DbBaseItemType getDbBaseItemType() {
        return dbBaseItemType;
    }

    @Override
    public Serializable getId() {
        return dbBaseItemType.getId();
    }
}
