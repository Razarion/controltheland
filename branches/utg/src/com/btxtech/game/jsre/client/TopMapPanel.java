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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: Oct 18, 2009
 * Time: 1:55:03 AM
 */
public abstract class TopMapPanel extends DecoratorPanel {
    public TopMapPanel() {
        setStyleName("topMapPanel");

        Widget content = createBody();
        content.getElement().getStyle().setBackgroundImage("url(/images/transparentimg.png)");

        setWidget(content);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
    }

    protected abstract Widget createBody();
}
