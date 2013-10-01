package com.btxtech.game.jsre.client.dialogs.incentive;

import java.io.Serializable;

/**
 * User: beat
 * Date: 23.07.13
 * Time: 14:37
 */
public class FriendInvitationBonus implements Serializable {
    private String userName;
    private int level;
    private int crystalBonus;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCrystalBonus() {
        return crystalBonus;
    }

    public void setCrystalBonus(int crystalBonus) {
        this.crystalBonus = crystalBonus;
    }
}
