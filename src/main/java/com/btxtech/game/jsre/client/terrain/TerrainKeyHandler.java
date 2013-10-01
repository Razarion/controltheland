package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

/**
 * User: beat
 * Date: 30.07.12
 * Time: 20:30
 */
public class TerrainKeyHandler implements KeyDownHandler, BlurHandler {
    private TerrainScrollHandler terrainScrollHandler;

    public TerrainKeyHandler(Canvas canvas, TerrainScrollHandler terrainScrollHandler) {
        this.terrainScrollHandler = terrainScrollHandler;
        canvas.addKeyDownHandler(this);
        canvas.addBlurHandler(this);
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (CockpitMode.getInstance().hasInventoryItemPlacer()) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                CockpitMode.getInstance().setInventoryItemPlacer(null);
            }
        } else if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                CockpitMode.getInstance().setToBeBuildPlacer(null);
            }
        }
    }

    @Override
    public void onBlur(BlurEvent event) {
        if (CockpitMode.getInstance().hasInventoryItemPlacer()) {
            CockpitMode.getInstance().setInventoryItemPlacer(null);
        }
    }

    public void handlePreviewNativeEvent(Event.NativePreviewEvent event) {
        if (event.getTypeInt() == Event.ONKEYDOWN) {

            try {
                // Prevent scrolling if event if from a text or password
                if (Element.is(event.getNativeEvent().getEventTarget())) {
                    com.google.gwt.dom.client.Element element = Element.as(event.getNativeEvent().getEventTarget());
                    if (element instanceof InputElement) {
                        String type = ((InputElement) element).getType();
                        if (type != null) {
                            if (type.equalsIgnoreCase("text")) {
                                return;
                            }
                            if (type.equalsIgnoreCase("password")) {
                                return;
                            }
                            if (type.equalsIgnoreCase("textarea")) {
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ClientExceptionHandler.handleExceptionOnlyOnce("TerrainKeyHandler.handlePreviewNativeEvent()", e);
            }

            switch (event.getNativeEvent().getKeyCode()) {
                case 65:
                case KeyCodes.KEY_LEFT: {
                    terrainScrollHandler.executeAutoScrollKey(TerrainScrollHandler.ScrollDirection.WEST, null);
                    event.cancel(); // Prevent from scrolling the browser window
                    break;
                }
                case 68:
                case KeyCodes.KEY_RIGHT: {
                    terrainScrollHandler.executeAutoScrollKey(TerrainScrollHandler.ScrollDirection.EAST, null);
                    event.cancel();
                    break;
                }
                case 87:
                case KeyCodes.KEY_UP: {
                    terrainScrollHandler.executeAutoScrollKey(null, TerrainScrollHandler.ScrollDirection.NORTH);
                    event.cancel();
                    break;
                }
                case 83:
                case KeyCodes.KEY_DOWN: {
                    terrainScrollHandler.executeAutoScrollKey(null, TerrainScrollHandler.ScrollDirection.SOUTH);
                    event.cancel();
                    break;
                }
                case KeyCodes.KEY_ESCAPE: {
                    CockpitMode.getInstance().setToBeBuildPlacer(null);
                }
            }
        } else if (event.getTypeInt() == Event.ONKEYUP) {
            switch (event.getNativeEvent().getKeyCode()) {
                case 65:
                case KeyCodes.KEY_LEFT: {
                    terrainScrollHandler.executeAutoScrollKey(TerrainScrollHandler.ScrollDirection.STOP, null);
                    event.cancel(); // Prevent from scrolling the browser window
                    break;
                }
                case 68:
                case KeyCodes.KEY_RIGHT: {
                    terrainScrollHandler.executeAutoScrollKey(TerrainScrollHandler.ScrollDirection.STOP, null);
                    event.cancel();
                    break;
                }
                case 87:
                case KeyCodes.KEY_UP: {
                    terrainScrollHandler.executeAutoScrollKey(null, TerrainScrollHandler.ScrollDirection.STOP);
                    event.cancel();
                    break;
                }
                case 83:
                case KeyCodes.KEY_DOWN: {
                    terrainScrollHandler.executeAutoScrollKey(null, TerrainScrollHandler.ScrollDirection.STOP);
                    event.cancel();
                    break;
                }
            }
        }
    }

}
