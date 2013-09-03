package com.btxtech.game.jsre.common.utg.condition;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 05.02.2012
 * Time: 13:11:53
 */
public class GenericComparisonValueContainer {
    public enum Key {
        REMAINING_COUNT,
        REMAINING_ITEM_TYPES,
        REMAINING_TIME
    }

    private Map<Object, Object> children = new HashMap<Object, Object>();

    public GenericComparisonValueContainer() {
    }

    public void addChild(Key key, Object value) {
        if (children.containsKey(key)) {
            throw new GenericComparisonValueException("Already exists: " + key);
        }
        children.put(key, value);
    }

    public void addChild(ItemType key, Object value) {
        if (children.containsKey(key)) {
            throw new GenericComparisonValueException("Already exists: " + key);
        }
        children.put(key, value);
    }

    public GenericComparisonValueContainer createChildContainer(Key key) {
        GenericComparisonValueContainer childContainer = new GenericComparisonValueContainer();
        addChild(key, childContainer);
        return childContainer;
    }

    public Object getValue(Key key) {
        Object value = children.get(key);
        if (value == null) {
            throw new GenericComparisonValueException("No such key: " + key);
        }
        return value;
    }

    public Object getValue(ItemType key) {
        Object value = children.get(key);
        if (value == null) {
            throw new GenericComparisonValueException("No such key: " + key);
        }
        return value;
    }

    public GenericComparisonValueContainer getChildContainer(Key key) {
        Object value = children.get(key);
        if (value == null) {
            throw new GenericComparisonValueException("No such key: " + key);
        }
        if (value instanceof GenericComparisonValueContainer) {
            return (GenericComparisonValueContainer) value;
        } else {
            throw new GenericComparisonValueException("Is no GenericComparisonValueContainer: " + value);
        }
    }

    public Collection<Map.Entry<Object, Object>> getEntries() {
        return children.entrySet();
    }

    public boolean hasKey(Key key) {
        return children.containsKey(key);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}
