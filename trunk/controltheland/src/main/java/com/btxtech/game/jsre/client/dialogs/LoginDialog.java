package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 07.12.12
 * Time: 16:35
 */
public class LoginDialog extends Dialog {
    private TextBox name;

    public LoginDialog() {
        super(ClientI18nHelper.CONSTANTS.login());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        FlexTable grid = new FlexTable();
        // Name
        name = new TextBox();
        grid.setWidget(0, 0, new Label(ClientI18nHelper.CONSTANTS.userName()));
        grid.setWidget(0, 1, name);
        // Password
        final PasswordTextBox password = new PasswordTextBox();
        grid.setWidget(1, 0, new Label(ClientI18nHelper.CONSTANTS.password()));
        grid.setWidget(1, 1, password);
        // Login button
        final Button loginButton = new Button(ClientI18nHelper.CONSTANTS.login());
        grid.setWidget(2, 0, loginButton);
        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loginButton.setEnabled(false);
                if (Connection.getMovableServiceAsync() != null) {
                    Connection.getMovableServiceAsync().login(name.getText(), password.getText(), new AsyncCallback<SimpleUser>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            loginButton.setEnabled(true);
                            if (caught instanceof LoginFailedException) {
                                DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.loginFailed(), ClientI18nHelper.CONSTANTS.loginFailedText()), DialogManager.Type.STACK_ABLE);
                            } else if (caught instanceof LoginFailedNotVerifiedException) {
                                DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.loginFailed(), ClientI18nHelper.CONSTANTS.loginFailedNotVerifiedText()), DialogManager.Type.STACK_ABLE);
                            } else {
                                ClientExceptionHandler.handleException("LoginDialog.setupPanel() login", caught);
                            }
                        }

                        @Override
                        public void onSuccess(SimpleUser simpleUser) {
                            Connection.getInstance().setSimpleUser(simpleUser);
                            MenuBarCockpit.getInstance().setSimpleUser(simpleUser);
                            LoginDialog.this.hide();
                            Window.Location.reload();
                        }
                    });
                }
            }
        });
        grid.getFlexCellFormatter().setColSpan(2, 0, 2);
        grid.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

        dialogVPanel.add(grid);
    }

    @Override
    protected void setupDialog() {
        super.setupDialog();
        // Field must be added before focus can be set
        name.setFocus(true);
    }
}
