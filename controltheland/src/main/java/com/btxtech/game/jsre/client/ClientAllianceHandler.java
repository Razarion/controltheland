package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.dialogs.AllianceOfferDialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 27.04.12
 * Time: 13:00
 */
public class ClientAllianceHandler {
    private static final ClientAllianceHandler INSTANCE = new ClientAllianceHandler();
    private static Logger log = Logger.getLogger(ClientAllianceHandler.class.getName());

    /**
     * Singleton
     */
    private ClientAllianceHandler() {
    }

    public static ClientAllianceHandler getInstance() {
        return INSTANCE;
    }

    public static void offerAlliance(int baseId) {
        try {
            SimpleBase simpleBase = ClientBase.getInstance().getSimpleBase4Id(baseId);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Exception in offerAlliance");
        }
    }

    public void checkForPendingAlliances() {
        RealGameInfo realGameInfo = (RealGameInfo) Connection.getInstance().getGameInfo();
        if (realGameInfo.getAllianceOffers() != null) {
            for (AllianceOfferPacket allianceOfferPacket : realGameInfo.getAllianceOffers()) {
                handleAllianceOfferPacket(allianceOfferPacket);
            }
        }
    }

    public void handleAllianceOfferPacket(AllianceOfferPacket allianceOfferPacket) {
        DialogManager.showDialog(new AllianceOfferDialog(allianceOfferPacket.getActorUserName()), DialogManager.Type.QUEUE_ABLE);
    }

    public static native void exportStaticMethod() /*-{
        $wnd.offerAlliance = $entry(@com.btxtech.game.jsre.client.ClientAllianceHandler::offerAlliance(I));
    }-*/;

}
