/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.simulation.hint;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.tutorial.ResourceHintConfig;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 22:39:37
 */
public class ResourceHint implements Hint {
    private Image image;

    public ResourceHint(ResourceHintConfig resourceHintConfig) {
        image = ImageHandler.getTutorialImage(resourceHintConfig.getImageId(), null);
        image.getElement().getStyle().setZIndex(Constants.Z_INDEX_BELOW_BUILDING);
        Index relPos = TerrainView.getInstance().toRelativeIndex(resourceHintConfig.getPosition());
        MapWindow.getAbsolutePanel().add(image, relPos.getX(), relPos.getY());
        MapWindow.getInstance().addToScrollElements(image);
    }

    public void dispose() {
        MapWindow.getInstance().removeToScrollElements(image);        
        MapWindow.getAbsolutePanel().remove(image);
    }
}
