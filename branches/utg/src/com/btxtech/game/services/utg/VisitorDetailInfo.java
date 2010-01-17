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

package com.btxtech.game.services.utg;

import java.util.List;
import java.util.ArrayList;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 12:01:19
 */
public class VisitorDetailInfo {
    private UserDetails userDetails;
    private List<GameTrackingInfo> gameTrackingInfos = new ArrayList<GameTrackingInfo>();

    public VisitorDetailInfo(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public List<GameTrackingInfo> getGameTrackingInfos() {
        return gameTrackingInfos;
    }

    public void setGameTrackingInfos(List<GameTrackingInfo> gameTrackingInfos) {
        this.gameTrackingInfos = gameTrackingInfos;
    }
}
