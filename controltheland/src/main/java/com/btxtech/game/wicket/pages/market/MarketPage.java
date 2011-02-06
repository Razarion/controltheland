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

package com.btxtech.game.wicket.pages.market;

import com.btxtech.game.services.market.MarketCategory;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import com.btxtech.game.wicket.pages.user.NewUser;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 7:27:03 PM
 */
public class MarketPage extends BasePage {
    @SpringBean
    private ServerMarketService serverMarketService;

    public MarketPage() {
        add(new Link("createAccountLink") {
            @Override
            public void onClick() {
                setResponsePage(NewUser.class);
            }
        });

        add(new Label("xp", new IModel<String>() {
            @Override
            public String getObject() {
                return Integer.toString(serverMarketService.getXp());
            }

            @Override
            public void setObject(String s) {
                // Ignore
            }

            @Override
            public void detach() {
                // Ignore
            }
        }));

        add(new ListView<MarketCategory>("categories", new IModel<List<MarketCategory>>() {
            private List<MarketCategory> marketCategories;

            @Override
            public List<MarketCategory> getObject() {
                if (marketCategories == null) {
                    marketCategories = serverMarketService.getUsedMarketCategories();
                }
                return marketCategories;
            }

            @Override
            public void setObject(List<MarketCategory> object) {
                // Ignore
            }

            @Override
            public void detach() {
                marketCategories = null;
            }
        }) {
            @Override
            protected void populateItem(ListItem<MarketCategory> marketCategoryListItem) {
                marketCategoryListItem.add(new MarketCategoryPanel("category", marketCategoryListItem.getModelObject()));
            }
        });
    }

}
