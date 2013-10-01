package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 01.11.12
 * Time: 13:19
 */
public class ItemEffectHandler {
    private static ItemEffectHandler INSTANCE = new ItemEffectHandler();
    private Map<SyncItem, Map<ItemClipPosition, ItemEffect>> demolitionCache = new HashMap<SyncItem, Map<ItemClipPosition, ItemEffect>>();

    public static ItemEffectHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemEffectHandler() {
    }

    public Collection<ItemEffect> getClips(long timeStamp, Rectangle viewRect, Collection<SyncItem> itemsInView) {
        Collection<ItemEffect> itemEffects = new ArrayList<ItemEffect>();
        Map<SyncItem, Map<ItemClipPosition, ItemEffect>> tmpCache = new HashMap<SyncItem, Map<ItemClipPosition, ItemEffect>>();
        for (SyncItem syncItem : itemsInView) {
            if (syncItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                if (syncBaseItem.isHealthy()) {
                    continue;
                }
                Collection<ItemClipPosition> demolitionClips = syncBaseItem.getItemType().getItemTypeSpriteMap().getDemolitionClipIds(syncBaseItem);
                if (demolitionClips == null || demolitionClips.isEmpty()) {
                    continue;
                }
                Map<ItemClipPosition, ItemEffect> itemClipPositions = demolitionCache.get(syncItem);
                Map<ItemClipPosition, ItemEffect> tmpItemClipPositions = new HashMap<ItemClipPosition, ItemEffect>();
                tmpCache.put(syncBaseItem, tmpItemClipPositions);
                for (ItemClipPosition demolitionClip : demolitionClips) {
                    try {
                        ItemEffect itemEffect = null;
                        if (itemClipPositions != null) {
                            itemEffect = itemClipPositions.get(demolitionClip);
                        }
                        if (itemEffect == null) {
                            itemEffect = new ItemEffect(syncBaseItem, demolitionClip);
                        } else {
                            itemEffect.refresh(syncBaseItem);
                        }
                        itemEffect.prepareRender(timeStamp, viewRect);
                        if (itemEffect.isInViewRect()) {
                            tmpItemClipPositions.put(demolitionClip, itemEffect);
                            itemEffects.add(itemEffect);
                        }
                    } catch (Exception e) {
                        ClientExceptionHandler.handleExceptionOnlyOnce("ItemEffectHandler.getClips()", e);
                    }
                }
            }
        }
        demolitionCache = tmpCache;
        return itemEffects;
    }
}
