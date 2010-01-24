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

import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.widgetideas.graphics.client.Color;

/**
 * User: beat
 * Date: 22.01.2010
 * Time: 21:59:32
 */
public class SpeechBubble extends AbsolutePanel {
    public static final int SPACING = 2;
    public static final int CURVE_SIZE = 40;
    public static final int BUBLLE_BORDER_SIZE = 40;
    public static final int BEAK_LENGTH = 30;
    public static final int BEAK_WIDTH = 30;
    public static final int LINE_SIZE = 2;

    enum Direction {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    public SpeechBubble(int beakRelX, int beakRelY) {
        int scrrenWidth = TerrainView.getInstance().getViewWidth();
        int scrrenHeight = TerrainView.getInstance().getViewHeight();
        int htmlWidth = 200;
        int htmlHeight = 200;
        int bubbleWidth = htmlWidth + 2 * BUBLLE_BORDER_SIZE + 2 * LINE_SIZE;
        int bubbleHeight = htmlHeight + 2 * BUBLLE_BORDER_SIZE + 2 * LINE_SIZE;

        Direction direction = getBeakDirection(beakRelX, beakRelY, scrrenWidth, scrrenHeight, bubbleWidth, bubbleHeight);

        int totalBubbleWidth = bubbleWidth;
        int totalBubbleHeight = bubbleHeight;
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            totalBubbleWidth += BEAK_LENGTH;
        } else if (direction == Direction.TOP || direction == Direction.BOTTOM) {
            totalBubbleHeight += BEAK_LENGTH;
        }

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
        HTML content = new HTML("<h3>Hallo Du</h3> sdffsdaaf sdafsdaf sdfsdafasdf sdafsdafsdaf");
        content.getElement().getStyle().setZIndex(2);
        content.setPixelSize(htmlWidth, htmlHeight);
        switch (direction) {
            case BOTTOM:
                add(content, BUBLLE_BORDER_SIZE, BUBLLE_BORDER_SIZE);
                break;
            case LEFT:
                add(content, BUBLLE_BORDER_SIZE + BEAK_LENGTH, BUBLLE_BORDER_SIZE);
                break;
            case RIGHT:
                add(content, BUBLLE_BORDER_SIZE, BUBLLE_BORDER_SIZE);
                break;
            case TOP:
                add(content, BUBLLE_BORDER_SIZE, BUBLLE_BORDER_SIZE + BEAK_LENGTH);
                break;
        }

        buildBubble(LINE_SIZE, LINE_SIZE, totalBubbleWidth - LINE_SIZE, totalBubbleHeight - LINE_SIZE, beakOffset, direction);
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

    private Direction getBeakDirection(int beakRelX, int beakRelY, int scrrenWidth, int scrrenHeight, int bubblWidth, int bubblHeight) {
        Direction direction = null;
        if (beakRelX < (bubblWidth + BEAK_WIDTH) / 2) {
            direction = Direction.LEFT;
        } else if (beakRelX > scrrenWidth - (bubblWidth + BEAK_WIDTH) / 2) {
            direction = Direction.RIGHT;
        }
        if (beakRelY < bubblHeight) {
            if (direction == null) {
                direction = Direction.TOP;
            }
        } else if (beakRelY > scrrenHeight - bubblHeight) {
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

        ExtendedCanvas extendedCanvas = new ExtendedCanvas(right + LINE_SIZE, bottom + LINE_SIZE);
        extendedCanvas.getElement().getStyle().setZIndex(1);
        add(extendedCanvas, 0, 0);
        extendedCanvas.setLineWidth(LINE_SIZE);
        extendedCanvas.beginPath();
        extendedCanvas.moveTo(CURVE_SIZE + bodyLeft, bodyTop);
        extendedCanvas.quadraticCurveTo(bodyLeft, bodyTop, bodyLeft, CURVE_SIZE + bodyTop);
        leftBeakOrLine(extendedCanvas, direction == Direction.LEFT, left, beakOffset, bodyTop, bodyBottom, bodyLeft);
        extendedCanvas.quadraticCurveTo(bodyLeft, bodyBottom, CURVE_SIZE + bodyLeft, bodyBottom);
        bottomBeakOrLine(extendedCanvas, direction == Direction.BOTTOM, beakOffset, bottom, bodyLeft, bodyRight, bodyBottom);
        extendedCanvas.quadraticCurveTo(bodyRight, bodyBottom, bodyRight, bodyBottom - CURVE_SIZE);
        rightBeakOrLine(extendedCanvas, direction == Direction.RIGHT, right, beakOffset, bodyTop, bodyBottom, bodyRight);
        extendedCanvas.quadraticCurveTo(bodyRight, bodyTop, bodyRight - CURVE_SIZE, bodyTop);
        topBeakOrLine(extendedCanvas, direction == Direction.TOP, beakOffset, top, bodyLeft, bodyRight, bodyTop);

        extendedCanvas.stroke();
        extendedCanvas.setFillStyle(Color.RED);
        extendedCanvas.fill();
    }

    private void bottomBeakOrLine(ExtendedCanvas extendedCanvas, boolean makeBeak, int beakLeft, int beakTop, int bodyLeft, int bodyRight, int bodyBottom) {
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

    private void topBeakOrLine(ExtendedCanvas extendedCanvas, boolean makeBeak, int beakLeft, int beakTop, int bodyLeft, int bodyRight, int bodyTop) {
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

    private void leftBeakOrLine(ExtendedCanvas extendedCanvas, boolean makeBeak, int beakLeft, int beakTop, int bodyTop, int bodyBottom, int bodyLeft) {
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

    private void rightBeakOrLine(ExtendedCanvas extendedCanvas, boolean makeBeak, int beakLeft, int beakTop, int bodyTop, int bodyBottom, int bodyRight) {
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

}
