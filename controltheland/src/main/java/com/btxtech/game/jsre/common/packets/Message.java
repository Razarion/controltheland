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

package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:04:47 PM
 */
public class Message extends Packet {
    private String message;
    private boolean showRegisterDialog;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShowRegisterDialog() {
        return showRegisterDialog;
    }

    public void setShowRegisterDialog(boolean showRegisterDialog) {
        this.showRegisterDialog = showRegisterDialog;
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + message + " showRegisterDialog: " + showRegisterDialog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        return showRegisterDialog == message1.showRegisterDialog
                && !(message != null ? !message.equals(message1.message) : message1.message != null);

    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (showRegisterDialog ? 1 : 0);
        return result;
    }
}
