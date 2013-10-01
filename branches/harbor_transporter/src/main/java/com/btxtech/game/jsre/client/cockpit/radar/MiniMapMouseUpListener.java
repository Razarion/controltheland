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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.google.gwt.event.dom.client.MouseUpEvent;

/**
 * User: beat
 * Date: 24.05.2010
 * Time: 13:41:35
 */
public interface MiniMapMouseUpListener {
    void onMouseUp(int absX, int absY, MouseUpEvent event);
}