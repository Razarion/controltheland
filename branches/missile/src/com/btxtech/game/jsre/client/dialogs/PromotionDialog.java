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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: May 18, 2010
 * Time: 3:23:47 PM
 */
public class PromotionDialog extends PopupPanel {

    private PromotionDialog(String oldLevel, String newLevel) {
        setAnimationEnabled(true);
        VerticalPanel verticalPanel = new VerticalPanel();
        HTML html = new HTML("<span style=\"color:rgb(200,0,200);font-size:300%\"><strong>You have been promoted!</strong></span>\n" +
                "<center><span style=\"color:#CC0000;font-size:150%\">From <strong>" + oldLevel + "</strong> to <strong>" + newLevel + "</strong></span><br>\n" +
                "<span style=\"color:blue\">Please check your new mission target</span></center>");
        html.setPixelSize(300, 200);
        verticalPanel.add(html);
        Button closeButton = new Button("Close");
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        verticalPanel.add(closeButton);
        setWidget(verticalPanel);
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