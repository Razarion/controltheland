package com.btxtech.game.jsre.common.gameengine.services.unlock.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 10.02.13
 * Time: 15:22
 */
public class UnlockContainer implements Serializable {
    private Set<Integer> itemTypes = new HashSet<Integer>();
    private Set<Integer> quests = new HashSet<Integer>();

    public boolean containsItemTypeId(int itemTypeId) {
        return itemTypes.contains(itemTypeId);
    }

    public void unlockItemType(int itemTypeId) {
        itemTypes.add(itemTypeId);
    }

    public void setItemTypes(Collection<Integer> itemTypes) {
        this.itemTypes = new HashSet<Integer>(itemTypes);
    }

    public Set<Integer> getItemTypes() {
        return itemTypes;
    }

    public boolean containsQuestId(int questId) {
        return quests.contains(questId);
    }

    public void unlockQuest(int questId) {
        quests.add(questId);
    }

    public void setQuests(Collection<Integer> quests) {
        this.quests = new HashSet<Integer>(quests);
    }

    public Set<Integer> getQuests() {
        return quests;
    }

    @Override
    public String toString() {
        return "UnlockContainer{itemTypes=" + itemTypes + " quests=" + quests + '}';
    }
}
