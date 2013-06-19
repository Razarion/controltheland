package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.VerificationRequestField;
import com.btxtech.game.jsre.client.common.info.RazarionCostInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CreateGuildPanel extends Composite {

    private static CreateGuildPanelUiBinder uiBinder = GWT.create(CreateGuildPanelUiBinder.class);
    @UiField(provided = true)
    GuildNameField guildName;
    @UiField
    Label razarionCost;

    interface CreateGuildPanelUiBinder extends UiBinder<Widget, CreateGuildPanel> {
    }

    public CreateGuildPanel(RazarionCostInfo razarionCostInfo, VerificationRequestField.ValidListener validListener) {
        guildName = new GuildNameField(validListener);
        initWidget(uiBinder.createAndBindUi(this));
        guildName.checkName();
        razarionCost.setText(ClientI18nHelper.CONSTANTS.createGuildRazarionCost(razarionCostInfo.getCost()));
    }

    public String getGuildName() {
        return guildName.getText();
    }
}
