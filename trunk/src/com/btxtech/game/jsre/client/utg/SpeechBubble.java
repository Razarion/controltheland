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
    public static final int LINE_WIDTH = 2;

    enum Direction {
        TOP,
        LEFT,
        BOTTOM,
        RIGHT
    }

    public SpeechBubble(int beakRelX, int beakRelY) {
        int scrrenWidth = TerrainView.getInstance().getViewWidth();
        int htmlWidth = 200;
        int htmlHeight = 200;
        int bubblWidth = htmlWidth + 2 * BUBLLE_BORDER_SIZE + 2 * LINE_WIDTH;
        int bubblHeight = htmlHeight + 2 * BUBLLE_BORDER_SIZE + 2 * LINE_WIDTH + BEAK_LENGTH;

        Direction direction = Direction.TOP;
        int left;
        int beakOffset;
        if (beakRelX < SPACING + CURVE_SIZE + BEAK_WIDTH / 2) {
            left = SPACING;
            beakOffset = beakRelX;
        } else if (beakRelX > scrrenWidth - bubblWidth - SPACING) {
            left = scrrenWidth - SPACING - bubblWidth;
            beakOffset = beakRelX - left;
        } else {
            beakOffset = BEAK_WIDTH + CURVE_SIZE;
            left = beakRelX - BEAK_WIDTH - CURVE_SIZE;
        }


        int top = beakRelY;

        ////
        setPixelSize(bubblWidth, bubblHeight);
        getElement().getStyle().setBackgroundColor("#888888");
        getElement().getStyle().setZIndex(Constants.Z_INDEX_SPEECH_BUBBLE);
        MapWindow.getAbsolutePanel().add(this, left, top);
        // HTML content
        HTML content = new HTML("<h3>Hallo Du</h3> sdffsdaaf sdafsdaf sdfsdafasdf sdafsdafsdaf");
        content.getElement().getStyle().setZIndex(2);
        content.setPixelSize(htmlWidth, htmlHeight);
        add(content, BUBLLE_BORDER_SIZE, BUBLLE_BORDER_SIZE + BEAK_LENGTH);
        ////
        buildBubble(LINE_WIDTH, LINE_WIDTH, bubblWidth - LINE_WIDTH, bubblHeight - LINE_WIDTH, beakOffset, direction);

        //buildBubble(2, 2, 198, 198, 199);
        //buildBubble(2, 2, 198, 198, 144);

        //buildBubble(2, 2, 198, 198, 1);
        //buildBubble(2, 2, 198, 198, 56);

        //buildBubble(2, 2, 198, 198, 143);
        //  buildBubble(2, 2, 198, 198, 57);
        //buildBubble(2, 2, 198, 198, 100);
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

        ExtendedCanvas extendedCanvas = new ExtendedCanvas(right + LINE_WIDTH, bodyBottom + LINE_WIDTH);
        extendedCanvas.getElement().getStyle().setZIndex(1);
        add(extendedCanvas, 0, 0);
        extendedCanvas.setLineWidth(LINE_WIDTH);
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
