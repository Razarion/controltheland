package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

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
    private Map<SyncItem, Map<ItemClipPosition, ItemEffect>> oldCache;
    private Map<SyncItem, Map<ItemClipPosition, ItemEffect>> newCache;

    public static ItemEffectHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemEffectHandler() {
    }

    public Collection<ItemEffect> getClips(long timeStamp, Rectangle viewRect, Collection<SyncItem> itemsInView) {
        newCache = new HashMap<>();
        Collection<ItemEffect> itemEffects = new ArrayList<>();
        for (SyncItem syncItem : itemsInView) {
            if (syncItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                handleDemolition(syncBaseItem, timeStamp, viewRect, itemEffects);
                handleHarvest(syncBaseItem, timeStamp, viewRect, itemEffects);
                handleBuildup(syncBaseItem, timeStamp, viewRect, itemEffects);
            }
        }
        oldCache = newCache;
        newCache = null;
        return itemEffects;
    }

    private void handleDemolition(SyncBaseItem syncBaseItem, long timeStamp, Rectangle viewRect, Collection<ItemEffect> itemEffects) {
        if (syncBaseItem.isHealthy()) {
            return;
        }
        Collection<ItemClipPosition> demolitionClips = syncBaseItem.getItemType().getItemTypeSpriteMap().getDemolitionClipIds(syncBaseItem);
        if (demolitionClips == null || demolitionClips.isEmpty()) {
            return;
        }
        for (ItemClipPosition demolitionClip : demolitionClips) {
            addItemEffect(syncBaseItem, demolitionClip, null, timeStamp, viewRect, itemEffects);
        }
    }

    private void handleHarvest(SyncBaseItem syncBaseItem, long timeStamp, Rectangle viewRect, Collection<ItemEffect> itemEffects) {
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
        try {
            SyncResourceItem resource = (SyncResourceItem) ClientPlanetServices.getInstance().getItemService().getItem(syncBaseItem.getSyncHarvester().getTarget());
            addItemEffect(syncBaseItem, harvesterClipPosition, resource.getSyncItemArea().getPosition(), timeStamp, viewRect, itemEffects);
        } catch (ItemDoesNotExistException e) {
            ClientExceptionHandler.handleExceptionOnlyOnce("ItemEffectHandler handleHarvest", e);
        }
    }

    private void handleBuildup(SyncBaseItem syncBaseItem, long timeStamp, Rectangle viewRect, Collection<ItemEffect> itemEffects) {
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
        addItemEffect(syncBaseItem, buildupClipPosition, syncBaseItem.getSyncBuilder().getToBeBuildPosition(), timeStamp, viewRect, itemEffects);
    }

    private void addItemEffect(SyncBaseItem syncBaseItem, ItemClipPosition demolitionClip, Index target, long timeStamp, Rectangle viewRect, Collection<ItemEffect> itemEffects) {
        try {
            ItemEffect itemEffect = getFromOldCache(syncBaseItem, demolitionClip);
            if (itemEffect != null) {
                itemEffect.refresh(syncBaseItem);
            } else {
                itemEffect = new ItemEffect(timeStamp, syncBaseItem, demolitionClip, target);
            }
            itemEffect.prepareRender(timeStamp, viewRect);
            if (itemEffect.isInViewRect()) {
                itemEffects.add(itemEffect);
                putToNewCache(syncBaseItem, demolitionClip, itemEffect);
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleExceptionOnlyOnce("ItemEffectHandler.getClips()", e);
        }
    }

    private ItemEffect getFromOldCache(SyncBaseItem syncBaseItem, ItemClipPosition demolitionClip) {
        if (oldCache == null) {
            return null;
        }
        Map<ItemClipPosition, ItemEffect> positions = oldCache.get(syncBaseItem);
        if (positions == null) {
            return null;
        }
        return positions.get(demolitionClip);
    }


    private void putToNewCache(SyncBaseItem syncBaseItem, ItemClipPosition demolitionClip, ItemEffect itemEffect) {
        Map<ItemClipPosition, ItemEffect> itemEffects = newCache.get(syncBaseItem);
        if (itemEffects == null) {
            itemEffects = new HashMap<>();
            newCache.put(syncBaseItem, itemEffects);
        }
        itemEffects.put(demolitionClip, itemEffect);
    }

}
