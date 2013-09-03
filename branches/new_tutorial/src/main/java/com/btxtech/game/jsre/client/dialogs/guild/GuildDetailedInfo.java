package com.btxtech.game.jsre.client.dialogs.guild;

/**
 * User: beat
 * Date: 03.06.13
 * Time: 13:20
 */
public class GuildDetailedInfo extends GuildInfo {
    private int members;

    /**
     * Used by GWT
     */
    GuildDetailedInfo() {
    }

    public GuildDetailedInfo(int id, String name, String text, int members) {
        super(id, name, text);
        this.members = members;
    }

    public int getMembers() {
        return members;
    }
}
