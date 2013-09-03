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

package com.btxtech.game.services.debug;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;

import java.awt.*;
import java.util.Collection;

/**
 * User: beat
 * Date: Oct 4, 2009
 * Time: 12:19:55 AM
 */
public interface DebugService {
    void drawRectangle(Rectangle rectangle, Color color);

    void drawRectangles(Collection<Rectangle> rectangles);

    void drawLine(Line line, Color color);

    void drawLines(Collection<Line> lines);
    
    void drawSyncItemArea(SyncItemArea syncItemArea, Color color);

    void drawPosition(Index position, Color color);

    void drawPosition(Index position);

    void waitForClose();

    void displayOverlapping(java.util.List<SyncBaseItem> attackers);
}
