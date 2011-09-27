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

package com.btxtech.game.services.statistics;

import java.util.List;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 1:25:35 PM
 */
public interface StatisticsService {
    List<BaseStatisticsDTO> getBasesByUpTime(int count);

    List<BaseStatisticsDTO> getBasesByMoney(int count);

    List<BaseStatisticsDTO> getBasesBySize(int count);

    List<BaseStatisticsDTO> getBasesByKills(int count);

}