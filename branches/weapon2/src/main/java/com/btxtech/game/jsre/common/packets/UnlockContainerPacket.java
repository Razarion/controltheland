package com.btxtech.game.jsre.common.packets;

import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 14:17
 */
public class UnlockContainerPacket extends Packet {
    private UnlockContainer unlockContainer;

    public UnlockContainer getUnlockContainer() {
        return unlockContainer;
    }

    public void setUnlockContainer(UnlockContainer unlockContainer) {
        this.unlockContainer = unlockContainer;
    }

    @Override
    public String toString() {
        return "UnlockContainerPacket{unlockContainer=" + unlockContainer + '}';
    }
}
