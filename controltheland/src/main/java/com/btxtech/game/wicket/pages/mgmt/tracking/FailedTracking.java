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

package com.btxtech.game.wicket.pages.mgmt.tracking;

import com.btxtech.game.services.utg.LifecycleTrackingInfo;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 17.01.2010
 * Time: 13:11:15
 */
public class FailedTracking extends Panel {

    public FailedTracking(String id, LifecycleTrackingInfo lifecycleTrackingInfo) {
        super(id);
        add(new LifecyclePanel("lifecycle", lifecycleTrackingInfo));
        add(new Label("baseName", lifecycleTrackingInfo.getBaseName()));
    }
}
