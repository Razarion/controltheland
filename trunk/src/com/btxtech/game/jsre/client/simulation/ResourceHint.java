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

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
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
        MapWindow.getAbsolutePanel().add(image, resourceHintConfig.getPosition().getX(), resourceHintConfig.getPosition().getY());
    }

    public void dispose() {
        MapWindow.getAbsolutePanel().remove(image);
    }
}
