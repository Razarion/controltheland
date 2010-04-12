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
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * User: beat
 * Date: 27.03.2010
 * Time: 16:06:12
 */
public class HistoryDataProvider implements IDataProvider<DisplayHistoryElement> {
    private List<DisplayHistoryElement> history;
    private User user;
    private HistoryService historyService;
    private int entryCount;

    public HistoryDataProvider(User user, HistoryService historyService, int entryCount) {
        this.user = user;
        this.historyService = historyService;
        this.entryCount = entryCount;
    }

    @Override
    public Iterator<? extends DisplayHistoryElement> iterator(int first, int count) {
        return getHistory().subList(first, first + count).iterator();
    }

    @Override
    public int size() {
        return getHistory().size();
    }

    @Override
    public IModel<DisplayHistoryElement> model(DisplayHistoryElement displayHistoryElement) {
        return new Model<DisplayHistoryElement>(displayHistoryElement);
    }

    @Override
    public void detach() {
        history = null;
    }

    private List<DisplayHistoryElement> getHistory() {
        if (history == null) {
            history = historyService.getNewestHistoryElements(user, entryCount);
        }
        return history;
    }
}
