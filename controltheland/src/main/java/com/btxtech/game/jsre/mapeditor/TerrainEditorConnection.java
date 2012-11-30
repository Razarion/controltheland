package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.mapeditor.render.MapEditorRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasEnabled;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 01:32
 */
public class TerrainEditorConnection {
    private TerrainEditorAsync terrainEditorAsync = GWT.create(TerrainEditor.class);
    private Cockpit cockpit;
    private TerrainData terrainData;
    private MapEditorRenderer mapEditorRenderer;
    private MapEditorModel mapEditorModel;
    private String rootType;
    private int rootId;

    public void setCockpit(Cockpit cockpit) {
        this.cockpit = cockpit;
    }

    public void setTerrainData(TerrainData terrainData) {
        this.terrainData = terrainData;
    }

    public void setMapEditorRenderer(MapEditorRenderer mapEditorRenderer) {
        this.mapEditorRenderer = mapEditorRenderer;
    }

    public void setMapEditorModel(MapEditorModel mapEditorModel) {
        this.mapEditorModel = mapEditorModel;
    }

    public void setTootType(String rootType) {
        this.rootType = rootType;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    public void loadTerrain() {
        if (rootType.equals(TerrainEditorAsync.ROOT_TYPE_PLANET)) {
            loadPlanetTerrain(rootId);
        } else if (rootType.equals(TerrainEditorAsync.ROOT_TYPE_MISSION)) {
            loadTutorialTerrain(rootId);
        } else {
            throw new IllegalArgumentException("TerrainEditorConnection.loadTerrain() Unknwon toot type: " + rootType);
        }
    }

    private void loadTutorialTerrain(int terrainId) {
        terrainEditorAsync.getTutorialTerrainInfo(terrainId, new AsyncCallback<com.btxtech.game.jsre.common.TerrainInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                ClientExceptionHandler.handleException(throwable);
            }

            @Override
            public void onSuccess(TerrainInfo terrainInfo) {
                handleTerrainInfo(terrainInfo);
            }
        });
    }

    private void loadPlanetTerrain(int planetId) {
        terrainEditorAsync.getPlanetTerrainInfo(planetId, new AsyncCallback<com.btxtech.game.jsre.common.TerrainInfo>() {
            @Override
            public void onFailure(Throwable throwable) {
                ClientExceptionHandler.handleException(throwable);
            }

            @Override
            public void onSuccess(TerrainInfo terrainInfo) {
                handleTerrainInfo(terrainInfo);
            }
        });
    }

    private void handleTerrainInfo(TerrainInfo terrainInfo) {
        terrainData.setSurfaceRects(terrainInfo.getSurfaceRects());
        terrainData.setTerrainImagePositions(terrainInfo.getTerrainImagePositions());
        TerrainView.getInstance().setupTerrain(terrainInfo.getTerrainSettings(),
                terrainInfo.getTerrainImagePositions(),
                terrainInfo.getSurfaceRects(),
                terrainInfo.getSurfaceImages(),
                terrainInfo.getTerrainImages(),
                terrainInfo.getTerrainImageBackground());
        RadarPanel.getInstance().onTerrainSettings(terrainInfo.getTerrainSettings());
        RadarPanel.getInstance().getRadarFrameView().onScroll(mapEditorModel.getViewRectangle().getX(), mapEditorModel.getViewRectangle().getY(), mapEditorModel.getViewRectangle().getWidth(), mapEditorModel.getViewRectangle().getHeight(), 0, 0);
        mapEditorRenderer.start();
        loadTerrainImageGroups(terrainInfo);
    }

    private void loadTerrainImageGroups(final TerrainInfo terrainInfo) {
        terrainEditorAsync.getTerrainImageGroups(new AsyncCallback<Map<String, Collection<Integer>>>() {
            @Override
            public void onFailure(Throwable caught) {
                ClientExceptionHandler.handleException(caught, true);
            }

            @Override
            public void onSuccess(Map<String, Collection<Integer>> terrainImageGroups) {
                cockpit.setupSurfaceSelector(terrainInfo);
                cockpit.setupImageSelectors(terrainInfo, terrainImageGroups);
            }
        });
    }

    public void save(final HasEnabled hasEnabled) {
        AsyncCallback<Void> voidAsyncCallback = new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ClientExceptionHandler.handleException(caught, true);
                hasEnabled.setEnabled(true);
            }

            @Override
            public void onSuccess(Void result) {
                hasEnabled.setEnabled(true);
            }
        };

        if (rootType.equals(TerrainEditorAsync.ROOT_TYPE_PLANET)) {
            terrainEditorAsync.savePlanetTerrainImagePositions(terrainData.getTerrainImagePositions(), terrainData.getSurfaceRects(), rootId, voidAsyncCallback);
        } else if (rootType.equals(TerrainEditorAsync.ROOT_TYPE_MISSION)) {
            terrainEditorAsync.saveTutorialTerrainImagePositions(terrainData.getTerrainImagePositions(), terrainData.getSurfaceRects(), rootId, voidAsyncCallback);
        } else {
            throw new IllegalArgumentException("TerrainEditorConnection.save() Unknwon toot type: " + rootType);
        }
    }
}
