package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainScrollHandler;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Focusable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 13:57
 */
public class MapEditorModel implements TerrainScrollHandler.ScrollExecutor, MouseMoveHandler, MouseDownHandler, MouseUpHandler, KeyDownHandler, MouseOverHandler {
    public enum ActiveLayer {
        SURFACE,
        IMAGE_LAYER_1,
        IMAGE_LAYER_2
    }

    private Rectangle viewRectangle;
    private Rectangle viewTileRectangle;
    private TerrainScrollHandler terrainScrollHandler = new TerrainScrollHandler();
    private ActiveLayer activeLayer;
    private boolean selectionMode;
    private Rectangle absoluteMouseOver;
    private TerrainData terrainData;
    private boolean deleteMode;
    private TerrainImageModifier terrainImageModifier;
    private SurfaceModifier surfaceModifier;
    private MapEditorCursorHandler mapEditorCursorHandler;
    private TerrainEditorSelection terrainEditorSelection;
    private boolean selectionModeSurface;
    private boolean selectionModeLayer1;
    private boolean selectionModeLayer2;
    private TerrainImageSurfaceGroup terrainImageSurfaceGroup;
    private boolean leftButton;

    public MapEditorModel(int width, int height) {
        viewRectangle = new Rectangle(0, 0, width, height);
        viewTileRectangle = TerrainUtil.convertToTilePositionRoundUp(viewRectangle);
        terrainScrollHandler.setScrollExecutor(this);
    }

    public void setTerrainData(TerrainData terrainData) {
        this.terrainData = terrainData;
    }

    public void setActiveLayer(ActiveLayer activeLayer) {
        this.activeLayer = activeLayer;
        absoluteMouseOver = null;
    }

    public Rectangle getViewRectangle() {
        return viewRectangle;
    }

    public Rectangle getViewTileRectangle() {
        return viewTileRectangle;
    }

    public Rectangle getAbsoluteMouseOver() {
        return absoluteMouseOver;
    }

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    public TerrainImageModifier getTerrainImageModifier() {
        return terrainImageModifier;
    }

    public SurfaceModifier getSurfaceModifier() {
        return surfaceModifier;
    }

    public void setMapEditorCursorHandler(MapEditorCursorHandler mapEditorCursorHandler) {
        this.mapEditorCursorHandler = mapEditorCursorHandler;
    }

