package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.VerificationRequestField;
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
    Label crystalCostLabel;

    interface CreateGuildPanelUiBinder extends UiBinder<Widget, CreateGuildPanel> {
    }

    public CreateGuildPanel(int crystalCost, VerificationRequestField.ValidListener validListener) {
        guildName = new GuildNameField(validListener);
        initWidget(uiBinder.createAndBindUi(this));
        guildName.checkName();
        crystalCostLabel.setText(ClientI18nHelper.CONSTANTS.createGuildCrystalCost(crystalCost));
    }

    public String getGuildName() {
        return guildName.getText();
    }
}
