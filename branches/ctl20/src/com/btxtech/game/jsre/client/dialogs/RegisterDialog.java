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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.client.utg.missions.HtmlConstants;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 20.02.2010
 * Time: 18:36:49
 */
public class RegisterDialog extends Dialog {
    private TextBox userName;
    private PasswordTextBox password;
    private PasswordTextBox confirmPassword;

    public RegisterDialog() {
        setShowCloseButton(false);
        setupDialog("You are not registered");
        getElement().getStyle().setWidth(300, Style.Unit.PX);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        // Text
        dialogVPanel.add(new HTML(HtmlConstants.REGISTRATION_DIALOG));

        FlexTable grid = new FlexTable();
        Button skip = new Button("Skip");
        grid.setWidget(0, 0, skip);
        skip.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ClientUserTracker.getInstance().onRegisterDialogCloseNoReg();
                closeDialog();
            }
        });
        grid.getFlexCellFormatter().setColSpan(0, 0, 2);
        grid.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        grid.setWidget(1, 0, new HTML("&nbsp")); // Spacer
        grid.setWidget(2, 0, new Label("User name"));
        userName = new TextBox();
        grid.setWidget(2, 1, userName);
        grid.setWidget(3, 0, new Label("Password"));
        password = new PasswordTextBox();
        grid.setWidget(3, 1, password);
        grid.setWidget(4, 0, new Label("Confirm password"));
        confirmPassword = new PasswordTextBox();
        grid.setWidget(4, 1, confirmPassword);
        Button regsiter = new Button("Register");
        grid.setWidget(5, 0, regsiter);
        regsiter.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                register();
            }
        });
        grid.getFlexCellFormatter().setColSpan(5, 0, 2);
        grid.getFlexCellFormatter().setHorizontalAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER);
        dialogVPanel.add(grid);
        ClientUserTracker.getInstance().onRegisterDialogOpen();
    }

    private void register() {
        if (userName.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
            MessageDialog.show(HtmlConstants.REGISTRATION_FAILED, HtmlConstants.REGISTRATION_FILLED);
            return;
        }

        if (!password.getText().equals(confirmPassword.getText())) {
            MessageDialog.show(HtmlConstants.REGISTRATION_FAILED, HtmlConstants.REGISTRATION_MATCH);
            return;
        }

        Connection.getMovableServiceAsync().register(userName.getText(), password.getText(), confirmPassword.getText(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof UserAlreadyExistsException) {
                    MessageDialog.show(HtmlConstants.REGISTRATION_FAILED, HtmlConstants.REGISTRATION_EXISTS);
                } else if (throwable instanceof PasswordNotMatchException) {
                    MessageDialog.show(HtmlConstants.REGISTRATION_FAILED, HtmlConstants.REGISTRATION_MATCH);
                } else {
                    GwtCommon.handleException(throwable);
                }
            }

            @Override
            public void onSuccess(Void aVoid) {
                ClientUserTracker.getInstance().onRegisterDialogCloseReg();
                closeDialog();
                Connection.getInstance().setRegistered(true);
            }
        });
    }

    private void closeDialog() {
        hide(true);
    }

    public static void showDialogWithDelay(int delay) {
        if (Connection.getInstance().isRegistered()) {
            return;
        }

        Timer timer = new Timer() {
            @Override
            public void run() {
                if (!Connection.getInstance().isRegistered()) {
                    new RegisterDialog();
                }
            }
        };
        timer.schedule(1000 * delay);
    }

}