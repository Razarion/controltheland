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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class NoMoneyDialog extends Dialog {
    private final static long DELAY = 60 * 1000;
    private String message;
    private static NoMoneyDialog noMoneyDialog;
    private static long lastShowTimeStamp = 0;

    private NoMoneyDialog(String title, String message) {
        this.message = message;
        setupDialog(title);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new HTML(message, false));
    }

    public static void open() {
        if(lastShowTimeStamp + DELAY > System.currentTimeMillis()) {
            return;
        }

        if (noMoneyDialog == null) {
            noMoneyDialog = new NoMoneyDialog("Insufficient Money!", "You do not have enough money. You have to Collect more money");
        } else if(!noMoneyDialog.isShowing()){
            noMoneyDialog.show();
        }
        lastShowTimeStamp = System.currentTimeMillis();
    }
}
