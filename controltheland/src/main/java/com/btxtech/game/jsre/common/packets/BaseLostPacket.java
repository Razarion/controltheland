package com.btxtech.game.jsre.common.packets;

import com.btxtech.game.jsre.client.common.info.RealGameInfo;

/**
 * User: beat
 * Date: 03.05.13
 * Time: 11:13
 */
public class BaseLostPacket extends Packet {
    private RealGameInfo realGameInfo;

    public RealGameInfo getRealGameInfo() {
        return realGameInfo;
    }

    public void setRealGameInfo(RealGameInfo realGameInfo) {
        this.realGameInfo = realGameInfo;
    }
}
