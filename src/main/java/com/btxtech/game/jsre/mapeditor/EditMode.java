package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

/**
 * User: beat
 * Date: 17.09.12
 * Time: 21:27
 */
public class EditMode {
    private static final int RESIZE_CURSOR_SPACE = 15;
    private boolean nr;
    private boolean er;
    private boolean sr;
    private boolean wr;

    public EditMode() {
    }

    public EditMode(Index absoluteMouse, Rectangle absoluteRectangle) {
        int inMarkerX = absoluteMouse.getX() - absoluteRectangle.getX();
        int inMarkerY = absoluteMouse.getY() - absoluteRectangle.getY();
        if (inMarkerY >= 0 && inMarkerY <= RESIZE_CURSOR_SPACE) {
            nr = true;
        }
        if (inMarkerX >= absoluteRectangle.getWidth() - RESIZE_CURSOR_SPACE && inMarkerX <= absoluteRectangle.getWidth()) {
            er = true;
        }
        if (inMarkerY >= absoluteRectangle.getHeight() - RESIZE_CURSOR_SPACE && inMarkerY <= absoluteRectangle.getHeight()) {
            sr = true;
        }
        if (inMarkerX >= 0 && inMarkerX <= RESIZE_CURSOR_SPACE) {
            wr = true;
        }
    }

    public boolean isNr() {
        return nr;
    }

    public boolean isEr() {
        return er;
    }

    public boolean isSr() {
        return sr;
    }

    public boolean isWr() {
        return wr;
    }
}
