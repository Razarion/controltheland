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

package com.btxtech.game.jsre.playback;

import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;

/**
 * User: beat
 * Date: 22.12.2010
 * Time: 14:35:33
 */
public enum PlaybackStartupSeq implements StartupSeq {
    COLD_PLAYBACK {
        @Override
        public StartupTaskEnum[] getAbstractStartupTaskEnum() {
            return ColdPlaybackStartupTaskEnum.values();
        }
        @Override
        public boolean isCold() {
            return true;
        }
    };


    @Override
    public boolean isBackEndMode() {
        return true;
    }
}
