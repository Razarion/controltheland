package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.radar.MiniTerrain;
import com.btxtech.game.jsre.client.cockpit.radar.ScaleStep;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.RegionBuilder;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.mapeditor.TerrainEditor;
import com.btxtech.game.jsre.mapeditor.TerrainEditorAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasEnabled;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 17:15
 */
public class RegionEditorModel {
    public enum Mode {
        PAINT,
        ERASE
    }

    public enum CursorSize {
        SMALL(1),
        MIDDLE(4),
        BIG(10);
        private int size;

        CursorSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    private Collection<Index> mouseOverTiles;
    private RegionBuilder regionBuilder;
    private CursorSize cursorSize;
    private Mode mode;
    private double scaleForFullTerrain;
    private ScaleStep scaleStep;
    private double scale;
    private TerrainSettings terrainSettings;
    private Rectangle displayRectangle = new Rectangle(0, 0, 0, 0);
    private Index viewOriginTerrain = new Index(0, 0);
    private MiniTerrain miniTerrain;
    private int width;
    private int height;


    public RegionEditorModel(Region region, TerrainInfo terrainInfo, MiniTerrain miniTerrain, int width, int height) {
        this.miniTerrain = miniTerrain;
        this.width = width;
        this.height = height;
        terrainSettings = terrainInfo.getTerrainSettings();
        scaleForFullTerrain = Math.min((double) width / (double) terrainSettings.getPlayFieldXSize(), (double) height / (double) terrainSettings.getPlayFieldYSize());
        regionBuilder = new RegionBuilder(region);
        setScale(ScaleStep.WHOLE_MAP);
    }

    public void save(final HasEnabled hasEnabled) {
        Region region = regionBuilder.toRegion();
        TerrainEditorAsync terrainEditor = GWT.create(TerrainEditor.class);
        terrainEditor.saveRegionToDb(region, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                GwtCommon.handleException(throwable, true);
                hasEnabled.setEnabled(true);
            }

            @Override
            public void onSuccess(Void aVoid) {
                hasEnabled.setEnabled(true);
            }
        });
    }

    public Collection<Index> getMouseOverTiles() {
        return mouseOverTiles;
    }

    public void setMouseOverTile(Index mousePosition) {
        if (mousePosition != null) {
            Index absolute = mousePosition.scaleInverse(scale).add(viewOriginTerrain);
            Index tile = TerrainUtil.getTerrainTileIndexForAbsPosition(absolute);
            mouseOverTiles = createTileField(tile);
        } else {
            mouseOverTiles = null;
        }
    }

    public void tileSelected(Index mousePosition) {
        Index absolute = mousePosition.scaleInverse(scale).add(viewOriginTerrain);
        Index tile = TerrainUtil.getTerrainTileIndexForAbsPosition(absolute);
        Collection<Index> tiles = createTileField(tile);
        if (mode == Mode.PAINT) {
            regionBuilder.insertTile(tiles);
        } else {
            regionBuilder.removeTile(tiles);
        }
    }

    private Collection<Index> createTileField(Index tile) {
        Collection<Index> tiles = new ArrayList<Index>();
        if (cursorSize.size == 1) {
            tiles.add(tile);
        } else {
            for (int x = -cursorSize.size / 2; x < cursorSize.size / 2; x++) {
                for (int y = -cursorSize.size / 2; y < cursorSize.size / 2; y++) {
                    tiles.add(tile.add(x, y));
                }
            }
        }
        return tiles;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Rectangle getDisplayRectangle() {
        return displayRectangle;
    }

    public RegionBuilder getRegionBuilder() {
        return regionBuilder;
    }

    public void setCursorSize(CursorSize cursorSize) {
        this.cursorSize = cursorSize;
    }

    public void zoomIn() {
        ScaleStep newScale = ScaleStep.zoomIn(scaleStep);
        if (newScale != null) {
            setScale(newScale);
        }
    }

    public void zoomOut() {
        ScaleStep newScale = ScaleStep.zoomOut(scaleStep);
        if (newScale != null) {
            setScale(newScale);
        }
    }

    private void setScale(ScaleStep scaleStep) {
        Index absoluteMiddle = getAbsoluteMiddle();
        this.scaleStep = scaleStep;
        scale = scaleForFullTerrain * Math.sqrt(scaleStep.getZoom());

        int xShiftRadarPixel;
        int yShiftRadarPixel;
        if (terrainSettings.getPlayFieldXSize() > terrainSettings.getPlayFieldYSize()) {
            xShiftRadarPixel = 0;
            yShiftRadarPixel = (int) ((terrainSettings.getPlayFieldXSize() - terrainSettings.getPlayFieldYSize()) * scale / 2.0);
        } else if (terrainSettings.getPlayFieldYSize() > terrainSettings.getPlayFieldXSize()) {
            xShiftRadarPixel = (int) ((terrainSettings.getPlayFieldYSize() - terrainSettings.getPlayFieldXSize()) * scale / 2.0);
            yShiftRadarPixel = 0;
        } else {
            xShiftRadarPixel = 0;
            yShiftRadarPixel = 0;
        }
        int displayWidth = (int) Math.min(width - xShiftRadarPixel / 2, terrainSettings.getPlayFieldXSize() * scale);
        int displayHeight = (int) Math.min(height - yShiftRadarPixel / 2, terrainSettings.getPlayFieldYSize() * scale);
        displayRectangle = new Rectangle(xShiftRadarPixel, yShiftRadarPixel, displayWidth, displayHeight);
        miniTerrain.setScale(scaleStep);
        setAbsoluteViewRectMiddle(absoluteMiddle);
    }

    public double getScale() {
        return scale;
    }

    private Index getAbsoluteMiddle() {
        return new Index(viewOriginTerrain.getX() + (int) (displayRectangle.getWidth() / scale / 2.0), (int) (viewOriginTerrain.getY() + displayRectangle.getHeight() / scale / 2.0));
    }

    private void setAbsoluteViewRectMiddle(Index absoluteMiddle) {
        int width = (int) (displayRectangle.getWidth() / scale / 2);
        int height = (int) (displayRectangle.getHeight() / scale / 2);
        setViewOriginTerrain(absoluteMiddle.sub(width, height));
    }

    public Index getViewOriginTerrain() {
        return viewOriginTerrain;
    }

    public void scroll(int x, int y) {
        setViewOriginTerrain(viewOriginTerrain.add((int) (x / scale * 500), (int) (y / scale * 500)));
    }

    private void setViewOriginTerrain(Index position) {
        int newX = position.getX();
        if (newX < 0) {
            newX = 0;
        }
        if (newX + (int) (displayRectangle.getWidth() / scale) > terrainSettings.getPlayFieldXSize()) {
            newX = terrainSettings.getPlayFieldXSize() - (int) (displayRectangle.getWidth() / scale);
        }
        int newY = position.getY();
        if (newY < 0) {
            newY = 0;
        }
        if (newY + (int) (displayRectangle.getHeight() / scale) > terrainSettings.getPlayFieldYSize()) {
            newY = terrainSettings.getPlayFieldYSize() - (int) (displayRectangle.getHeight() / scale);
        }

        viewOriginTerrain = new Index(newX, newY);
        miniTerrain.setAbsoluteViewRect(viewOriginTerrain);
    }
}
