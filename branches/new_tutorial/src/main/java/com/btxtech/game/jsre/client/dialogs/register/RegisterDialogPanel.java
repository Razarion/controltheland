package com.btxtech.game.jsre.client.dialogs.register;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.common.CommonJava;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegisterDialogPanel extends Composite implements VerificationRequestField.ValidListener {

    private static RegisterDialogPanelUiBinder uiBinder = GWT.create(RegisterDialogPanelUiBinder.class);
    @UiField(provided = true)
    NickNameField nickNameField;
    @UiField
    TextBox emailField;
    @UiField
    TextBox confirmEmailField;
    @UiField
    PasswordTextBox passwordField;
    @UiField
    PasswordTextBox confirmPasswordField;
    @UiField
    Button normalRegisterButton;
    @UiField
    Button facebookRegisterButton;

    interface RegisterDialogPanelUiBinder extends UiBinder<Widget, RegisterDialogPanel> {
    }

    public RegisterDialogPanel() {
        nickNameField = new NickNameField(this);
        initWidget(uiBinder.createAndBindUi(this));
        nickNameField.checkName();
    }

    @UiHandler("normalRegisterButton")
    void onNormalRegisterButtonClick(ClickEvent event) {
        if (nickNameField.getText().isEmpty() || emailField.getText().isEmpty() || confirmEmailField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationFilled()), DialogManager.Type.STACK_ABLE);
            return;
        }

        if (!CommonJava.isValidEmail(emailField.getText())) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationEmailNotValid()), DialogManager.Type.STACK_ABLE);
            return;
        }

        if (!emailField.getText().equals(confirmEmailField.getText())) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationEmailMatch()), DialogManager.Type.STACK_ABLE);
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.registrationFailed(), ClientI18nHelper.CONSTANTS.registrationMatch()), DialogManager.Type.STACK_ABLE);
            return;
        }

        normalRegisterButton.setEnabled(false);
        ClientUserService.getInstance().proceedNormalRegister(nickNameField.getText(), passwordField.getText(), confirmPasswordField.getText(), emailField.getText());
    }

    @UiHandler("facebookRegisterButton")
    void onFacebookRegisterButtonClick(ClickEvent event) {
        ClientUserService.getInstance().proceedFacebookRegister();
    }

    @Override
    public void onValidStateChanged(boolean isValid) {
        normalRegisterButton.setEnabled(isValid);
    }

    public void enableRegisterButton() {
        nickNameField.checkName();
    }

    public void setFocusOnRegisterButton() {
        normalRegisterButton.setFocus(true);
    }

}
