package com.btxtech.game.jsre.client.dialogs.register;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 27.08.13
 * Time: 22:29
 */
public class FacebookNickNameDialog extends Dialog implements RegisterButtonDialog {
    private FacebookNickNamePanel facebookNickNamePanel;

    public FacebookNickNameDialog() {
        super(ClientI18nHelper.CONSTANTS.register());
        setShowYesButton(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setYesButtonEnabled(false);
                ClientUserService.getInstance().onFacebookNicknameChosen(facebookNickNamePanel.getNickNameField().getText());
            }
        }, ClientI18nHelper.CONSTANTS.register());

    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        facebookNickNamePanel = new FacebookNickNamePanel(new VerificationRequestField.ValidListener() {
            @Override
            public void onValidStateChanged(boolean isValid) {
                setYesButtonEnabled(isValid);
            }
        });
        dialogVPanel.add(facebookNickNamePanel);
    }

    @Override
    public void enableRegisterButton() {
        setYesButtonEnabled(true);
    }

    @Override
    public void setFocusOnRegisterButton() {
        facebookNickNamePanel.getNickNameField().setFocus(true);
    }
}
