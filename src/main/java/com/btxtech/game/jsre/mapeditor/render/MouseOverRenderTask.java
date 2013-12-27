package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 16:14
 */
public class MouseOverRenderTask extends AbstractMapEditorRenderTask {
    @Override
    public void render(long timeStamp, Context2d context2d, MapEditorModel mapEditorModel) {
        Rectangle absoluteMouseOver = mapEditorModel.getAbsoluteMouseOver();
        if (absoluteMouseOver == null) {
            return;
        }
        Rectangle relativeMouseOver = getRelativeRectangle(absoluteMouseOver);
        context2d.save();
        context2d.setGlobalAlpha(0.5);
        context2d.setFillStyle(OVER_COLOR);
        context2d.fillRect(relativeMouseOver.getX(), relativeMouseOver.getY(), relativeMouseOver.getWidth(), relativeMouseOver.getHeight());
        context2d.setGlobalAlpha(1.0);
        context2d.setLineWidth(2.0);
        context2d.strokeRect(relativeMouseOver.getX(), relativeMouseOver.getY(), relativeMouseOver.getWidth(), relativeMouseOver.getHeight());
        context2d.restore();
    }
}
