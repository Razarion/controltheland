package com.btxtech.game.services.user;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 10:12
 */
public interface AllianceService {
    void proposeAlliance(SimpleBase partner);

    void acceptAllianceOffer(String partnerUserName);

    void rejectAllianceOffer(String partnerUserName);

    void breakAlliance(String partnerUserName);

    void restoreAlliances();

    Collection<AllianceOfferPacket> getPendingAllianceOffers();

    Collection<String> getAllAlliances();

    void onBaseCreatedOrDeleted(int userId);

    void onMakeBaseAbandoned(SimpleBase simpleBase);

    void fillAlliancesForFakeBases(BaseAttributes fakeBaseAttributes, HashMap<SimpleBase, BaseAttributes> allFakeBaseAttributes, UserState userState, int planetId);

    Set<SimpleBase> getAllianceBases(UserState userState, PlanetInfo planetInfo);
}
