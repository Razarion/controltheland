package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.AdCellHelper;
import com.btxtech.game.jsre.client.AdCellProvision;
import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
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
    private AdCellProvision adCellProvision;

    public FacebookRegisterThanksDialog(AdCellProvision adCellProvision) {
        super(ClientI18nHelper.CONSTANTS.registerThanks());
        this.adCellProvision = adCellProvision;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new Label(ClientI18nHelper.CONSTANTS.registerThanksLong()));
        if (adCellProvision.isProvisionExpected()) {
            ScriptInjector.fromUrl(AdCellHelper.AD_CELL_JS_LIB_URL).setCallback(
                    new Callback<Void, Exception>() {
                        public void onFailure(Exception e) {
                            ClientExceptionHandler.handleException("FacebookRegisterThanksDialog ScriptInjector failed", e);
                        }

                        public void onSuccess(Void result) {
                            try {
                                nativeTrackAdCell(Integer.toString(adCellProvision.getSimpleUser().getId()), adCellProvision.getBid());
                            } catch (Exception e) {
                                ClientExceptionHandler.handleException("Exception in nativeTrackAdCell. userId: " + adCellProvision.getSimpleUser().getId() + " bid: " + adCellProvision.getBid(), e);
                            }
                        }
                    }).inject();
        }
    }

    native private static void nativeTrackAdCell(String reference, String bid)/*-{
        Adcell.user.track({
            'pid':'3111',
            'eventid':'3820',
            'referenz':reference,
            'bid':bid
        });
    }-*/;

}
