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

import com.btxtech.game.jsre.client.common.Arc;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Line;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.formation.Segment;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.services.debug.DebugService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private static final int WIDTH = 4200;
    private static final int HEIGHT = 4200;
    private static final int GRID = 100;
    private Panel panel;
    private Frame frame;
    private ScrollPane scroll;
    private final Map<Rectangle, Color> rectangleColorMap = new HashMap<Rectangle, Color>();
    private final Map<Line, Color> lineColorMap = new HashMap<Line, Color>();
    private final Map<SyncItemArea, Color> syncItemAreaColorMap = new HashMap<SyncItemArea, Color>();
    private final Map<Index, Color> indexColorMap = new HashMap<Index, Color>();
    private final Map<Arc, Color> arcColorMap = new HashMap<Arc, Color>();
    private Thread blockedThread;
    private Label mousePosition;

    //@PostConstruct
    public void init() {
        frame = new Frame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (blockedThread != null) {
                    blockedThread.interrupt();
                }
            }
        });
        Panel container = new Panel();
        container.setLayout(new BorderLayout());

        Panel info = new Panel();
        mousePosition = new Label();
        info.add(mousePosition);
        container.add(info, BorderLayout.NORTH);

        scroll = new ScrollPane();
        panel = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                synchronized (rectangleColorMap) {
                    privatePaintGrid(g);
                    privatePaintRectangles(g);
                    privatePaintLines(g);
                    privatePaintArcs(g);
                    privatePaintSyncItemArea(g);
                    privatePaintIndexes(g);
                }
            }
        };
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Ignore
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition.setText("Mouse Position: " + (int) e.getPoint().getX() + " " + (int) e.getPoint().getY());
            }
        });
        scroll.add(panel);

        frame.setSize(900, 700);
        container.add(scroll, BorderLayout.CENTER);
        frame.add(container);
        frame.setVisible(true);
    }

    @PreDestroy
    public void destroy() {
        if (frame != null) {
            frame.dispose();
        }
    }

    private void update() {
        scroll.validate();
        panel.validate();
        frame.validate();
        panel.repaint();
        frame.repaint();
        scroll.repaint();
    }

    private void privatePaintGrid(Graphics graphics) {
        graphics.setColor(Color.LIGHT_GRAY);
        for (int i = GRID; i < WIDTH; i += GRID) {
            graphics.drawLine(i, 0, i, HEIGHT);
        }
        for (int i = GRID; i < HEIGHT; i += GRID) {
            graphics.drawLine(0, i, HEIGHT, i);
        }
    }

    private void privatePaintIndexes(Graphics graphics) {
        for (Map.Entry<Index, Color> entry : indexColorMap.entrySet()) {
            graphics.setColor(entry.getValue());
            Index index = entry.getKey();
            graphics.fillArc(index.getX() - 4, index.getY() - 4, 8, 8, 0, 360);
        }
    }

    private void privatePaintSyncItemArea(Graphics graphics) {
        for (Map.Entry<SyncItemArea, Color> entry : syncItemAreaColorMap.entrySet()) {
            graphics.setColor(entry.getValue());
            SyncItemArea syncItemArea = entry.getKey();
            Index index1 = syncItemArea.getCorner1();
            Index index2 = syncItemArea.getCorner2();
            graphics.drawLine(index1.getX(), index1.getY(), index2.getX(), index2.getY());
            index1 = syncItemArea.getCorner2();
            index2 = syncItemArea.getCorner3();
            graphics.drawLine(index1.getX(), index1.getY(), index2.getX(), index2.getY());
            index1 = syncItemArea.getCorner3();
            index2 = syncItemArea.getCorner4();
            graphics.drawLine(index1.getX(), index1.getY(), index2.getX(), index2.getY());
            index1 = syncItemArea.getCorner4();
            index2 = syncItemArea.getCorner1();
            graphics.drawLine(index1.getX(), index1.getY(), index2.getX(), index2.getY());
        }
    }

    private void privatePaintRectangles(Graphics graphics) {
        for (Map.Entry<Rectangle, Color> entry : rectangleColorMap.entrySet()) {
            graphics.setColor(entry.getValue());
            Rectangle rectangle = entry.getKey();
            graphics.drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        }
    }

    private void privatePaintLines(Graphics graphics) {
        for (Map.Entry<Line, Color> entry : lineColorMap.entrySet()) {
            graphics.setColor(entry.getValue());
            Line line = entry.getKey();
            graphics.drawLine(line.getPoint1().getX(), line.getPoint1().getY(), line.getPoint2().getX(), line.getPoint2().getY());
        }
    }

    private void privatePaintArcs(Graphics graphics) {
        for (Map.Entry<Arc, Color> entry : arcColorMap.entrySet()) {
            graphics.setColor(entry.getValue());
            Arc arc = entry.getKey();
            graphics.drawArc(arc.getUpperLeftCorner().getX(),
                    arc.getUpperLeftCorner().getY(),
                    (int) Math.round(2.0 * arc.getRadius()),
                    (int) Math.round(2.0 * arc.getRadius()),
                    (int) MathHelper.radToGrad(arc.getMiddle().getAngleToNord(arc.getStart()) + MathHelper.QUARTER_RADIANT),
                    (int) MathHelper.radToGrad(arc.getAngel()));
        }
    }

    @Override
    public void waitForClose() {
        try {
            blockedThread = Thread.currentThread();
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // Ignore
        }
        blockedThread = null;
    }

    @Override
    public void drawRectangle(Rectangle rectangle, Color color) {
        synchronized (rectangleColorMap) {
            rectangleColorMap.put(rectangle, color);
        }
        update();
    }

    @Override
    public void drawRectangles(Collection<Rectangle> rectangles) {
        for (Rectangle rectangle : rectangles) {
            synchronized (rectangleColorMap) {
                rectangleColorMap.put(rectangle, Color.BLACK);
            }
        }
        update();
    }

    @Override
    public void drawLine(Line line, Color color) {
        synchronized (rectangleColorMap) {
            lineColorMap.put(line, color);
        }
        update();
    }

    @Override
    public void drawLines(Collection<Line> lines) {
        for (Line line : lines) {
            synchronized (rectangleColorMap) {
                lineColorMap.put(line, Color.BLUE);
            }
        }
        update();
    }

    @Override
    public void drawArc(Arc arc, Color color) {
        synchronized (rectangleColorMap) {
            arcColorMap.put(arc, color);
        }
        update();
    }


    @Override
    public void drawSegments(Collection<Segment> segments) {
        for (Segment segment : segments) {
            synchronized (rectangleColorMap) {
                if (segment instanceof Line) {
                    lineColorMap.put((Line) segment, Color.BLUE);
                } else if (segment instanceof Arc) {
                    arcColorMap.put((Arc) segment, Color.BLUE);
                } else {
                    throw new IllegalArgumentException("Unknown segment type: " + segment);
                }
            }
        }
        update();
    }

    @Override
    public void drawSyncItemArea(SyncItemArea syncItemArea, Color color) {
        synchronized (rectangleColorMap) {
            syncItemAreaColorMap.put(syncItemArea, color);
        }
        update();
    }

    @Override
    public void drawPosition(Index position, Color color) {
        synchronized (rectangleColorMap) {
            indexColorMap.put(position, color);
        }
        update();
    }

    @Override
    public void drawPosition(Index position) {
        drawPosition(position, Color.RED);
    }
}
