package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.btxtech.game.jsre.mapeditor.TerrainImageModifier;
import com.btxtech.game.jsre.mapeditor.TerrainImageSurfaceGroup;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 16:14
 */
public class TerrainImageModifyRenderTask extends AbstractMapEditorRenderTask {
    @Override
    public void render(long timeStamp, Context2d context2d, MapEditorModel mapEditorModel) {
        TerrainImageSurfaceGroup terrainImageSurfaceGroup = mapEditorModel.getTerrainImageSurfaceGroup();
        if (terrainImageSurfaceGroup != null && !terrainImageSurfaceGroup.getTerrainImageModifiers().isEmpty()) {
            renderGroup(context2d, terrainImageSurfaceGroup);
        }
        TerrainImageModifier terrainImageModifier = mapEditorModel.getTerrainImageModifier();
        if (terrainImageModifier != null) {
            renderSingle(context2d, terrainImageModifier);
        }
    }

    private void renderGroup(Context2d context2d, TerrainImageSurfaceGroup terrainImageSurfaceGroup) {
        context2d.save();
        for (TerrainImageModifier terrainImageModifier : terrainImageSurfaceGroup.getTerrainImageModifiers()) {
            Index relativeImage = terrainImageModifier.getRelativeGridPosition();
            ImageElement imageElement = TerrainImageLoaderContainer.getInstance().getImage(terrainImageModifier.getImageId());

            context2d.setGlobalAlpha(0.5);
            context2d.setFillStyle("#FF0000");
            context2d.fillRect(relativeImage.getX(), relativeImage.getY(), terrainImageModifier.getWidth(), terrainImageModifier.getHeight());
            if (imageElement != null) {
                context2d.drawImage(imageElement, relativeImage.getX(), relativeImage.getY());
            }

            context2d.setGlobalAlpha(1.0);
            if (!terrainImageModifier.isPlaceAllowed()) {
                context2d.setLineWidth(10);
                context2d.setStrokeStyle("#FF0000");
                context2d.beginPath();
                context2d.moveTo(relativeImage.getX(), relativeImage.getY());
                context2d.lineTo(relativeImage.getX() + terrainImageModifier.getWidth(), relativeImage.getY() + terrainImageModifier.getHeight());
                context2d.moveTo(relativeImage.getX() + terrainImageModifier.getWidth(), relativeImage.getY());
                context2d.lineTo(relativeImage.getX(), relativeImage.getY() + terrainImageModifier.getHeight());
                context2d.stroke();
            }
        }
        context2d.restore();
    }

    private void renderSingle(Context2d context2d, TerrainImageModifier terrainImageModifier) {
        context2d.save();
        context2d.setGlobalAlpha(0.5);
        Index relativeImage = terrainImageModifier.getRelativeGridPosition();
        ImageElement imageElement = TerrainImageLoaderContainer.getInstance().getImage(terrainImageModifier.getImageId());

        if (imageElement != null) {
            context2d.drawImage(imageElement, relativeImage.getX(), relativeImage.getY());
        } else {
            context2d.setFillStyle("#FFFFFF");
            context2d.fillRect(relativeImage.getX(), relativeImage.getY(), terrainImageModifier.getWidth(), terrainImageModifier.getHeight());
        }

        if (!terrainImageModifier.isPlaceAllowed()) {
            context2d.setGlobalAlpha(1.0);
            context2d.setLineWidth(10);
            context2d.setStrokeStyle("#FF0000");
            context2d.beginPath();
            context2d.moveTo(relativeImage.getX(), relativeImage.getY());
            context2d.lineTo(relativeImage.getX() + terrainImageModifier.getWidth(), relativeImage.getY() + terrainImageModifier.getHeight());
            context2d.moveTo(relativeImage.getX() + terrainImageModifier.getWidth(), relativeImage.getY());
            context2d.lineTo(relativeImage.getX(), relativeImage.getY() + terrainImageModifier.getHeight());
            context2d.stroke();
        }
        context2d.restore();
    }
}
