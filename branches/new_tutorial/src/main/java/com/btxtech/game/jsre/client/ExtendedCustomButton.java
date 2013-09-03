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

/**
 * User: beat
 * Date: 10.11.2010
 * Time: 17:57:12
 */
public class ExtendedCustomButton extends CustomButton {
    private boolean toggle;
    private String imageName;

    public ExtendedCustomButton(String imageName, boolean toggle, String toolTip, ClickHandler handler) {
        this(imageName, toggle, toolTip);
        addClickHandler(handler);
    }

    public ExtendedCustomButton(String imageName, boolean toggle, String toolTip) {
        super(ImageHandler.getButtonUpImage(imageName), ImageHandler.getButtonDownImage(imageName));
        this.imageName = imageName;
        setStyleName("excustombutton");
        this.toggle = toggle;
        getElement().getStyle().setCursor(Style.Cursor.POINTER);
        setTooltip(toolTip);
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

    public void setDownState(boolean isDown) {
        if (toggle) {
            setDown(isDown);
        }
    }

    public void setTooltip(String toolTip) {
        setTitle(toolTip);
    }

    public void setSupportDisabled() {
        getUpDisabledFace().setImage(ImageHandler.getButtonDisabledImage(imageName));
        getDownDisabledFace().setImage(ImageHandler.getButtonDisabledImage(imageName));
    }
}
