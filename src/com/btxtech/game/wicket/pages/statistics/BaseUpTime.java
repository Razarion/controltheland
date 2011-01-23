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

package com.btxtech.game.wicket.pages.statistics;

import com.btxtech.game.services.statistics.BaseStatisticsDTO;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.wicket.uiservices.ColorField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 2:18:47 PM
 */
public class BaseUpTime extends Panel {
    @SpringBean
    private StatisticsService statisticsService;

    public BaseUpTime(int count) {
        super("baseUpTime");
        DataView<BaseStatisticsDTO> tileList = new DataView<BaseStatisticsDTO>("table", new BaseDataProvider(statisticsService, count, BaseDataProvider.Type.UP_TIME)) {

            @Override
            protected void populateItem(Item<BaseStatisticsDTO> baseItem) {
                baseItem.add(new Label("rank", baseItem.getModelObject().getRankAsString()));
                baseItem.add(new Label("base", baseItem.getModelObject().getBaseName()));
                baseItem.add(new Label("duration", baseItem.getModelObject().getData()));
                baseItem.add(ColorField.create("color", baseItem.getModelObject().getColor()));
            }
        };
        add(tileList);
        BookmarkablePageLink<StatisticsPage> link = new BookmarkablePageLink<StatisticsPage>("link", StatisticsPage.class);
        link.setVisible(count != 0);
        add(link);
    }

}
