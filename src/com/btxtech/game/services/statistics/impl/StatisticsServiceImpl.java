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

package com.btxtech.game.services.statistics.impl;

import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.statistics.BaseStatisticsDTO;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.wicket.WebCommon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 1:25:52 PM
 */
@Component("statisticsService")
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private BaseService baseService;

    @Override
    public List<BaseStatisticsDTO> getBasesByUpTime(int count) {
        List<Base> bases = baseService.getBases();
        Collections.sort(bases, new Comparator<Base>() {
            @Override
            public int compare(Base b1, Base b2) {
                if (b1.getUptime() < b2.getUptime()) {
                    return 1;
                } else if (b1.getUptime() > b2.getUptime()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        if (bases.size() > count && count != 0) {
            bases = bases.subList(0, count);
        }

        ArrayList<BaseStatisticsDTO> dtos = new ArrayList<BaseStatisticsDTO>();
        int rank = 1;
        for (Base base : bases) {
            BaseStatisticsDTO dto = new BaseStatisticsDTO(rank, base, WebCommon.formatDuration(base.getUptime()));
            dtos.add(dto);
            rank++;
        }
        return dtos;
    }

    @Override
    public List<BaseStatisticsDTO> getBasesByMoney(int count) {
        List<Base> bases = baseService.getBases();
        Collections.sort(bases, new Comparator<Base>() {
            @Override
            public int compare(Base b1, Base b2) {
                if (b1.getAccountBalance() < b2.getAccountBalance()) {
                    return 1;
                } else if (b1.getAccountBalance() > b2.getAccountBalance()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        if (bases.size() > count && count != 0) {
            bases = bases.subList(0, count);
        }
        ArrayList<BaseStatisticsDTO> dtos = new ArrayList<BaseStatisticsDTO>();
        int rank = 1;
        for (Base base : bases) {
            BaseStatisticsDTO dto = new BaseStatisticsDTO(rank, base, "$" + Integer.toString(base.getAccountBalance()));
            dtos.add(dto);
            rank++;
        }

        return dtos;
    }

    @Override
    public List<BaseStatisticsDTO> getBasesBySize(int count) {
        List<Base> bases = baseService.getBases();
        Collections.sort(bases, new Comparator<Base>() {
            @Override
            public int compare(Base b1, Base b2) {
                if (b1.getItems().size() < b2.getItems().size()) {
                    return 1;
                } else if (b1.getItems().size() > b2.getItems().size()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        if (bases.size() > count && count != 0) {
            bases = bases.subList(0, count);
        }
        ArrayList<BaseStatisticsDTO> dtos = new ArrayList<BaseStatisticsDTO>();
        int rank = 1;
        for (Base base : bases) {
            BaseStatisticsDTO dto = new BaseStatisticsDTO(rank, base, Integer.toString(base.getItems().size()));
            dtos.add(dto);
            rank++;
        }

        return dtos;
    }

    @Override
    public List<BaseStatisticsDTO> getBasesByKills(int count) {
       List<Base> bases = baseService.getBases();
        Collections.sort(bases, new Comparator<Base>() {
            @Override
            public int compare(Base b1, Base b2) {
                if (b1.getKills() < b2.getKills()) {
                    return 1;
                } else if (b1.getKills() > b2.getKills()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        if (bases.size() > count && count != 0) {
            bases = bases.subList(0, count);
        }
        ArrayList<BaseStatisticsDTO> dtos = new ArrayList<BaseStatisticsDTO>();
        int rank = 1;
        for (Base base : bases) {
            BaseStatisticsDTO dto = new BaseStatisticsDTO(rank, base, Integer.toString(base.getKills()));
            dtos.add(dto);
            rank++;
        }

        return dtos;
    }
}
