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
import java.util.HashMap;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class UnfrequentDialog extends Dialog {
    public enum Type {
        NO_MONEY("You do not have enough money. You have to Collect more money", 60 * 1000),
        ITEM_LIMIT("You have to many items. Level up to get a bigger limit", 60 * 1000),
        SPACE_LIMIT("Space Limit Exceeded!. Build more houses to get more space", 60 * 1000);
        private String message;
        private int delay;

        Type(String message, int delay) {
            this.message = message;
            this.delay = delay;
        }
    }

    private static HashMap<Type, UnfrequentDialog> dialogHashMap = new HashMap<Type, UnfrequentDialog>();
    private String message;
    private long lastShowTimeStamp = 0;

    private UnfrequentDialog(Type type) {
        this.message = type.message;
        setupDialog();
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new HTML(message, false));
    }

    public static void open(Type type, boolean noDelayCheck) {
        UnfrequentDialog unfrequentDialog = dialogHashMap.get(type);
        if (unfrequentDialog == null) {
            unfrequentDialog = new UnfrequentDialog(type);
            dialogHashMap.put(type, unfrequentDialog);
            unfrequentDialog.lastShowTimeStamp = System.currentTimeMillis();
            return;
        }

        if (!noDelayCheck && unfrequentDialog.lastShowTimeStamp + type.delay > System.currentTimeMillis()) {
            return;
        }

        if (!unfrequentDialog.isShowing()) {
            unfrequentDialog.show();
            if (!noDelayCheck) {
                unfrequentDialog.lastShowTimeStamp = System.currentTimeMillis();
            }
        }
    }
}
