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

package com.btxtech.game.wicket.pages.history;

import com.btxtech.game.services.history.DisplayHistoryElement;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.wicket.WebCommon;
import com.btxtech.game.wicket.pages.BorderPanel;
import java.text.SimpleDateFormat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.03.2010
 * Time: 14:04:57
 */
public class HistoryPanel extends BorderPanel {
    public static final int ENTRY_COUNT = 20;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebCommon.DATE_TIME_FORMAT_STRING);
    @SpringBean
    private HistoryService historyService;

    public HistoryPanel(String id, User user) {
        super(id);
        DataView<DisplayHistoryElement> tileList = new DataView<DisplayHistoryElement>("table", new HistoryDataProvider(user, historyService, ENTRY_COUNT)) {

            @Override
            protected void populateItem(Item<DisplayHistoryElement> baseItem) {
                baseItem.add(new Label("timeStamp", simpleDateFormat.format(baseItem.getModelObject().getTimeStamp())));
                baseItem.add(new Label("message", baseItem.getModelObject().getMessage()));
            }
        };
        add(tileList);
    }
}
