package com.btxtech.game.services.bot.impl;

import com.btxtech.game.services.bot.DbBotItemConfig;

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
    private Map<DbBotItemConfig, Integer> directCreatedNeeds = new HashMap<DbBotItemConfig, Integer>();
    private Map<DbBotItemConfig, Integer> normalNeeds = new HashMap<DbBotItemConfig, Integer>();

    public Need(Collection<DbBotItemConfig> dbBotItemConfigs) {
        for (DbBotItemConfig dbBotItemConfig : dbBotItemConfigs) {
            if (dbBotItemConfig.isCreateDirectly()) {
                directCreatedNeeds.put(dbBotItemConfig, dbBotItemConfig.getCount());
            } else {
                normalNeeds.put(dbBotItemConfig, dbBotItemConfig.getCount());
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

    public int getNeedCount(DbBotItemConfig dbBotItemConfig) {
        Integer need = directCreatedNeeds.get(dbBotItemConfig);
        if (need != null) {
            return need;
        }
        return normalNeeds.get(dbBotItemConfig);
    }

    public List<DbBotItemConfig> getItemNeed() {
        ArrayList<DbBotItemConfig> needs = new ArrayList<DbBotItemConfig>();
        for (Map.Entry<DbBotItemConfig, Integer> entry : directCreatedNeeds.entrySet()) {
            if (entry.getValue() > 0) {
                needs.add(entry.getKey());
            }
        }
        for (Map.Entry<DbBotItemConfig, Integer> entry : normalNeeds.entrySet()) {
            if (entry.getValue() > 0) {
                needs.add(entry.getKey());
            }
        }
        return needs;
    }

    public void onItemAdded(BotSyncBaseItem botSyncBaseItem) {
        DbBotItemConfig dbBotItemConfig = getSuitableDbBotItemConfig(botSyncBaseItem, false);
        setNeedCount(dbBotItemConfig, false);
    }

    public void onItemRemoved(BotSyncBaseItem botSyncBaseItem) {
        DbBotItemConfig dbBotItemConfig = getSuitableDbBotItemConfig(botSyncBaseItem, true);
        setNeedCount(dbBotItemConfig, true);
    }

    private void setNeedCount(DbBotItemConfig dbBotItemConfig, boolean increase) {
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

    private DbBotItemConfig getSuitableDbBotItemConfig(BotSyncBaseItem botSyncBaseItem, boolean ignoreRegion) {
        if (botSyncBaseItem.getSyncBaseItem().getId().hasParent()) {
            return getSuitableDbBotItemConfig(botSyncBaseItem, normalNeeds.keySet(), ignoreRegion);
        } else {
            return getSuitableDbBotItemConfig(botSyncBaseItem, directCreatedNeeds.keySet(), ignoreRegion);
        }
    }

    private DbBotItemConfig getSuitableDbBotItemConfig(BotSyncBaseItem botSyncBaseItem, Collection<DbBotItemConfig> botItemConfigs, boolean ignoreRegion) {
        for (DbBotItemConfig dbBotItemConfig : botItemConfigs) {
            if (dbBotItemConfig.getBaseItemType().getId() == botSyncBaseItem.getSyncBaseItem().getBaseItemType().getId()) {
                if (!ignoreRegion && dbBotItemConfig.getRegion() != null) {
                    if (dbBotItemConfig.getRegion().contains(botSyncBaseItem.getSyncBaseItem().getPosition())) {
                        return dbBotItemConfig;
                    }
                } else {
                    return dbBotItemConfig;
                }
            }
        }
        throw new IllegalStateException("No DbBotItemConfig found for: " + botSyncBaseItem);
    }

}
