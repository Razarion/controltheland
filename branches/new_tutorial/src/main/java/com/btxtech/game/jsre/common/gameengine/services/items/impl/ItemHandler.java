package com.btxtech.game.jsre.common.gameengine.services.items.impl;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 07.10.2011
 * Time: 14:54:10
 */
public interface ItemHandler<T> {
    /**
     * Is called for every SyncItem
     *
     * @param syncItem syncItem
     * @return null if the iteration shall continue T if the iteration shall stop
     */
    T handleItem(SyncItem syncItem);
}
