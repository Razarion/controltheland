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
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * User: beat
 * Date: Sep 20, 2009
 * Time: 11:24:52 AM
 */
public class BaseDataProvider implements IDataProvider<BaseStatisticsDTO> {
    public enum Type {
        MONEY,
        SIZE,
        UP_TIME,
        KILLS
    }

    private int count;
    private Type type;
    private StatisticsService statisticsService;

    public BaseDataProvider(StatisticsService statisticsService, int count, Type type) {
        this.statisticsService = statisticsService;
        this.count = count;
        this.type = type;
    }

    @Override
    public Iterator<? extends BaseStatisticsDTO> iterator(int first, int count) {
        List<BaseStatisticsDTO> bases = getStatistic();
        if (first != 0 || count != bases.size()) {
            throw new IllegalArgumentException();
        }
        return bases.iterator();
    }

    @Override
    public int size() {
        return getStatistic().size();
    }

    @Override
    public IModel<BaseStatisticsDTO> model(BaseStatisticsDTO statistics) {
         return new Model<BaseStatisticsDTO>(statistics);
    }

    @Override
    public void detach() {
    }

    private List<BaseStatisticsDTO> getStatistic() {
        switch (type) {
            case MONEY:
                return statisticsService.getBasesByMoney(count);
            case SIZE:
                return statisticsService.getBasesBySize(count);
            case UP_TIME:
                return statisticsService.getBasesByUpTime(count);
            case KILLS:
                return statisticsService.getBasesByKills(count);
            default:
                throw new IllegalArgumentException("Unkwno type: " + type);
        }

    }
}