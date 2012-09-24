package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.btxtech.game.jsre.mapeditor.SurfaceModifier;
import com.btxtech.game.jsre.mapeditor.TerrainEditorSelection;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 16:14
 */
public class SelectionAreaRenderTask extends AbstractMapEditorRenderTask {
    @Override
    public void render(long timeStamp, Context2d context2d, MapEditorModel mapEditorModel) {
        TerrainEditorSelection terrainEditorSelection = mapEditorModel.getTerrainEditorSelection();
        if (terrainEditorSelection == null) {
            return;
        }
        Rectangle relativeRectangle = terrainEditorSelection.getRelativeRectangle();
        if (relativeRectangle == null) {
            return;
        }
        context2d.save();
        context2d.setGlobalAlpha(0.5);
        context2d.setFillStyle(SELECT_COLOR);
        context2d.fillRect(relativeRectangle.getX(), relativeRectangle.getY(), relativeRectangle.getWidth(), relativeRectangle.getHeight());
        context2d.setGlobalAlpha(1.0);
        context2d.setStrokeStyle(SELECT_COLOR);
        context2d.strokeRect(relativeRectangle.getX(), relativeRectangle.getY(), relativeRectangle.getWidth(), relativeRectangle.getHeight());
        context2d.restore();
    }
}
