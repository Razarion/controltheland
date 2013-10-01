package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;

/**
 * User: beat
 * Date: 18.09.12
 * Time: 20:12
 */
public class TerrainEditorSelection {
    private Index start;
    private Rectangle relativeRectangle;
    private Rectangle absoluteRectangle;

    public void setStart(Index start) {
        this.start = start;
    }

    public void setEnd(Index end, Rectangle viewRectangle) {
        absoluteRectangle = Rectangle.generateRectangleFromAnyPoints(start, end);
        relativeRectangle = new Rectangle(absoluteRectangle.getStart().sub(viewRectangle.getStart()), absoluteRectangle.getEnd().sub(viewRectangle.getStart()));
    }

    public Rectangle getRelativeRectangle() {
        return relativeRectangle;
    }

    public Rectangle getAbsoluteRectangle() {
        return absoluteRectangle;
    }
}
