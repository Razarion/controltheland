package com.btxtech.game.services.cms.layout;

import com.btxtech.game.wicket.pages.cms.content.plugin.PluginEnum;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: beat
 * Date: 01.07.2011
 * Time: 12:06:45
 */
@Entity
@DiscriminatorValue("CONTENT_PLUGIN")
public class DbContentPlugin extends DbContent {
    private PluginEnum pluginEnum;

    public PluginEnum getPluginEnum() {
        return pluginEnum;
    }

    public void setPluginEnum(PluginEnum pluginEnum) {
        this.pluginEnum = pluginEnum;
    }
}
