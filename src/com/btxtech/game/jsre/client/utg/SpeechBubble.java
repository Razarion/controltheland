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

package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.graphics.client.Color;

/**
 * User: beat
 * Date: 22.01.2010
 * Time: 21:59:32
 */
public class SpeechBubble extends AbsolutePanel {
    public static final int LINE_SIZE = 5;
    public static final int SPACING = 2;
    public static final int CURVE_SIZE = 20;
    public static final int HTML_OFFSET = CURVE_SIZE / 4 + LINE_SIZE;
    public static final int BEAK_LENGTH = 20;
    public static final int BEAK_WIDTH = 10;
    public static final String STOP_TEXT = "Stop tutorials. I know the game.";
    private ExtendedCanvas extendedCanvas;
    private boolean blink = false;
    private boolean preventScroll;

    enum Direction {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    public SpeechBubble(ClientSyncItemView item, String html, boolean preventScroll) {
        this.preventScroll = preventScroll;
        Index htmlSize = getHtmlSize(html);
        Direction direction = getBeakDirection(item.getRelativeMiddleX(), item.getRelativeMiddleY(), htmlSize.getX(), htmlSize.getY());
        int deltaX = (int) (item.getSyncItem().getItemType().getWidth() / 2 * 0.8);
        int deltaY = (int) (item.getSyncItem().getItemType().getHeight() / 2 * 0.8);

        switch (direction) {
            case BOTTOM:
                setup(item.getRelativeMiddleX(), item.getRelativeMiddleY() - deltaY, html, htmlSize.getX(), htmlSize.getY(), direction);
                break;
            case LEFT:
                setup(item.getRelativeMiddleX() + deltaX, item.getRelativeMiddleY(), html, htmlSize.getX(), htmlSize.getY(), direction);
                break;
            case RIGHT:
                setup(item.getRelativeMiddleX() - deltaX, item.getRelativeMiddleY(), html, htmlSize.getX(), htmlSize.getY(), direction);
                break;
            case TOP:
                setup(item.getRelativeMiddleX(), item.getRelativeMiddleY() + deltaY, html, htmlSize.getX(), htmlSize.getY(), direction);
                break;
        }
    }

    public SpeechBubble(int beakRelX, int beakRelY, String html, boolean preventScroll) {
        this.preventScroll = preventScroll;
        Index htmlSize = getHtmlSize(html);
        setup(beakRelX, beakRelY, html, htmlSize.getX(), htmlSize.getY(), null);
    }

    private Index getHtmlSize(String html) {
        html = html + "<hr>" + STOP_TEXT;
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("100%", "100%");
        MapWindow.getAbsolutePanel().add(absolutePanel, 0, 0);
        absolutePanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_HIDDEN);
        HTML content = new HTML(html);
        absolutePanel.add(content, 0, 0);
        Index size;
        if (content.getOffsetWidth() == 0 || content.getOffsetHeight() == 0) {
            GwtCommon.sendLogToServer("Can not determine HTML panel size: " + content.getOffsetWidth() + ":" + content.getOffsetHeight());
            size = new Index(200, 200);
        } else {
            // Try to make the content quadratic
            int area = content.getOffsetWidth() * content.getOffsetHeight();
            int length = (int) Math.sqrt(area);
            content.setHTML(html);
            absolutePanel.remove(content);
            absolutePanel.setPixelSize((int) (length * 1.5), length);
            absolutePanel.add(content, 0, 0);
            size = new Index(content.getOffsetWidth(), content.getOffsetHeight());
        }
        MapWindow.getAbsolutePanel().remove(absolutePanel);
        return size;
    }

    private void setup(int beakRelX, int beakRelY, String html, int htmlWidth, int htmlHeight, Direction direction) {
        if (direction == null) {
            direction = getBeakDirection(beakRelX, beakRelY, htmlWidth, htmlHeight);
        }

        int totalBubbleWidth = getBubbleSize(htmlWidth);
        int totalBubbleHeight = getBubbleSize(htmlHeight);

        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            totalBubbleWidth += BEAK_LENGTH;
        } else if (direction == Direction.TOP || direction == Direction.BOTTOM) {
            totalBubbleHeight += BEAK_LENGTH;
        }

