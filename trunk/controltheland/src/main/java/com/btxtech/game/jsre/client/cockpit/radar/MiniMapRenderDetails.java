package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.common.Rectangle;

/**
 * User: beat
 * Date: 24.09.12
 * Time: 13:41
 */
public class MiniMapRenderDetails {
    private static final int MAX_NUMBER_PIECES = 3000;
    private boolean drawImages;
    private int tileIncrease;

    public MiniMapRenderDetails(Rectangle tileRect) {
        int area = tileRect.getArea();
        if (area < MAX_NUMBER_PIECES) {
            drawImages = true;
            tileIncrease = 1;
        } else {
            drawImages = false;
            tileIncrease = Math.max(1, (int) ((double) area / (double) MAX_NUMBER_PIECES / 2.0));
        }
    }

    public boolean isDrawImages() {
        return drawImages;
    }

    public int getTileIncrease() {
        return tileIncrease;
    }
}
