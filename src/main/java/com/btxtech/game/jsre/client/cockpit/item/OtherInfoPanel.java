package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.dialogs.guild.MembershipRequestPanel;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat Date: 18.08.12 Time: 13:04
 */
public class OtherInfoPanel extends Composite {
    private static OwnInfoPanelUiBinder uiBinder = GWT.create(OwnInfoPanelUiBinder.class);
    @UiField(provided = true)
    Image image;
    @UiField
    Label itemTypeName;
    @UiField
    Label type;
    @UiField
    HTML itemTypeDescr;
    @UiField
    Label baseName;
    @UiField
    Image friendImage;
    @UiField
    Image enemyImage;
    @UiField
    Label guildName;
    @UiField
    Button inviteGuildButton;
    @UiField
    Button requestMembership;
    private SimpleBase simpleBase;
    private SimpleGuild simpleGuild;
    private String enemyName;

    interface OwnInfoPanelUiBinder extends UiBinder<Widget, OtherInfoPanel> {
    }

    public OtherInfoPanel(SyncItem syncItem) {
        image = ImageHandler.getItemTypeImage(syncItem.getItemType(), 50, 50);
        initWidget(uiBinder.createAndBindUi(this));
        GwtCommon.preventDragImage(image);
        if (Game.isDebug()) {
            itemTypeName.setText(ClientI18nHelper.getLocalizedString(syncItem.getItemType().getI18Name()) + " {" + syncItem.getId() + "}");
        } else {
            itemTypeName.setText(ClientI18nHelper.getLocalizedString(syncItem.getItemType().getI18Name()));
        }
        itemTypeDescr.setHTML(ClientI18nHelper.getLocalizedString(syncItem.getItemType().getDescription()));
        friendImage.setVisible(false);
        enemyImage.setVisible(false);
        inviteGuildButton.setVisible(false);
        requestMembership.setVisible(false);
        guildName.setVisible(false);
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isBot(syncBaseItem.getBase())) {
                type.setText(ClientI18nHelper.CONSTANTS.botEnemy());
                enemyImage.setVisible(true);
            } else if (ClientBase.getInstance().isEnemy(syncBaseItem)) {
                enemyName = ClientBase.getInstance().getBaseName(syncBaseItem.getBase());
                simpleBase = syncBaseItem.getBase();
                type.setText(ClientI18nHelper.CONSTANTS.playerEnemy());
                enemyImage.setVisible(true);
                simpleGuild = ClientBase.getInstance().getGuild(simpleBase);
                if (simpleGuild != null) {
                    if (!ClientBase.getInstance().isGuildMember()) {
                        requestMembership.setVisible(true);
                    }
                    guildName.setVisible(true);
                    guildName.setText(simpleGuild.getName());
                } else {
                    if (ClientBase.getInstance().isGuildMember()) {
                        inviteGuildButton.setVisible(true);
                    }
                }
            } else {
                type.setText(ClientI18nHelper.CONSTANTS.itemCockpitGuildMember());
                friendImage.setVisible(true);
            }
            baseName.setText(ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
        } else if (syncItem instanceof SyncResourceItem) {
            baseName.setVisible(false);
            type.setVisible(false);
        } else if (syncItem instanceof SyncBoxItem) {
            baseName.setVisible(false);
            type.setVisible(false);
        }
    }

    @UiHandler("inviteGuildButton")
    void onInviteGuildButtonClick(ClickEvent event) {
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            if (ClientBase.getInstance().isAbandoned(simpleBase)) {
                DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.guildInvitationBaseAbandoned(enemyName)), DialogManager.Type.QUEUE_ABLE);
            } else {
                Connection.getInstance().inviteGuildMember(simpleBase, enemyName);
            }
        } else if (ClientUserService.getInstance().isRegistered()) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.guildsOnlyRegisteredVerified()), DialogManager.Type.QUEUE_ABLE);
        } else {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.guildsOnlyRegistered()), DialogManager.Type.QUEUE_ABLE);
        }

    }

    @UiHandler("requestMembership")
    void onRequestMembershipClick(ClickEvent event) {
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            DialogManager.showDialog(new MembershipRequestPanel(simpleGuild.getId(), simpleGuild.getName()), DialogManager.Type.STACK_ABLE);
        } else if (ClientUserService.getInstance().isRegistered()) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.guildsOnlyRegisteredVerified()), DialogManager.Type.QUEUE_ABLE);
        } else {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.gildMemberInvited(), ClientI18nHelper.CONSTANTS.guildsOnlyRegistered()), DialogManager.Type.QUEUE_ABLE);
        }
    }

}
