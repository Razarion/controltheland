package com.btxtech.game.jsre.common.packets;

/**
 * User: beat
 * Date: 25.05.12
 * Time: 20:43
 */
public class BoxPickedPacket extends Packet {
    private String html;

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }
}
