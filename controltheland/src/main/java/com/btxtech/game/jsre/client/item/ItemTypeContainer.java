package com.btxtech.game.jsre.client.item;

import com.btxtech.game.jsre.common.gameengine.services.items.impl.AbstractItemTypeService;

/**
 * User: beat
 * Date: 28.08.12
 * Time: 00:52
 */
public class ItemTypeContainer extends AbstractItemTypeService {
   private static final ItemTypeContainer INSTANCE = new ItemTypeContainer();

    public static ItemTypeContainer getInstance() {
        return INSTANCE;
    }
}
