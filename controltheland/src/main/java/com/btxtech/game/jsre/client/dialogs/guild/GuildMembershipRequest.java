package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.common.info.DetailedUser;

import java.io.Serializable;

/**
 * User: beat
 * Date: 13.06.13
 * Time: 14:22
 */
public class GuildMembershipRequest implements Serializable {
    private DetailedUser detailedUser;
    private String text;

    /**
     * Used by GWT
     */
    GuildMembershipRequest() {
    }

    public GuildMembershipRequest(DetailedUser detailedUser, String text) {
        this.detailedUser = detailedUser;
        this.text = text;
    }

    public DetailedUser getDetailedUser() {
        return detailedUser;
    }

    public String getText() {
        return text;
    }
}
