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

package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * User: beat
 * Date: May 18, 2010
 * Time: 3:23:47 PM
 */
public class PromotionDialog extends PopupPanel {

    private PromotionDialog(String oldLevel, String newLevel) {
        setAnimationEnabled(true);
        setPixelSize(400, 560);
        getElement().getStyle().setBackgroundImage("url(images/promotion_bg.jpg)");
        getElement().getStyle().setCursor(Style.Cursor.POINTER);
        HTML html = new HTML("<center><br><br><br><br><br><br><br><br><br><br><span style=\"color:rgb(0,0,0);font-size:150%\">You have been promoted " +
                "from<br><strong>" + oldLevel + "</strong><br>to<br><strong>" + newLevel + "</strong></span></center>\n");
        setWidget(html);
        addDomHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                hide();
            }
        }, MouseDownEvent.getType());

        getElement().getStyle().setZIndex(Constants.Z_INDEX_DIALOG);
    }

    public static void showPromotion(String oldLevel, String newLevel) {
        final PromotionDialog promotionDialog = new PromotionDialog(oldLevel, newLevel);
        promotionDialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = (Window.getClientHeight() - offsetHeight) / 2;
                promotionDialog.setPopupPosition(left, top);
            }
        });

    }

}