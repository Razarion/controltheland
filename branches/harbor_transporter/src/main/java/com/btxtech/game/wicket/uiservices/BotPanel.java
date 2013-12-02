/*
 * Copyright (c) 2011.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.RuServiceHelper;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 17.11.2013
 * Time: 18:44:15
 */
public class BotPanel extends Panel {
    @SpringBean
    private RuServiceHelper<DbBotConfig> ruServiceHelper;

    public BotPanel(String id) {
        super(id);
        add(new TextField<>("botId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbBotConfig dbBotConfig = (DbBotConfig) getDefaultModelObject();
                if (dbBotConfig != null) {
                    return dbBotConfig.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                if (integer != null) {
                    DbBotConfig dbBotConfig = ruServiceHelper.readDbChild(integer, DbBotConfig.class);
                    if (dbBotConfig == null) {
                        error("DbBotConfig does not exist: " + integer);
                        return;
                    }
                    setDefaultModelObject(dbBotConfig);
                } else {
                    setDefaultModelObject(null);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new Label("botName", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                DbBotConfig dbBotConfig = (DbBotConfig) getDefaultModelObject();
                if (dbBotConfig != null) {
                    return dbBotConfig.getName();
                } else {
                    return null;
                }
            }
        }));
    }
}
