package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogUiBinderWrapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;
import com.google.gwt.user.client.ui.Label;

public class ChangeRankPanel extends DialogUiBinderWrapper {

    private static ChangeRankPanelUiBinder uiBinder = GWT.create(ChangeRankPanelUiBinder.class);
    @UiField
    RadioButton managementRadioButton;
    @UiField
    RadioButton memberRadioButton;
    @UiField Label textLabel;
    private Logger log = Logger.getLogger(MyGuildPanel.class.getName());
    private GuildMemberInfo guildMemberInfo;

    @Override
    public String getDialogTitle() {
        return ClientI18nHelper.CONSTANTS.changeRank();
    }

    interface ChangeRankPanelUiBinder extends UiBinder<Widget, ChangeRankPanel> {
    }

    public ChangeRankPanel(final GuildMemberInfo guildMemberInfo) {
        this.guildMemberInfo = guildMemberInfo;
        initWidget(uiBinder.createAndBindUi(this));
        setRank(guildMemberInfo.getRank());
        textLabel.setText(ClientI18nHelper.CONSTANTS.changeRankText(guildMemberInfo.getDetailedUser().getSimpleUser().getName()));
    }

    @Override
    public void init(Dialog dialog) {
        dialog.setShowYesButton(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Connection.getInstance().saveGuildMemberRank(guildMemberInfo.getDetailedUser().getSimpleUser().getId(), getRank());
            }
        }, ClientI18nHelper.CONSTANTS.save());
    }

    private GuildMemberInfo.Rank getRank() {
        if (managementRadioButton.getValue()) {
            return GuildMemberInfo.Rank.MANAGEMENT;
        } else if (memberRadioButton.getValue()) {
            return GuildMemberInfo.Rank.MEMBER;
        } else {
            log.warning("ChangeRankPanel.getRank() managementRadioButton.getValue() == false && memberRadioButton.getValue()");
            return GuildMemberInfo.Rank.MEMBER;
        }
    }

    private void setRank(GuildMemberInfo.Rank rank) {
        switch (rank) {
            case MANAGEMENT: {
                managementRadioButton.setValue(true);
                memberRadioButton.setValue(false);
                break;
            }
            case MEMBER:{
                managementRadioButton.setValue(false);
                memberRadioButton.setValue(true);
                break;
            }
            default:{
                managementRadioButton.setValue(false);
                memberRadioButton.setValue(true);
                log.warning("ChangeRankPanel.setRank() unknown rank: " + rank);
            }
        }
    }
}
