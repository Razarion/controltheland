package com.btxtech.game.jsre.client.terrain;

import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

/**
 * User: beat
 * Date: 30.07.12
 * Time: 20:30
 */
public class TerritoryKeyHandler implements KeyDownHandler, BlurHandler {
    public TerritoryKeyHandler(Canvas canvas) {
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
        } else if (CockpitMode.getInstance().hasToBeBuildPlacer()) {
            CockpitMode.getInstance().setToBeBuildPlacer(null);
        }
    }
}
