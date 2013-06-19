package com.btxtech.game.jsre.client.common.info;

import java.io.Serializable;

/**
 * User: beat
 * Date: 02.06.13
 * Time: 13:30
 */
public class DetailedUser implements Serializable {
    private SimpleUser simpleUser;
    private int level;
    private String planet;

    /**
     * Used by GWT
     */
    DetailedUser() {
    }

    public DetailedUser(SimpleUser simpleUser, int level, String planet) {
        this.simpleUser = simpleUser;
        this.level = level;
        this.planet = planet;
    }

    public int getLevel() {
        return level;
    }

    public String getPlanet() {
        return planet;
    }

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }
}
