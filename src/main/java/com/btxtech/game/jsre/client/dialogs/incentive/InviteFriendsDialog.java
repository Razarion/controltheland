package com.btxtech.game.jsre.client.dialogs.incentive;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 23.07.13
 * Time: 10:04
 */
public class InviteFriendsDialog extends Dialog {
    private static InviteFriendPanel inviteFriendPanel;

    public InviteFriendsDialog() {
        super(ClientI18nHelper.CONSTANTS.inviteFriends());
    }

    public static void showDialog() {
        if (ClientUserService.getInstance().isRegisteredAndVerified()) {
            DialogManager.showDialog(new InviteFriendsDialog(), DialogManager.Type.STACK_ABLE);
        } else if (ClientUserService.getInstance().isRegistered()) {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.inviteFriends(), ClientI18nHelper.CONSTANTS.inviteFriendsOnlyRegisteredVerified()), DialogManager.Type.STACK_ABLE);
        } else {
            DialogManager.showDialog(new MessageDialog(ClientI18nHelper.CONSTANTS.inviteFriends(), ClientI18nHelper.CONSTANTS.inviteFriendsOnlyRegistered()), DialogManager.Type.STACK_ABLE);
        }
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        inviteFriendPanel = new InviteFriendPanel();
        dialogVPanel.add(inviteFriendPanel);
    }

    @Override
    public void close() {
        inviteFriendPanel = null;
        super.close();
    }

    public static void enableFacebookButton(boolean enabled) {
        if (inviteFriendPanel != null) {
            inviteFriendPanel.enableFacebookButton(enabled);
        }
    }
}
