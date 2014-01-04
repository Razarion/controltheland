package com.btxtech.game.wicket.pages.mgmt.usermgmt;

import com.btxtech.game.jsre.client.common.info.SimpleUser;

import java.io.Serializable;

/**
 * User: beat
 * Date: 03.01.14
 * Time: 20:10
 */
public class UserEntryHelper implements Serializable {
    private String name;
    private int id;
    private boolean include = true;

    public UserEntryHelper(SimpleUser simpleUser) {
        id = simpleUser.getId();
        name = simpleUser.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        this.include = include;
    }
}
