package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 14.06.12
 * Time: 12:44
 */
public class BotEnragementState {
    public interface Listener {
        void onEnrageNormal(String botName, BotEnragementStateConfig botEnragementStateConfig);

        void onEnrageUp(String botName, BotEnragementStateConfig botEnragementStateConfig, SimpleBase actor);
    }

    private List<BotEnragementStateConfig> botEnragementStateConfigs;
    private BotEnragementStateConfig currentBotEnragementStateConfig;
    private boolean isEnragementActive;
    private BotItemContainer botItemContainer;
    private final Region realm;
    private final PlanetServices planetServices;
    private final String botName;
    private Map<SimpleBase, Integer> killsPerBase = new HashMap<SimpleBase, Integer>();
    private Listener listener;

    public BotEnragementState(List<BotEnragementStateConfig> botEnragementStateConfigs, Region realm, PlanetServices planetServices, String botName, Listener listener) {
        this.botEnragementStateConfigs = botEnragementStateConfigs;
        this.realm = realm;
        this.planetServices = planetServices;
        this.botName = botName;
        this.listener = listener;
        if (botEnragementStateConfigs.isEmpty()) {
            throw new IllegalArgumentException("Bot must have at least one enragement state configured: " + botName);
        }
        activateEnragementState(botEnragementStateConfigs.get(0), null);
    }

    public void work(SimpleBase base) {
        botItemContainer.work(base);
    }

    public boolean isFulfilledUseInTestOnly(SimpleBase base) {
        return botItemContainer.isFulfilledUseInTestOnly(base);
    }

    public void killAllItems(SimpleBase base) {
        botItemContainer.killAllItems(base);
    }

    public Collection<BotSyncBaseItem> getAllIdleAttackers() {
        return botItemContainer.getAllIdleAttackers();
    }

    private void activateEnragementState(BotEnragementStateConfig botEnragementStateConfig, SimpleBase base) {
        if (base != null && currentBotEnragementStateConfig != null) {
            botItemContainer.killAllItems(base);
        }
        currentBotEnragementStateConfig = botEnragementStateConfig;
        botItemContainer = new BotItemContainer(botEnragementStateConfig.getBotItems(), realm, planetServices, botName);
        killsPerBase.clear();
        isEnragementActive = currentBotEnragementStateConfig.hasMaxKillsPerBase() && botEnragementStateConfigs.indexOf(currentBotEnragementStateConfig) + 1 < botEnragementStateConfigs.size();
    }

    public void handleIntruders(Collection<SyncBaseItem> allIntruders, SimpleBase botBase) {
        if (allIntruders.isEmpty()) {
            if (!currentBotEnragementStateConfig.equals(botEnragementStateConfigs.get(0))) {
                BotEnragementStateConfig normalState = botEnragementStateConfigs.get(0);
                activateEnragementState(normalState, botBase);
                if (listener != null) {
                    listener.onEnrageNormal(botName, normalState);
                }
            }
        }
        Set<SimpleBase> intruderBases = getAllBases(allIntruders);
        for (Iterator<SimpleBase> iterator = killsPerBase.keySet().iterator(); iterator.hasNext(); ) {
            SimpleBase oldIntruder = iterator.next();
            if (!intruderBases.contains(oldIntruder)) {
                iterator.remove();
            }
        }
    }

    private Set<SimpleBase> getAllBases(Collection<SyncBaseItem> allIntruders) {
        Set<SimpleBase> bases = new HashSet<SimpleBase>();
        for (SyncBaseItem intruder : allIntruders) {
            bases.add(intruder.getBase());
        }
        return bases;
    }

    public void onBotItemKilled(SyncBaseItem botBaseItem, SimpleBase actor) {
        if (botItemContainer.itemBelongsToMy(botBaseItem)) {
            if (isEnragementActive) {
                Integer kills = killsPerBase.get(actor);
                if (kills == null) {
                    kills = 0;
                }
                kills = kills + 1;
                killsPerBase.put(actor, kills);
                if (kills >= currentBotEnragementStateConfig.getEnrageUpKills()) {
                    BotEnragementStateConfig nextState = botEnragementStateConfigs.get(botEnragementStateConfigs.indexOf(currentBotEnragementStateConfig) + 1);
                    activateEnragementState(nextState, botBaseItem.getBase());
                    if (listener != null) {
                        listener.onEnrageUp(botName, nextState, actor);
                    }
                }
                // TODO remove the killed bot item from the botItemContainer here instead of iterating over and removing the death items
                // TODO keep in mind: this method is only called if the actor is not null -> solve
            }
        }
    }
}
