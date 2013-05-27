package com.btxtech.game.jsre.client.dialogs.news;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 20.05.13
 * Time: 13:36
 */
public class NewsDialog extends Dialog {
    public NewsDialog() {
        super(ClientI18nHelper.CONSTANTS.newsDialogTitle());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new NewsPanel());
    }
}
