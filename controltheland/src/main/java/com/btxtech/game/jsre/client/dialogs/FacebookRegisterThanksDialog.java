package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.AdCellHelper;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
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
        ScriptInjector.fromUrl(AdCellHelper.AD_CELL_JS_LIB_URL).setCallback(
                new Callback<Void, Exception>() {
                    public void onFailure(Exception e) {
                        ClientExceptionHandler.handleException("FacebookRegisterThanksDialog ScriptInjector failed", e);
                    }

                    public void onSuccess(Void result) {
                        try {
                            nativeTrackAdCell(Integer.toString(simpleUser.getId()));
                        } catch (Exception e) {
                            ClientExceptionHandler.handleException("Exception in nativeTrackAdCell. userId: " + simpleUser, e);
                        }
                    }
                }).inject();
    }

    native private static void nativeTrackAdCell(String reference)/*-{
        Adcell.user.track({
            'pid': '3111',
            'eventid': '3820',
            'referenz': reference
        });
    }-*/;

}
