package com.btxtech.game.services.market;

import com.btxtech.game.services.common.SimpleCrudChild;

import java.io.Serializable;

/**
 * User: beat
 * Date: 05.07.2011
 * Time: 13:42:56
 */
public class AvailableMarketEntry extends SimpleCrudChild {
    private DbMarketEntry dbMarketEntry;

    public AvailableMarketEntry(DbMarketEntry dbMarketEntry) {
        this.dbMarketEntry = dbMarketEntry;
    }

    public DbMarketEntry getDbMarketEntry() {
        return dbMarketEntry;
    }

    @Override
    public Serializable getId() {
        return dbMarketEntry.getId();
    }
}
