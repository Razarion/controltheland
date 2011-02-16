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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: 10.11.2010
 * Time: 17:57:12
 */
public class ExtendedCustomButton extends CustomButton {
    private boolean toggle;

    public ExtendedCustomButton(String upImage, String downImage, boolean toggle, String toolTip, ClickHandler handler) {
        super(new Image(upImage), new Image(downImage), handler);
        this.toggle = toggle;
        getElement().getStyle().setCursor(Style.Cursor.POINTER);
        setTitle(toolTip);
    }

    @Override
    protected void onClick() {
        if (toggle) {
            setDown(!isDown());
        } else {
            setDown(false);
        }
        super.onClick();
    }

    @Override
    protected void onClickCancel() {
        if (!toggle) {
            setDown(false);
        }
    }

    @Override
    protected void onClickStart() {
        if (!toggle) {
            setDown(true);
        }
    }

    @Override
    public boolean isDown() {
        // Changes access to public.
        return super.isDown();
    }


}
