package com.btxtech.game.jsre.client.dialogs.register;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.common.CmsUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 07.12.12
 * Time: 16:35
 */
public class LoginDialog extends Dialog {
    private TextBox name;
    private Button loginButton;

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
        loginButton = new Button(ClientI18nHelper.CONSTANTS.login());
        grid.setWidget(2, 0, loginButton);
        loginButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                loginButton.setEnabled(false);
                ClientUserService.getInstance().proceedLogin(name.getText(), password.getText());
            }
        });
        grid.getFlexCellFormatter().setColSpan(2, 0, 2);
        grid.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);
        // Forgot password
        grid.setWidget(3, 0, new Anchor(ClientI18nHelper.CONSTANTS.forgotPassword(), GwtCommon.getPredefinedUrl(CmsUtil.CmsPredefinedPage.FORGOT_PASSWORD_REQUEST)));
        grid.getFlexCellFormatter().setColSpan(3, 0, 2);
        grid.getFlexCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_LEFT);

        dialogVPanel.add(grid);
    }

    @Override
    protected void setupDialog() {
        super.setupDialog();
        // Field must be added before focus can be set
        name.setFocus(true);
    }

    public void setLoginButtonEnabled() {
        loginButton.setEnabled(true);
    }
}
