/*
 * Copyright (c) 2010.
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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.uiservices.ListProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 19.05.2010
 * Time: 22:13:43
 */
public class BotEditor extends WebPage {
    @SpringBean
    private BotService botService;
    @SpringBean
    private UserService userService;

    public BotEditor() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);

        final ListProvider<DbBotConfig> botConfigProvider = new ListProvider<DbBotConfig>() {
            @Override
            protected List<DbBotConfig> createList() {
                return new ArrayList<DbBotConfig>(botService.getDbBotConfigs());
            }
        };
        form.add(new DataView<DbBotConfig>("bots", botConfigProvider) {
            @Override
            protected void populateItem(final Item<DbBotConfig> dbBotConfigItem) {
                dbBotConfigItem.add(new TextField<String>("userName", new IModel<String>() {

                    @Override
                    public String getObject() {
                        if (dbBotConfigItem.getModelObject().getUser() != null) {
                            return dbBotConfigItem.getModelObject().getUser().getName();
                        }
                        return null;
                    }

                    @Override
                    public void setObject(String userName) {
                        if (userName != null) {
                            User user = userService.getUser(userName);
                            if (user != null) {
                                dbBotConfigItem.getModelObject().setUser(user);
                            } else {
                                error("No such user: " + userName);
                            }
                        } else {
                            dbBotConfigItem.getModelObject().setUser(null);
                        }
                    }

                    @Override
                    public void detach() {
                        //Ignore
                    }
                }));
                dbBotConfigItem.add(new TextField<Integer>("actionDelay"));
                dbBotConfigItem.add(new Button("delete") {

                    @Override
                    public void onSubmit() {
                        botService.removeDbBotConfig(dbBotConfigItem.getModelObject());
                    }
                });

            }
        });

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                botService.saveDbBotConfig(botConfigProvider.getLastModifiedList());
            }
        });
        form.add(new Button("add") {

            @Override
            public void onSubmit() {
                botService.addDbBotConfig();
            }
        });

    }
}
