package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.services.utg.DbLevel;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 24.07.13
 * Time: 13:30
 */
public interface InvitationService {
    void sendMailInvite(String emailAddress);

    void onFacebookInvite(String fbRequestId, Collection<String> fbUserIds);

    User getHost4FacebookRequest(String request_ids);

    void onLevelUp(UserState userState, DbLevel dbLevel);

    void onUserRegisteredAndVerified(User invitee);

    List<FriendInvitationBonus> getFriendInvitationBonus();
}
