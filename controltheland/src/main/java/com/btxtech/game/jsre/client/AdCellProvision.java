package com.btxtech.game.jsre.client;

import java.io.Serializable;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 11:59
 */
public class AdCellProvision implements Serializable {
    private SimpleUser simpleUser;
    private String bid;

    /**
     * Used by GWT
     */
    AdCellProvision() {
    }

    public AdCellProvision(SimpleUser simpleUser, String bid) {
        this.simpleUser = simpleUser;
        this.bid = bid;
    }

    public SimpleUser getSimpleUser() {
        return simpleUser;
    }

    public String getBid() {
        return bid;
    }

    public boolean isProvisionExpected() {
        return bid != null;
    }
}
