package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 25.01.13
 * Time: 13:59
 */
public class FacebookRegisterThanksDialog extends Dialog {

    private SimpleUser simpleUser;

    public FacebookRegisterThanksDialog(SimpleUser simpleUser) {
        super(ClientI18nHelper.CONSTANTS.registerThanks());
        this.simpleUser = simpleUser;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new Label(ClientI18nHelper.CONSTANTS.registerThanksLong()));
        // TODO insert conversion tracking here
        /*ScriptInjector.fromUrl(.....).setCallback(
                new Callback<Void, Exception>() {
                    public void onFailure(Exception e) {
                        ClientExceptionHandler.handleException("FacebookRegisterThanksDialog ScriptInjector failed", e);
                    }

                    public void onSuccess(Void result) {
                        try {
                            ...
                        } catch (Exception e) {
                            ClientExceptionHandler.handleException(... + simpleUser, e);
                        }
                    }
                }).inject(); */
    }
}
