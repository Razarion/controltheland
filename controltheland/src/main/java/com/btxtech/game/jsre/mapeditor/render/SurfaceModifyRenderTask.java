package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.btxtech.game.jsre.mapeditor.SurfaceModifier;
import com.btxtech.game.jsre.mapeditor.TerrainImageSurfaceGroup;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 16:14
 */
public class SurfaceModifyRenderTask extends AbstractMapEditorRenderTask {
    @Override
    public void render(long timeStamp, Context2d context2d, MapEditorModel mapEditorModel) {
        TerrainImageSurfaceGroup terrainImageSurfaceGroup = mapEditorModel.getTerrainImageSurfaceGroup();
        if (terrainImageSurfaceGroup != null) {
            renderGroup(context2d, terrainImageSurfaceGroup);
        }
        SurfaceModifier surfaceModifier = mapEditorModel.getSurfaceModifier();
        if (surfaceModifier != null) {
            renderSingle(context2d, surfaceModifier);
        }

    }

    private void renderGroup(Context2d context2d, TerrainImageSurfaceGroup terrainImageSurfaceGroup) {
        context2d.save();
        for (SurfaceModifier surfaceModifier : terrainImageSurfaceGroup.getSurfaceModifiers()) {
            context2d.setFillStyle("#FFFFFF");
            Rectangle newRelativeRectangle = surfaceModifier.getNewRelativeRectangle();
            context2d.fillRect(newRelativeRectangle.getX(), newRelativeRectangle.getY(), newRelativeRectangle.getWidth(), newRelativeRectangle.getHeight());
            context2d.setGlobalAlpha(0.5);
            ImageElement imageElement = SurfaceLoaderContainer.getInstance().getImage(surfaceModifier.getImageId());
            if (imageElement != null) {
                for (int x = newRelativeRectangle.getX(); x < newRelativeRectangle.getEndX(); x += Constants.TERRAIN_TILE_WIDTH) {
                    for (int y = newRelativeRectangle.getY(); y < newRelativeRectangle.getEndY(); y += Constants.TERRAIN_TILE_HEIGHT) {
                        context2d.drawImage(imageElement, x, y, Constants.TERRAIN_TILE_WIDTH, Constants.TERRAIN_TILE_HEIGHT);
                    }
                }
            }
            context2d.setGlobalAlpha(1.0);
            if (!surfaceModifier.isPlaceAllowed()) {
                context2d.setLineWidth(10);
                context2d.setStrokeStyle("#FF0000");
                context2d.beginPath();
                context2d.moveTo(newRelativeRectangle.getX(), newRelativeRectangle.getY());
                context2d.lineTo(newRelativeRectangle.getX() + newRelativeRectangle.getWidth(), newRelativeRectangle.getY() + newRelativeRectangle.getHeight());
                context2d.moveTo(newRelativeRectangle.getX() + newRelativeRectangle.getWidth(), newRelativeRectangle.getY());
                context2d.lineTo(newRelativeRectangle.getX(), newRelativeRectangle.getY() + newRelativeRectangle.getHeight());
                context2d.stroke();
            }
        }
        context2d.restore();
    }

    private void renderSingle(Context2d context2d, SurfaceModifier surfaceModifier) {
        context2d.save();
        context2d.setFillStyle("#FFFFFF");
        Rectangle newRelativeRectangle = surfaceModifier.getNewRelativeRectangle();
        context2d.fillRect(newRelativeRectangle.getX(), newRelativeRectangle.getY(), newRelativeRectangle.getWidth(), newRelativeRectangle.getHeight());
        context2d.setGlobalAlpha(0.5);
        ImageElement imageElement = SurfaceLoaderContainer.getInstance().getImage(surfaceModifier.getImageId());
        if (imageElement != null) {
            for (int x = newRelativeRectangle.getX(); x < newRelativeRectangle.getEndX(); x += Constants.TERRAIN_TILE_WIDTH) {
                for (int y = newRelativeRectangle.getY(); y < newRelativeRectangle.getEndY(); y += Constants.TERRAIN_TILE_HEIGHT) {
                    context2d.drawImage(imageElement, x, y, Constants.TERRAIN_TILE_WIDTH, Constants.TERRAIN_TILE_HEIGHT);
                }
            }
        }
        context2d.setGlobalAlpha(1.0);
        if (!surfaceModifier.isPlaceAllowed()) {
            context2d.setLineWidth(10);
            context2d.setStrokeStyle("#FF0000");
            context2d.beginPath();
            context2d.moveTo(newRelativeRectangle.getX(), newRelativeRectangle.getY());
            context2d.lineTo(newRelativeRectangle.getX() + newRelativeRectangle.getWidth(), newRelativeRectangle.getY() + newRelativeRectangle.getHeight());
            context2d.moveTo(newRelativeRectangle.getX() + newRelativeRectangle.getWidth(), newRelativeRectangle.getY());
            context2d.lineTo(newRelativeRectangle.getX(), newRelativeRectangle.getY() + newRelativeRectangle.getHeight());
            context2d.stroke();
        }
        context2d.restore();
    }

}
