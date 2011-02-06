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

package com.btxtech.game.services.debug.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.debug.DebugService;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Oct 4, 2009
 * Time: 12:20:23 AM
 */
@Component("debugService")
public class DebugServiceImpl implements DebugService {
    private Frame frame;

    //@PostConstruct
    public void init() {
        frame = new Frame();
        frame.setSize(3200, 3200);
        frame.setVisible(true);
        frame.repaint();
    }

    @PreDestroy
    public void destroy() {
        if (frame != null) {
            frame.dispose();
        }
    }

    @Override
    public void onPositionChanged(SyncItem item, Index position) {
        Graphics graphics = frame.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillOval(position.getX(), position.getY(), 2, 2);
    }
}
