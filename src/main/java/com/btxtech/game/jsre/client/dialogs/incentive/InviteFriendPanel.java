package com.btxtech.game.jsre.client.dialogs.incentive;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.dialogs.history.HistoryPanel;
import com.btxtech.game.jsre.client.widget.EmptyTableWidget;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class InviteFriendPanel extends Composite {
    private static InviteFriendPanelUiBinder uiBinder = GWT.create(InviteFriendPanelUiBinder.class);
    @UiField
    TabPanel tabPanel;
    private CellTable.Resources tableRes = GWT.create(HistoryPanel.TableRes.class);
    @UiField(provided = true)
    CellTable<FriendInvitationBonus> completeTable = new CellTable<FriendInvitationBonus>(1000, tableRes);
    @UiField
    TextBox emailAddressField;
    @UiField
    Button emailButton;
    @UiField
    TextArea urlField;
    @UiField
    PushButton facebookButton;

    interface InviteFriendPanelUiBinder extends UiBinder<Widget, InviteFriendPanel> {
    }

    public InviteFriendPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        tabPanel.selectTab(0);
        setupCompleteTable();
        urlField.setValue(CmsUtil.generateInviteUrl(ClientUserService.getInstance().getSimpleUser(), CmsUtil.URL_VALUE));
    }

    private void setupCompleteTable() {
        completeTable.setEmptyTableWidget(new EmptyTableWidget(ClientI18nHelper.CONSTANTS.noFriendInvited()));
        // Create name
        completeTable.addColumn(new TextColumn<FriendInvitationBonus>() {
            @Override
            public String getValue(FriendInvitationBonus friendInvitationBonus) {
                return friendInvitationBonus.getUserName();
            }
        }, ClientI18nHelper.CONSTANTS.userName());
        // Level Column
        completeTable.addColumn(new TextColumn<FriendInvitationBonus>() {
            @Override
            public String getValue(FriendInvitationBonus friendInvitationBonus) {
                return Integer.toString(friendInvitationBonus.getLevel());
            }
        }, ClientI18nHelper.CONSTANTS.level());
        // Create crystal bonus
        completeTable.addColumn(new TextColumn<FriendInvitationBonus>() {
            @Override
            public String getValue(FriendInvitationBonus friendInvitationBonus) {
                return Integer.toString(friendInvitationBonus.getCrystalBonus());
            }
        }, ClientI18nHelper.CONSTANTS.crystalBonus());
        if (Connection.getMovableServiceAsync() != null) {
            Connection.getMovableServiceAsync().getFriendInvitationBonuses(new AsyncCallback<List<FriendInvitationBonus>>() {

                @Override
                public void onFailure(Throwable caught) {
                    ClientExceptionHandler.handleException("MovableServiceAsync.getFriendInvitationBonuses() failed", caught);
                }

                @Override
                public void onSuccess(List<FriendInvitationBonus> friendInvitationBonuses) {
                    completeTable.setRowCount(friendInvitationBonuses.size(), true);
                    completeTable.setRowData(0, friendInvitationBonuses);
                }
            });
        }
    }

    public void enableFacebookButton(boolean enabled) {
        facebookButton.setEnabled(enabled);
    }


    @UiHandler("facebookButton")
    void onFacebookButton(ClickEvent event) {
        FacebookUtils.invite();
    }

    @UiHandler("emailButton")
    void onEmailButton(ClickEvent event) {
        if (emailAddressField.getText() != null && !emailAddressField.getText().trim().isEmpty()) {
            if (!CommonJava.isValidEmail(emailAddressField.getText())) {
                DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.inviteFriends(), ClientI18nHelper.CONSTANTS.inviteFriendsEmailNotValid()), DialogManager.Type.STACK_ABLE);
            } else if (Connection.getMovableServiceAsync() != null) {
                emailButton.setEnabled(false);
                Connection.getInstance().sendMailInvite(emailAddressField.getText(), emailButton, emailAddressField);
            }
        }
    }
}
