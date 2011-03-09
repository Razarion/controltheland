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
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.wicket.uiservices.CrudTableHelper;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 19.05.2010
 * Time: 22:13:43
 */
public class BotTable extends MgmtWebPage {
    @SpringBean
    private BotService botService;

    public BotTable() {
        add(new FeedbackPanel("msgs"));

        Form form = new Form("form");
        add(form);


         new CrudTableHelper<DbBotConfig>("bots", "save", "create", true, form, false) {

            @Override
            protected CrudServiceHelper<DbBotConfig> getCrudServiceHelper() {
                return botService.getDbBotConfigCrudServiceHelper();
            }

            @Override
            protected void onEditSubmit(DbBotConfig dbBotConfig) {
                setResponsePage(new BotEditor(dbBotConfig));
            }
        };
        
        form.add(new Button("activate") {

            @Override
            public void onSubmit() {
                botService.activate();
            }
        });

    }
}
