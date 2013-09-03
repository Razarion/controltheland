package com.btxtech.game.jsre.client.dialogs.register;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 27.08.13
 * Time: 22:25
 */
public class RegisterDialog extends Dialog implements RegisterButtonDialog{
    private RegisterDialogPanel registerDialogPanel;

    public RegisterDialog() {
        super(ClientI18nHelper.CONSTANTS.register());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        registerDialogPanel = new RegisterDialogPanel();
        dialogVPanel.add(registerDialogPanel);
    }

    @Override
    public void enableRegisterButton() {
        registerDialogPanel.enableRegisterButton();
    }

    @Override
    public void setFocusOnRegisterButton() {
        registerDialogPanel.setFocusOnRegisterButton();
    }
}
