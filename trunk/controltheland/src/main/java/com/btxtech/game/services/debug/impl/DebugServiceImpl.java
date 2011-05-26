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
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.debug.DebugService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: Oct 4, 2009
 * Time: 12:20:23 AM
 */
@Component("debugService")
public class DebugServiceImpl implements DebugService {
    private Frame frame;
    private final Map<Rectangle, Color> rectangleColorMap = new HashMap<Rectangle, Color>();

    //@PostConstruct
    public void init() {
        frame = new Frame() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                synchronized (rectangleColorMap) {
                    privatePaintRectangles(g);
                }
            }
        };
        frame.setSize(3200, 3200);
        frame.setVisible(true);
        frame.repaint();
    }

    private void privatePaintRectangles(Graphics graphics) {
        for (Map.Entry<Rectangle, Color> entry : rectangleColorMap.entrySet()) {
            graphics.setColor(entry.getValue());
            Rectangle rectangle = entry.getKey();
            graphics.fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        }
    }

    @PreDestroy
    public void destroy() {
        if (frame != null) {
            frame.dispose();
        }
    }

    @Override
    public void onPositionChanged(SyncItem item, Index position) {
        // See drawRectangle
        // Graphics graphics = frame.getGraphics();
        // graphics.setColor(Color.BLACK);
        // graphics.fillOval(position.getX(), position.getY(), 2, 2);
    }

    @Override
    public void drawRectangle(Rectangle rectangle, Color color) {
        synchronized (rectangleColorMap) {
            rectangleColorMap.put(rectangle, color);
        }
        frame.repaint();
    }

    @Override
    public void drawRectangles(Collection<Rectangle> rectangles) {
        for (Rectangle rectangle : rectangles) {
            synchronized (rectangleColorMap) {
                rectangleColorMap.put(rectangle, Color.BLACK);
            }
        }
        frame.repaint();
    }
}
