package com.btxtech.game.jsre.client;

import java.io.Serializable;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 11:56
 */
public class SimpleUser implements Serializable {
    private String name;
    private int id;

    /**
     * Used by GWT
     */
    SimpleUser() {
    }

    public SimpleUser(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleUser that = (SimpleUser) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
