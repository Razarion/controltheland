package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 03.03.13
 * Time: 19:12
 */
public class ServerRebootMessagePacket extends MessageIdPacket {
    private int rebootInSeconds;
    private int downTimeInMinutes;

    public int getRebootInSeconds() {
        return rebootInSeconds;
    }

    public void setRebootInSeconds(int rebootInSeconds) {
        this.rebootInSeconds = rebootInSeconds;
    }

    public int getDownTimeInMinutes() {
        return downTimeInMinutes;
    }

    public void setDownTimeInMinutes(int downTimeInMinutes) {
        this.downTimeInMinutes = downTimeInMinutes;
    }
}
