package com.btxtech.game.jsre.common;

import java.util.Map;

/**
 * User: beat
 * Date: 13.05.2011
 * Time: 13:11:55
 */
public class SimpleEntry<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public SimpleEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V getValue() {
        return value;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}