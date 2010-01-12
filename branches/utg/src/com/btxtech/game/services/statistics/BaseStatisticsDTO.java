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

import com.btxtech.game.services.base.Base;
import java.io.Serializable;

/**
 * User: beat
 * Date: Sep 20, 2009
 * Time: 11:06:22 AM
 */
public class BaseStatisticsDTO implements Serializable {
    private int rank;
    private String userName;
    private String baseName;
    private String data;
    private String color;

    public BaseStatisticsDTO(int rank, Base base, String data) {
        this.rank = rank;
        this.color = base.getBaseColor().getHtmlColor();
        if (base.getUser() != null) {
            userName = base.getUser().getName();
        } else {
            userName = "";
        }
        baseName = base.getName();
        this.data = data;
    }

    public int getRank() {
        return rank;
    }

    public String getUserName() {
        return userName;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getData() {
        return data;
    }

    public String getRankAsString() {
        return Integer.toString(rank);
    }

    public String getColor() {
        return color;
    }
}


