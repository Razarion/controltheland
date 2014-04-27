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
    private Map<SyncItem, Map<ItemClipPosition, ItemEffect>> cache = new HashMap<>();

    public static ItemEffectHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemEffectHandler() {
    }

    public Collection<ItemEffect> getClips(long timeStamp, Rectangle viewRect, Collection<SyncItem> itemsInView) {
        Collection<ItemEffect> itemEffects = new ArrayList<>();
        Map<SyncItem, Map<ItemClipPosition, ItemEffect>> tmpCache = new HashMap<>();
        for (SyncItem syncItem : itemsInView) {
            if (syncItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                handleDemolition(syncBaseItem, timeStamp, viewRect, tmpCache, itemEffects);
                handleHarvest(syncBaseItem, timeStamp, viewRect, tmpCache, itemEffects);
                handleBuildup(syncBaseItem, timeStamp, viewRect, tmpCache, itemEffects);
            }
        }
        cache = tmpCache;
        return itemEffects;
    }

    private void handleDemolition(SyncBaseItem syncBaseItem, long timeStamp, Rectangle viewRect, Map<SyncItem, Map<ItemClipPosition, ItemEffect>> tmpCache, Collection<ItemEffect> itemEffects) {
        if (syncBaseItem.isHealthy()) {
            return;
        }
        Collection<ItemClipPosition> demolitionClips = syncBaseItem.getItemType().getItemTypeSpriteMap().getDemolitionClipIds(syncBaseItem);
        if (demolitionClips == null || demolitionClips.isEmpty()) {
            return;
        }
        for (ItemClipPosition demolitionClip : demolitionClips) {
            addItemEffect(syncBaseItem, demolitionClip, timeStamp, viewRect, tmpCache, itemEffects);
        }
    }

    private void handleHarvest(SyncBaseItem syncBaseItem, long timeStamp, Rectangle viewRect, Map<SyncItem, Map<ItemClipPosition, ItemEffect>> tmpCache, Collection<ItemEffect> itemEffects) {
        if (!syncBaseItem.hasSyncHarvester()) {
            return;
        }
        if (!syncBaseItem.getSyncHarvester().isHarvesting()) {
            return;
        }
        ItemClipPosition harvesterClipPosition = syncBaseItem.getSyncHarvester().getHarvesterType().getHarvesterClip();
        if (harvesterClipPosition == null) {
            return;
        }
        addItemEffect(syncBaseItem, harvesterClipPosition, timeStamp, viewRect, tmpCache, itemEffects);
    }

    private void handleBuildup(SyncBaseItem syncBaseItem, long timeStamp, Rectangle viewRect, Map<SyncItem, Map<ItemClipPosition, ItemEffect>> tmpCache, Collection<ItemEffect> itemEffects) {
        if (!syncBaseItem.hasSyncBuilder()) {
            return;
        }
        if (!syncBaseItem.getSyncBuilder().isBuilding()) {
            return;
        }
        ItemClipPosition buildupClipPosition = syncBaseItem.getSyncBuilder().getBuilderType().getBuildupClip();
        if (buildupClipPosition == null) {
            return;
        }
        addItemEffect(syncBaseItem, buildupClipPosition, timeStamp, viewRect, tmpCache, itemEffects);
    }

    private void addItemEffect(SyncBaseItem syncBaseItem, ItemClipPosition demolitionClip, long timeStamp, Rectangle viewRect, Map<SyncItem, Map<ItemClipPosition, ItemEffect>> tmpCache, Collection<ItemEffect> itemEffects) {
        Map<ItemClipPosition, ItemEffect> itemClipPositions = cache.get(syncBaseItem);
        Map<ItemClipPosition, ItemEffect> tmpItemClipPositions = new HashMap<>();
        tmpCache.put(syncBaseItem, tmpItemClipPositions);
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
