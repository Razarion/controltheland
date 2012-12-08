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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 20.02.2010
 * Time: 18:36:49
 */
public class RegisterDialog extends Dialog {
    private static final String REGISTRATION_DIALOG = "<b>Attention:</b> if you continue without registration you will not be able to return to your base after you leave the game.";
    private static final String REGISTRATION_FILLED = "All fields must be filled in";
    private static final String REGISTRATION_MATCH = "Password and confirm password do not match";
    public static final String REGISTRATION_EXISTS = "The user already exists";
    private static Timer timer;
    private TextBox userName;
    private TextBox email;
    private PasswordTextBox password;
    private PasswordTextBox confirmPassword;

    public RegisterDialog() {
        super("Register");
        setShowCloseButton(false);
        getElement().getStyle().setWidth(350, Style.Unit.PX);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.setSpacing(10);
        dialogVPanel.add(new HTML(REGISTRATION_DIALOG));
        // Skip button
        Button skip = new Button("Skip");
        skip.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                hide(true);
            }
        });
        dialogVPanel.add(skip);

        facebookRegister(dialogVPanel);
        normalRegsiter(dialogVPanel);

        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                if (!Connection.getInstance().isRegistered()) {
                    timer.schedule(Connection.getInstance().getGameInfo().getRegisterDialogDelayInS());
                }
            }
        });

    }

    private void facebookRegister(VerticalPanel dialogVPanel) {
        Button button = new Button("<span class='fbconnectbuttonBold'>Connect</span> with <span class='fbconnectbuttonBold'>Facebook</span>");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FacebookUtils.login(RegisterDialog.this);
            }
        });
        button.getElement().setId("fbconnectbutton");
        CaptionPanel captionPanel = new CaptionPanel("Register via Facebook");
        captionPanel.add(button);
        dialogVPanel.add(captionPanel);
    }

    private void normalRegsiter(VerticalPanel dialogVPanel) {
        FlexTable grid = new FlexTable();
        grid.setWidget(0, 0, new Label("User name"));
        userName = new TextBox();
        grid.setWidget(0, 1, userName);
        grid.setWidget(1, 0, new Label("Email"));
        email = new TextBox();
        grid.setWidget(1, 1, email);
        grid.setWidget(2, 0, new Label("Password"));
        password = new PasswordTextBox();
        grid.setWidget(2, 1, password);
        grid.setWidget(3, 0, new Label("Confirm password"));
        confirmPassword = new PasswordTextBox();
        grid.setWidget(3, 1, confirmPassword);
        Button regsiter = new Button("Register");
        grid.setWidget(4, 0, regsiter);
        regsiter.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                register();
            }
        });
        grid.getFlexCellFormatter().setColSpan(4, 0, 2);
        grid.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

        CaptionPanel captionPanel = new CaptionPanel("Direct registration");
        captionPanel.add(grid);
        dialogVPanel.add(captionPanel);
    }

    private void register() {
        if (userName.getText().isEmpty() || password.getText().isEmpty() || confirmPassword.getText().isEmpty()) {
            DialogManager.showDialog(new MessageDialog("Registration failed", REGISTRATION_FILLED), DialogManager.Type.STACK_ABLE);
            return;
        }

        if (!password.getText().equals(confirmPassword.getText())) {
            DialogManager.showDialog(new MessageDialog("Registration failed", REGISTRATION_MATCH), DialogManager.Type.STACK_ABLE);
            return;
        }

        Connection.getMovableServiceAsync().register(userName.getText(), password.getText(), confirmPassword.getText(), email.getText(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof UserAlreadyExistsException) {
                    DialogManager.showDialog(new MessageDialog("Registration failed", REGISTRATION_EXISTS), DialogManager.Type.STACK_ABLE);
                } else if (throwable instanceof PasswordNotMatchException) {
                    DialogManager.showDialog(new MessageDialog("Registration failed", REGISTRATION_MATCH), DialogManager.Type.STACK_ABLE);
                } else {
                    ClientExceptionHandler.handleException(throwable);
                }
            }

            @Override
            public void onSuccess(Void aVoid) {
                Connection.getInstance().setUserName(userName.getText());
                hide(true);
            }
        });
    }

    public static void showDialogRepeating() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (Connection.getInstance().isRegistered()) {
            return;
        }

        timer = new TimerPerfmon(PerfmonEnum.REGISTER_DIALOG) {
            @Override
            public void runPerfmon() {
                if (!Connection.getInstance().isRegistered()) {
                    DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.QUEUE_ABLE);
                }
            }
        };
        timer.schedule(Connection.getInstance().getGameInfo().getRegisterDialogDelayInS());
    }

}