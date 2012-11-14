package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.renderer.TerrainImageLoaderContainer;
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
            context2d.setGlobalAlpha(0.75);
            if (terrainImageModifier.isPlaceAllowed()) {
                context2d.setFillStyle(SELECT_COLOR);
            } else {
                context2d.setFillStyle(FORBIDDEN_COLOR);
            }
            context2d.fillRect(relativeImage.getX(), relativeImage.getY(), terrainImageModifier.getWidth(), terrainImageModifier.getHeight());
            context2d.setGlobalAlpha(0.5);
            if (imageElement != null) {
                context2d.drawImage(imageElement, relativeImage.getX(), relativeImage.getY());
            }
        }
        context2d.restore();
    }

    private void renderSingle(Context2d context2d, TerrainImageModifier terrainImageModifier) {
        context2d.save();
        Index relativeImage = terrainImageModifier.getRelativeGridPosition();
        ImageElement imageElement = TerrainImageLoaderContainer.getInstance().getImage(terrainImageModifier.getImageId());

        if (terrainImageModifier.isPlaceAllowed()) {
            context2d.setFillStyle(SELECT_COLOR);
        } else {
            context2d.setFillStyle(FORBIDDEN_COLOR);
        }
        context2d.setGlobalAlpha(0.75);
        context2d.fillRect(relativeImage.getX(), relativeImage.getY(), terrainImageModifier.getWidth(), terrainImageModifier.getHeight());

        context2d.setGlobalAlpha(0.5);
        if (imageElement != null) {
            context2d.drawImage(imageElement, relativeImage.getX(), relativeImage.getY());
        }
        context2d.restore();
    }
}
