package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.common.info.SimpleGuild;

public class GuildInfo extends SimpleGuild {
    private String text;

    /**
     * Used by GWT
     */
    GuildInfo() {
    }

    public GuildInfo(int id, String name, String text) {
        super(id, name);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
