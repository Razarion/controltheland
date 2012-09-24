package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:40
 */
public abstract class AbstractMapEditorRenderTask {
    public static final String OVER_COLOR = "#888800";
    public static final String SELECT_COLOR = "#FFFF00";
    public static final String FORBIDDEN_COLOR = "#FF0000";

    private Rectangle viewRectangle;

    public abstract void render(long timeStamp, final Context2d context2d, MapEditorModel mapEditorModel);

    public void setViewRectangle(Rectangle viewRectangle) {
        this.viewRectangle = viewRectangle;
    }

    protected Rectangle getRelativeRectangle(Rectangle absoluteMouseOver) {
        return new Rectangle(absoluteMouseOver.getStart().sub(viewRectangle.getStart()), absoluteMouseOver.getEnd().sub(viewRectangle.getStart()));
    }
}
