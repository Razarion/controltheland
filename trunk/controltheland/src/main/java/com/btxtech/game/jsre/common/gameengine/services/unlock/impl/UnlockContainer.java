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
    private Set<Integer> itemTypes;

    /**
     * Used by GWT
     */
    UnlockContainer() {
    }

    public UnlockContainer(Collection<Integer> unlockedItemTypes) {
        if (unlockedItemTypes != null) {
            itemTypes = new HashSet<Integer>(unlockedItemTypes);
        } else {
            itemTypes = new HashSet<Integer>();
        }
    }

    public boolean containsItemTypeId(int itemTypeId) {
        return itemTypes.contains(itemTypeId);
    }

    public void unlockItemType(int itemTypeId) {
        itemTypes.add(itemTypeId);
    }

    public Set<Integer> getItemTypes() {
        return itemTypes;
    }

    @Override
    public String toString() {
        return "UnlockContainer{itemTypes=" + itemTypes + '}';
    }
}
