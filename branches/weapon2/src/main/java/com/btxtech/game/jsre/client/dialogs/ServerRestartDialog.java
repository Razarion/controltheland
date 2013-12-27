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
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.common.packets.ServerRebootMessagePacket;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class ServerRestartDialog extends Dialog {
    private ServerRebootMessagePacket serverRebootMessagePacket;

    public ServerRestartDialog(ServerRebootMessagePacket serverRebootMessagePacket) {
        super(ClientI18nHelper.CONSTANTS.serverRebootTitle());
        this.serverRebootMessagePacket = serverRebootMessagePacket;
        getElement().getStyle().setWidth(350, Style.Unit.PX);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new Label(ClientI18nHelper.CONSTANTS.serverRebootMessage(serverRebootMessagePacket.getRebootInSeconds(), serverRebootMessagePacket.getDownTimeInMinutes())));

        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER) {
            dialogVPanel.add(new Label(ClientI18nHelper.CONSTANTS.serverRebootMissionNotSaved()));
        }

        if (!ClientUserService.getInstance().isRegistered()) {
            dialogVPanel.add(new Label(ClientI18nHelper.CONSTANTS.serverRebootNotRegistered()));
            dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
            dialogVPanel.add(new Button(ClientI18nHelper.CONSTANTS.register(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ServerRestartDialog.this.close();
                    ClientUserService.getInstance().promptRegister();
                }
            }));
        }
    }
}