    @Override
    public void moveDelta(int scrollX, int scrollY) {
        Index safeDelta = TerrainScrollHandler.calculateSafeDelta(scrollX, scrollY, TerrainView.getInstance().getTerrainHandler().getTerrainSettings(), viewRectangle);
        if (!safeDelta.isNull()) {
            viewRectangle.shift(safeDelta.getX(), safeDelta.getY());
            viewTileRectangle = TerrainUtil.convertToTilePositionRoundUp(viewRectangle);
            RadarPanel.getInstance().getRadarFrameView().onScroll(viewRectangle.getX(), viewRectangle.getY(), viewRectangle.getWidth(), viewRectangle.getHeight(), 0, 0);
        }
        if (terrainImageSurfaceGroup != null) {
            terrainImageSurfaceGroup.onScroll(viewRectangle);
        }
        absoluteMouseOver = null;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        terrainScrollHandler.handleMouseMoveScroll(event.getX(), event.getY(), viewRectangle.getWidth(), viewRectangle.getHeight());

        Index absoluteMouse = new Index(event.getX(), event.getY()).add(viewRectangle.getStart());
        absoluteMouseOver = null;
        if (selectionMode) {
            if (terrainImageSurfaceGroup != null) {
                if (leftButton) {
                    terrainImageSurfaceGroup.mouseMove(absoluteMouse, viewRectangle);
                }
            } else if (terrainEditorSelection != null) {
                terrainEditorSelection.setEnd(absoluteMouse, viewRectangle);
            }
        } else if (terrainImageModifier != null) {
            terrainImageModifier.onMouseMove(absoluteMouse, viewRectangle, null);
        } else if (surfaceModifier != null) {
            surfaceModifier.onMouseMove(absoluteMouse, viewRectangle, null);
        } else if (activeLayer != null) {
            mapEditorCursorHandler.clearCursor();
            switch (activeLayer) {
                case SURFACE: {
                    SurfaceRect surfaceRect = terrainData.getSurfaceRect(absoluteMouse.getX(), absoluteMouse.getY());
                    if (surfaceRect != null) {
                        absoluteMouseOver = TerrainUtil.convertToAbsolutePosition(surfaceRect.getTileRectangle());
                        EditMode editMode = new EditMode(absoluteMouse, absoluteMouseOver);
                        mapEditorCursorHandler.setSurfaceCursor(editMode);
                    }
                    break;
                }
                case IMAGE_LAYER_1: {
                    absoluteMouseOver = getAbsoluteTerrainImageRect(absoluteMouse, TerrainImagePosition.ZIndex.LAYER_1);
                    break;
                }
                case IMAGE_LAYER_2: {
                    absoluteMouseOver = getAbsoluteTerrainImageRect(absoluteMouse, TerrainImagePosition.ZIndex.LAYER_2);
                    break;
                }
            }
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            leftButton = true;
        }
        absoluteMouseOver = null;
        Index absoluteMouse = new Index(event.getX(), event.getY()).add(viewRectangle.getStart());
        if (selectionMode) {
            if (terrainImageSurfaceGroup == null) {
                terrainEditorSelection = new TerrainEditorSelection();
                terrainEditorSelection.setStart(absoluteMouse);
            } else {
                terrainImageSurfaceGroup.startMove(absoluteMouse);
            }
        } else if (deleteMode && activeLayer != null) {
            switch (activeLayer) {
                case SURFACE: {
                    SurfaceRect surfaceRect = terrainData.getSurfaceRect(absoluteMouse.getX(), absoluteMouse.getY());
                    if (surfaceRect != null) {
                        terrainData.removeSurfaceRect(surfaceRect);
                    }
                    break;
                }
                case IMAGE_LAYER_1: {
                    TerrainImagePosition terrainImagePosition = terrainData.getTerrainImagePosition(TerrainImagePosition.ZIndex.LAYER_1, absoluteMouse.getX(), absoluteMouse.getY());
                    if (terrainImagePosition != null) {
                        terrainData.removeTerrainImagePosition(terrainImagePosition);
                    }
                    break;
                }
                case IMAGE_LAYER_2: {
                    TerrainImagePosition terrainImagePosition = terrainData.getTerrainImagePosition(TerrainImagePosition.ZIndex.LAYER_2, absoluteMouse.getX(), absoluteMouse.getY());
                    if (terrainImagePosition != null) {
                        terrainData.removeTerrainImagePosition(terrainImagePosition);
                    }
                    break;
                }
            }
        } else if (surfaceModifier == null && terrainImageModifier == null && activeLayer != null) {
            switch (activeLayer) {
                case SURFACE: {
                    SurfaceRect surfaceRect = terrainData.getSurfaceRect(absoluteMouse.getX(), absoluteMouse.getY());
                    if (surfaceRect != null) {
                        EditMode editMode = new EditMode(absoluteMouse, TerrainUtil.convertToAbsolutePosition(surfaceRect.getTileRectangle()));
                        surfaceModifier = new SurfaceModifier(surfaceRect, absoluteMouse, viewRectangle, terrainData, editMode);
                    }
                    break;
                }
                case IMAGE_LAYER_1: {
                    TerrainImagePosition terrainImagePosition = terrainData.getTerrainImagePosition(TerrainImagePosition.ZIndex.LAYER_1, absoluteMouse.getX(), absoluteMouse.getY());
                    if (terrainImagePosition != null) {
                        terrainImageModifier = new TerrainImageModifier(terrainImagePosition, absoluteMouse, viewRectangle, terrainData);
                    }
                    break;
                }
                case IMAGE_LAYER_2: {
                    TerrainImagePosition terrainImagePosition = terrainData.getTerrainImagePosition(TerrainImagePosition.ZIndex.LAYER_2, absoluteMouse.getX(), absoluteMouse.getY());
                    if (terrainImagePosition != null) {
                        terrainImageModifier = new TerrainImageModifier(terrainImagePosition, absoluteMouse, viewRectangle, terrainData);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            leftButton = false;
        }
        Index absoluteMouse = new Index(event.getX(), event.getY()).add(viewRectangle.getStart());
        if (selectionMode) {
            if (terrainImageSurfaceGroup != null) {
                if (terrainImageSurfaceGroup.isPlaceAllowed()) {
                    terrainImageSurfaceGroup.updateModel();
                }
            } else if (terrainEditorSelection != null) {
                terrainEditorSelection.setEnd(absoluteMouse, viewRectangle);
                Collection<TerrainImagePosition> selectionImages = terrainData.getTerrainImagePositionInRegion(terrainEditorSelection.getAbsoluteRectangle(), selectionModeLayer1, selectionModeLayer2);
                Collection<SurfaceRect> surfaceRects;
                if (selectionModeSurface) {
                    surfaceRects = terrainData.getSurfaceRectInRegion(terrainEditorSelection.getAbsoluteRectangle());
                } else {
                    surfaceRects = new ArrayList<SurfaceRect>();
                }
                terrainImageSurfaceGroup = new TerrainImageSurfaceGroup(selectionImages, surfaceRects, absoluteMouse, viewRectangle, terrainData);
                terrainEditorSelection = null;
            }
        } else if (terrainImageModifier != null) {
            if (terrainImageModifier.isPlaceAllowed()) {
                terrainImageModifier.updateModel();
                terrainImageModifier = null;
            }
        } else if (surfaceModifier != null) {
            if (surfaceModifier.isPlaceAllowed()) {
                surfaceModifier.updateModel();
                surfaceModifier = null;
            }
        }
    }

    public void createTerrainImagePosition(TerrainImage terrainImage, MouseDownEvent mouseDownEvent) {
        absoluteMouseOver = null;
        Index absoluteIndex = new Index(mouseDownEvent.getClientX(), mouseDownEvent.getClientY()).add(viewRectangle.getStart());
        terrainImageModifier = new TerrainImageModifier(terrainImage,
                activeLayer == ActiveLayer.IMAGE_LAYER_1 ? TerrainImagePosition.ZIndex.LAYER_1 : TerrainImagePosition.ZIndex.LAYER_2,
                absoluteIndex,
                viewRectangle,
                terrainData);
    }

    public void createSurfaceRect(int imageId, MouseDownEvent mouseDownEvent) {
        absoluteMouseOver = null;
        Index absoluteIndex = new Index(mouseDownEvent.getClientX(), mouseDownEvent.getClientY()).add(viewRectangle.getStart());
        surfaceModifier = new SurfaceModifier(imageId, absoluteIndex, viewRectangle, terrainData);
    }

    private Rectangle getAbsoluteTerrainImageRect(Index absoluteIndex, TerrainImagePosition.ZIndex zIndex) {
        TerrainImagePosition terrainImagePosition = terrainData.getTerrainImagePosition(zIndex, absoluteIndex.getX(), absoluteIndex.getY());
        if (terrainImagePosition != null) {
            TerrainImage terrainImage = TerrainView.getInstance().getTerrainHandler().getCommonTerrainImageService().getTerrainImage(terrainImagePosition.getImageId());
            return TerrainUtil.convertToAbsolutePosition(new Rectangle(terrainImagePosition.getTileIndex().getX(),
                    terrainImagePosition.getTileIndex().getY(),
                    terrainImage.getTileWidth(),
                    terrainImage.getTileHeight()));
        } else {
            return null;
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_ESCAPE: {
                terrainImageModifier = null;
                surfaceModifier = null;
                terrainImageSurfaceGroup = null;
                break;
            }
            case KeyCodes.KEY_UP: {
                moveDelta(0, -100);
                break;
            }
            case KeyCodes.KEY_DOWN: {
                moveDelta(0, 100);
                break;
            }
            case KeyCodes.KEY_LEFT: {
                moveDelta(-100, 0);
                break;
            }
            case KeyCodes.KEY_RIGHT: {
                moveDelta(100, 0);
                break;
            }
        }
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        ((Focusable) event.getSource()).setFocus(true);
    }

    public void onCanvasSizeChanged(int width, int height) {
        viewRectangle.setWidth(width);
        viewRectangle.setHeight(height);
        viewTileRectangle = TerrainUtil.convertToTilePositionRoundUp(viewRectangle);
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public TerrainEditorSelection getTerrainEditorSelection() {
        return terrainEditorSelection;
    }

    public void setSelectionModeSurface(boolean selectionModeSurface) {
        this.selectionModeSurface = selectionModeSurface;
    }

    public void setSelectionModeLayer1(boolean selectionModeLayer1) {
        this.selectionModeLayer1 = selectionModeLayer1;
    }

    public void setSelectionModeLayer2(boolean selectionModeLayer2) {
        this.selectionModeLayer2 = selectionModeLayer2;
    }

    public TerrainImageSurfaceGroup getTerrainImageSurfaceGroup() {
        return terrainImageSurfaceGroup;
    }

    public boolean isSelectionModeSurface() {
        return selectionModeSurface;
    }

    public boolean isSelectionModeLayer1() {
        return selectionModeLayer1;
    }

    public boolean isSelectionModeLayer2() {
        return selectionModeLayer2;
    }

    public void deleteTerrainImageSurfaceGroup() {
        if (terrainImageSurfaceGroup != null) {
            terrainImageSurfaceGroup.deleteAll(terrainData);
            terrainImageSurfaceGroup = null;
        }
    }

}
