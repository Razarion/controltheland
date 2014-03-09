package com.btxtech.game.jsre.client.dialogs.crystals;

import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;

/**
 * User: beat
 * Date: 23.02.14
 * Time: 15:20
 */
public abstract class AbstractBuyCrystalsDialog extends Dialog {
    public AbstractBuyCrystalsDialog(String title) {
        super(title);
    }

    protected HTML createHtml(String htmlString, int fontSite) {
        HTML html = new HTML(htmlString);
        html.getElement().getStyle().setColor("#C7C4BB");
        html.getElement().getStyle().setFontSize(fontSite, Style.Unit.PX);
        html.getElement().getStyle().setProperty("fontFamily", "Arial, Helvetica, sans-serif");
        return html;
    }

    protected static boolean checkShowDialog(String title, String registeredVerified, String registered) {
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            return true;
        } else if (ClientUserService.getInstance().isRegistered()) {
            DialogManager.showDialog(new MessageDialog(title, registeredVerified), DialogManager.Type.STACK_ABLE);
            return false;
        } else {
            DialogManager.showDialog(new MessageDialog(title, registered, true), DialogManager.Type.STACK_ABLE);
            return false;
        }
    }

}