        int scrrenWidth = TerrainView.getInstance().getViewWidth();
        int scrrenHeight = TerrainView.getInstance().getViewHeight();
        int beakOffset;
        int left;
        int top;
        switch (direction) {
            case BOTTOM:
                left = getBubbleLeft(beakRelX, scrrenWidth, totalBubbleWidth);
                top = beakRelY - totalBubbleHeight;
                beakOffset = beakRelX - left;
                break;
            case LEFT:
                left = beakRelX;
                top = getBubbleTop(beakRelY, scrrenHeight, totalBubbleHeight);
                beakOffset = beakRelY - top;
                break;
            case RIGHT:
                left = beakRelX - totalBubbleWidth;
                top = getBubbleTop(beakRelY, scrrenHeight, totalBubbleHeight);
                beakOffset = beakRelY - top;
                break;
            case TOP:
                left = getBubbleLeft(beakRelX, scrrenWidth, totalBubbleWidth);
                top = beakRelY;
                beakOffset = beakRelX - left;
                break;
            default:
                throw new IllegalArgumentException(this + " unsupported direction: " + direction);
        }


        setPixelSize(totalBubbleWidth, totalBubbleHeight);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_SPEECH_BUBBLE);
        MapWindow.getAbsolutePanel().add(this, left, top);
        // HTML content
        VerticalPanel verticalPanel = new VerticalPanel();
        HTML htmlContent = new HTML(html);
        verticalPanel.add(htmlContent);
        verticalPanel.add(new HTML("<hr>"));
        Anchor anchor = new Anchor(STOP_TEXT);
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                GwtCommon.preventDefault(event);
            }
        });
        verticalPanel.add(anchor);
        verticalPanel.getElement().getStyle().setZIndex(2);
        verticalPanel.setPixelSize(htmlWidth, htmlHeight);
        htmlContent.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                ClientUserTracker.getInstance().clickSpeechBubble();
            }
        });
        switch (direction) {
            case BOTTOM:
                add(verticalPanel, HTML_OFFSET, HTML_OFFSET);
                break;
            case LEFT:
                add(verticalPanel, HTML_OFFSET + BEAK_LENGTH, HTML_OFFSET);
                break;
            case RIGHT:
                add(verticalPanel, HTML_OFFSET, HTML_OFFSET);
                break;
            case TOP:
                add(verticalPanel, HTML_OFFSET, HTML_OFFSET + BEAK_LENGTH);
                break;
        }

        buildBubble(LINE_SIZE, LINE_SIZE, totalBubbleWidth - LINE_SIZE, totalBubbleHeight - LINE_SIZE, beakOffset, direction);
    }

    private int getBubbleSize(int htmlSize) {
        return htmlSize + 2 * HTML_OFFSET;
    }

    private int getBubbleTop(int beakRelY, int scrrenHeight, int bubblHeight) {
        int top;
        if (beakRelY < (bubblHeight + BEAK_WIDTH) / 2) {
            top = SPACING;
        } else if (beakRelY > scrrenHeight - (bubblHeight + BEAK_WIDTH) / 2) {
            top = scrrenHeight - SPACING - bubblHeight;
        } else {
            top = beakRelY - bubblHeight / 2;
        }
        return top;
    }

    private int getBubbleLeft(int beakRelX, int scrrenWidth, int bubblWidth) {
        int left;
        if (beakRelX < (bubblWidth + BEAK_WIDTH) / 2) {
            left = SPACING;
        } else if (beakRelX > scrrenWidth - (bubblWidth + BEAK_WIDTH) / 2) {
            left = scrrenWidth - SPACING - bubblWidth;
        } else {
            left = beakRelX - bubblWidth / 2;
        }
        return left;
    }

    private Direction getBeakDirection(int beakRelX, int beakRelY, int htmlWidth, int htmlHeight) {
        Direction direction = null;
        int bubblWidth = getBubbleSize(htmlWidth);
        int bubblHeight = getBubbleSize(htmlHeight);
        if (beakRelX < (bubblWidth + BEAK_WIDTH) / 2) {
            direction = Direction.LEFT;
        } else if (beakRelX > TerrainView.getInstance().getViewWidth() - (bubblWidth + BEAK_WIDTH) / 2) {
            direction = Direction.RIGHT;
        }
        if (beakRelY < bubblHeight) {
            if (direction == null) {
                direction = Direction.TOP;
            }
        } else if (beakRelY > TerrainView.getInstance().getViewHeight() - bubblHeight) {
            if (direction == null) {
                direction = Direction.BOTTOM;
            }
        }
        if (direction == null) {
            direction = Direction.BOTTOM;
        }
        return direction;
    }

    private void buildBubble(int left, int top, int right, int bottom, int beakOffset, Direction direction) {
        int bodyLeft = left;
        int bodyTop = top;
        int bodyRight = right;
        int bodyBottom = bottom;
        switch (direction) {
            case BOTTOM:
                bodyBottom -= BEAK_LENGTH;
                break;
            case LEFT:
                bodyLeft += BEAK_LENGTH;
                break;
            case RIGHT:
                bodyRight -= BEAK_LENGTH;
                break;
            case TOP:
                bodyTop += BEAK_LENGTH;
                break;
        }

        extendedCanvas = new ExtendedCanvas(right + LINE_SIZE, bottom + LINE_SIZE);
        extendedCanvas.getElement().getStyle().setZIndex(1);
        add(extendedCanvas, 0, 0);
        extendedCanvas.setLineWidth(LINE_SIZE);
        extendedCanvas.beginPath();
        extendedCanvas.moveTo(CURVE_SIZE + bodyLeft, bodyTop);
        extendedCanvas.quadraticCurveTo(bodyLeft, bodyTop, bodyLeft, CURVE_SIZE + bodyTop);
        leftBeakOrLine(direction == Direction.LEFT, left, beakOffset, bodyTop, bodyBottom, bodyLeft);
        extendedCanvas.quadraticCurveTo(bodyLeft, bodyBottom, CURVE_SIZE + bodyLeft, bodyBottom);
        bottomBeakOrLine(direction == Direction.BOTTOM, beakOffset, bottom, bodyLeft, bodyRight, bodyBottom);
        extendedCanvas.quadraticCurveTo(bodyRight, bodyBottom, bodyRight, bodyBottom - CURVE_SIZE);
        rightBeakOrLine(direction == Direction.RIGHT, right, beakOffset, bodyTop, bodyBottom, bodyRight);
        extendedCanvas.quadraticCurveTo(bodyRight, bodyTop, bodyRight - CURVE_SIZE, bodyTop);
        topBeakOrLine(direction == Direction.TOP, beakOffset, top, bodyLeft, bodyRight, bodyTop);

        extendedCanvas.stroke();
        extendedCanvas.setFillStyle(Color.WHITE);
        extendedCanvas.fill();
        extendedCanvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                ClientUserTracker.getInstance().clickSpeechBubble();
            }
        });
    }

    private void bottomBeakOrLine(boolean makeBeak, int beakLeft, int beakTop, int bodyLeft, int bodyRight, int bodyBottom) {
        if (!makeBeak) {
            extendedCanvas.lineTo(bodyRight - CURVE_SIZE, bodyBottom);
            return;
        }
        if (beakLeft < CURVE_SIZE + BEAK_WIDTH / 2 + bodyLeft) {
            // too much left
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(CURVE_SIZE + BEAK_WIDTH + bodyLeft, bodyBottom); // beak
            extendedCanvas.lineTo(bodyRight - CURVE_SIZE, bodyBottom);
        } else if (beakLeft + BEAK_WIDTH / 2 > bodyRight - CURVE_SIZE) {
            // too much right
            extendedCanvas.lineTo(bodyRight - CURVE_SIZE - BEAK_WIDTH, bodyBottom);
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyRight - CURVE_SIZE, bodyBottom); // beak
        } else {
            // line
            if (beakLeft > CURVE_SIZE + BEAK_WIDTH / 2 + bodyLeft) {
                extendedCanvas.lineTo(beakLeft - BEAK_WIDTH / 2, bodyBottom);
            }
            // beak
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(beakLeft + BEAK_WIDTH / 2, bodyBottom); // beak
            //line
            if (beakLeft + BEAK_WIDTH / 2 < bodyRight - CURVE_SIZE) {
                extendedCanvas.lineTo(bodyRight - CURVE_SIZE, bodyBottom);
            }
        }
    }

    private void topBeakOrLine(boolean makeBeak, int beakLeft, int beakTop, int bodyLeft, int bodyRight, int bodyTop) {
        if (!makeBeak) {
            extendedCanvas.lineTo(CURVE_SIZE + bodyLeft, bodyTop);
            return;
        }
        if (beakLeft < CURVE_SIZE + BEAK_WIDTH / 2 + bodyLeft) {
            // too much left
            extendedCanvas.lineTo(bodyLeft + CURVE_SIZE + BEAK_WIDTH, bodyTop);
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyLeft + CURVE_SIZE, bodyTop); // beak
        } else if (beakLeft + BEAK_WIDTH / 2 > bodyRight - CURVE_SIZE) {
            // too much right
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyRight - CURVE_SIZE - BEAK_WIDTH, bodyTop); // beak
            extendedCanvas.lineTo(bodyLeft + CURVE_SIZE, bodyTop);
        } else {
            //line
            if (beakLeft + BEAK_WIDTH / 2 < bodyRight - CURVE_SIZE) {
                extendedCanvas.lineTo(beakLeft + BEAK_WIDTH / 2, bodyTop);
            }
            // beak
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(beakLeft - BEAK_WIDTH / 2, bodyTop); // beak
            // line
            if (beakLeft > CURVE_SIZE + BEAK_WIDTH / 2 + bodyLeft) {
                extendedCanvas.lineTo(CURVE_SIZE + bodyLeft, bodyTop);
            }
        }
    }

    private void leftBeakOrLine(boolean makeBeak, int beakLeft, int beakTop, int bodyTop, int bodyBottom, int bodyLeft) {
        if (!makeBeak) {
            extendedCanvas.lineTo(bodyLeft, bodyBottom - CURVE_SIZE);
            return;
        }
        if (beakTop < CURVE_SIZE + BEAK_WIDTH / 2 + bodyTop) {
            // too much top
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyLeft, bodyTop + CURVE_SIZE + BEAK_WIDTH); // beak
            extendedCanvas.lineTo(bodyLeft, bodyBottom - CURVE_SIZE);
        } else if (beakTop + BEAK_WIDTH / 2 > bodyBottom - CURVE_SIZE) {
            // too much bottom
            extendedCanvas.lineTo(bodyLeft, bodyBottom - CURVE_SIZE - BEAK_WIDTH);
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyLeft, bodyBottom - CURVE_SIZE); // beak
        } else {
            //line
            if (beakTop > bodyTop + CURVE_SIZE + BEAK_WIDTH / 2) {
                extendedCanvas.lineTo(bodyLeft, beakTop - BEAK_WIDTH / 2);
            }
            // beak
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyLeft, beakTop + BEAK_WIDTH / 2); // beak
            // line
            if (beakTop < bodyBottom - CURVE_SIZE - BEAK_WIDTH / 2) {
                extendedCanvas.lineTo(bodyLeft, bodyBottom - CURVE_SIZE);
            }
        }
    }

    private void rightBeakOrLine(boolean makeBeak, int beakLeft, int beakTop, int bodyTop, int bodyBottom, int bodyRight) {
        if (!makeBeak) {
            extendedCanvas.lineTo(bodyRight, bodyTop + CURVE_SIZE);
            return;
        }
        if (beakTop < CURVE_SIZE + BEAK_WIDTH / 2 + bodyTop) {
            // too much top
            extendedCanvas.lineTo(bodyRight, bodyTop + CURVE_SIZE + BEAK_WIDTH);
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyRight, bodyTop + CURVE_SIZE); // beak
        } else if (beakTop + BEAK_WIDTH / 2 > bodyBottom - CURVE_SIZE) {
            // too much bottom
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyRight, bodyBottom - CURVE_SIZE - BEAK_WIDTH); // beak
            extendedCanvas.lineTo(bodyRight, bodyTop + CURVE_SIZE);
        } else {
            // line
            if (beakTop < bodyBottom - CURVE_SIZE - BEAK_WIDTH / 2) {
                extendedCanvas.lineTo(bodyRight, beakTop + BEAK_WIDTH / 2);
            }
            // beak
            extendedCanvas.lineTo(beakLeft, beakTop); // beak
            extendedCanvas.lineTo(bodyRight, beakTop - BEAK_WIDTH / 2); // beak
            //line
            if (beakTop > bodyTop + CURVE_SIZE + BEAK_WIDTH / 2) {
                extendedCanvas.lineTo(bodyRight, bodyTop + CURVE_SIZE);
            }
        }
    }

    public void close() {
        MapWindow.getAbsolutePanel().remove(this);
    }

    public void blink() {
        if (blink) {
            extendedCanvas.setFillStyle(Color.WHITE);
        } else {
            extendedCanvas.setFillStyle(Color.RED);
        }
        blink = !blink;
        extendedCanvas.fill();
    }

    public void blinkOff() {
        extendedCanvas.setFillStyle(Color.WHITE);
        blink = false;
        extendedCanvas.fill();
    }

    public boolean isPreventScroll() {
        return preventScroll;
    }
}
