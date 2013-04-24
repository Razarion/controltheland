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

import com.btxtech.game.jsre.client.ClientI18nHelper;

import java.util.HashMap;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class UnfrequentDialog extends MessageDialog {
    public enum Type {
        NO_MONEY(ClientI18nHelper.CONSTANTS.noMoney(), ClientI18nHelper.CONSTANTS.notEnoughMoney(), 60 * 1000),
        ITEM_LIMIT(ClientI18nHelper.CONSTANTS.limitation(), ClientI18nHelper.CONSTANTS.tooManyItems(), 60 * 1000),
        SPACE_LIMIT(ClientI18nHelper.CONSTANTS.houseSpace(), ClientI18nHelper.CONSTANTS.spaceLimitExceeded(), 60 * 1000);
        private String title;
        private String message;
        private int delay;

        Type(String title, String message, int delay) {
            this.title = title;
            this.message = message;
            this.delay = delay;
        }
    }

    private static HashMap<Type, UnfrequentDialog> dialogHashMap = new HashMap<Type, UnfrequentDialog>();
    private long lastShowTimeStamp = 0;  // Can not be converted to local variable

    protected UnfrequentDialog(Type type) {
        super(type.title, type.message);
    }

    public static void open(Type type) {
        UnfrequentDialog unfrequentDialog = dialogHashMap.get(type);
        if (unfrequentDialog == null) {
            unfrequentDialog = new UnfrequentDialog(type);
            DialogManager.showDialog(unfrequentDialog, DialogManager.Type.UNIMPORTANT);
            dialogHashMap.put(type, unfrequentDialog);
            unfrequentDialog.lastShowTimeStamp = System.currentTimeMillis();
            return;
        }

        if (unfrequentDialog.lastShowTimeStamp + type.delay > System.currentTimeMillis()) {
            return;
        }

        if (!unfrequentDialog.isShowing()) {
            DialogManager.showDialog(unfrequentDialog, DialogManager.Type.UNIMPORTANT);
        }
        unfrequentDialog.lastShowTimeStamp = System.currentTimeMillis();
    }
}
