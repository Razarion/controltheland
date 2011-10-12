package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 08.08.2011
 * Time: 14:08:04
 */
public class Need {
    private Map<BotItemConfig, Integer> directCreatedNeeds = new HashMap<BotItemConfig, Integer>();
    private Map<BotItemConfig, Integer> normalNeeds = new HashMap<BotItemConfig, Integer>();

    public Need(Collection<BotItemConfig> botItemConfigs) {
        for (BotItemConfig botItemConfig : botItemConfigs) {
            if (botItemConfig.isCreateDirectly()) {
                directCreatedNeeds.put(botItemConfig, botItemConfig.getCount());
            } else {
                normalNeeds.put(botItemConfig, botItemConfig.getCount());
            }
        }
    }

    public int getNeedCount() {
        int totalNeedCount = 0;
        for (Integer integer : directCreatedNeeds.values()) {
            totalNeedCount += integer;
        }
        for (Integer integer : normalNeeds.values()) {
            totalNeedCount += integer;
        }
        return totalNeedCount;
    }

    public int getNeedCount(BotItemConfig botItemConfig) {
        Integer need = directCreatedNeeds.get(botItemConfig);
        if (need != null) {
            return need;
        }
        return normalNeeds.get(botItemConfig);
    }

    public List<BotItemConfig> getItemNeed() {
        ArrayList<BotItemConfig> needs = new ArrayList<BotItemConfig>();
        for (Map.Entry<BotItemConfig, Integer> entry : directCreatedNeeds.entrySet()) {
            if (entry.getValue() > 0) {
                needs.add(entry.getKey());
            }
        }
        for (Map.Entry<BotItemConfig, Integer> entry : normalNeeds.entrySet()) {
            if (entry.getValue() > 0) {
                needs.add(entry.getKey());
            }
        }
        return needs;
    }

    public void onItemAdded(BotSyncBaseItem botSyncBaseItem) {
        BotItemConfig botItemConfig = getSuitableDbBotItemConfig(botSyncBaseItem, false);
        setNeedCount(botItemConfig, false);
    }

    public void onItemRemoved(BotSyncBaseItem botSyncBaseItem) {
        BotItemConfig botItemConfig = getSuitableDbBotItemConfig(botSyncBaseItem, true);
        setNeedCount(botItemConfig, true);
    }

    private void setNeedCount(BotItemConfig dbBotItemConfig, boolean increase) {
        Integer need = directCreatedNeeds.get(dbBotItemConfig);
        if (need != null) {
            if (increase) {
                directCreatedNeeds.put(dbBotItemConfig, need + 1);
            } else if (need > 0) {
                directCreatedNeeds.put(dbBotItemConfig, need - 1);
            }
        } else {
            need = normalNeeds.get(dbBotItemConfig);
            if (increase) {
                normalNeeds.put(dbBotItemConfig, need + 1);
            } else if (need > 0) {
                normalNeeds.put(dbBotItemConfig, need - 1);
            }
        }
    }

    private BotItemConfig getSuitableDbBotItemConfig(BotSyncBaseItem botSyncBaseItem, boolean ignoreRegion) {
        if (botSyncBaseItem.getSyncBaseItem().getId().hasParent()) {
            return getSuitableDbBotItemConfig(botSyncBaseItem, normalNeeds.keySet(), ignoreRegion);
        } else {
            return getSuitableDbBotItemConfig(botSyncBaseItem, directCreatedNeeds.keySet(), ignoreRegion);
        }
    }

    private BotItemConfig getSuitableDbBotItemConfig(BotSyncBaseItem botSyncBaseItem, Collection<BotItemConfig> botItemConfigs, boolean ignoreRegion) {
        for (BotItemConfig botItemConfig : botItemConfigs) {
            if (botItemConfig.getBaseItemType().getId() == botSyncBaseItem.getSyncBaseItem().getBaseItemType().getId()) {
                if (!ignoreRegion && botItemConfig.getRegion() != null) {
                    if (botSyncBaseItem.getSyncBaseItem().getSyncItemArea().contains(botItemConfig.getRegion())) {
                        return botItemConfig;
                    }
                } else {
                    return botItemConfig;
                }
            }
        }
        throw new IllegalStateException("No DbBotItemConfig found for: " + botSyncBaseItem);
    }

}
