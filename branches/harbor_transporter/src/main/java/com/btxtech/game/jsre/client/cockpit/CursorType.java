/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client.cockpit;

import com.google.gwt.dom.client.Style;

/**
 * User: beat
 * Date: 01.06.2010
 * Time: 20:31:59
 * <p/>
 * Make an ico file with gimp and rename it to cur
 * Make cur file with RealWorld Cursor Editor 2012.1: http://www.rw-designer.com/cursor-maker
 */
public enum CursorType {
    GO("/images/cursors/go.cur", 15, 16, Style.Cursor.CROSSHAIR, "/images/cursors/nogo.cur", 15, 16, Style.Cursor.POINTER),
    ATTACK("/images/cursors/attack.cur", 15, 16, Style.Cursor.CROSSHAIR, "/images/cursors/noattack.cur", 15, 16, Style.Cursor.POINTER),
    COLLECT("/images/cursors/collect.cur", 15, 16, Style.Cursor.CROSSHAIR, "/images/cursors/nocollect.cur", 15, 16, Style.Cursor.POINTER),
    LOAD("/images/cursors/load.cur", 16, 30, Style.Cursor.S_RESIZE, "/images/cursors/noload.cur", 16, 30, Style.Cursor.POINTER),
    UNLOAD("/images/cursors/unload.cur", 15, 1, Style.Cursor.N_RESIZE, "/images/cursors/nounload.cur", 15, 1, Style.Cursor.POINTER),
    SELL("/images/cursors/sell.cur", 15, 16, Style.Cursor.POINTER, "/images/cursors/nosell.cur", 15, 16, Style.Cursor.DEFAULT),
    FINALIZE_BUILD("/images/cursors/finalizebuild.cur", 15, 16, Style.Cursor.POINTER, "/images/cursors/nofinalizebuild.cur", 15, 16, Style.Cursor.DEFAULT),
    GUILD_MEMBER("/images/cursors/noattack.cur", 15, 16, Style.Cursor.POINTER, "/images/cursors/noattack.cur", 15, 16, Style.Cursor.POINTER),
    PICKUP("/images/cursors/pickup.cur", 15, 16, Style.Cursor.CROSSHAIR, "/images/cursors/nopickup.cur", 15, 16, Style.Cursor.POINTER);
    private String url;
    private Style.Cursor alternativeDefault;
    private String noUrl;
    private Style.Cursor noAlternativeDefault;
    private int hotSpotX;
    private int hotSpotY;
    private int hotSpotNoX;
    private int hotSpotNoY;

    CursorType(String url, int hotSpotX, int hotSpotY, Style.Cursor alternativeDefault, String noUrl, int hotSpotNoX, int hotSpotNoY, Style.Cursor noAlternativeDefault) {
        this.url = url;
        this.hotSpotX = hotSpotX;
        this.hotSpotY = hotSpotY;
        this.alternativeDefault = alternativeDefault;
        this.hotSpotNoX = hotSpotNoX;
        this.hotSpotNoY = hotSpotNoY;
        this.noUrl = noUrl;
        this.noAlternativeDefault = noAlternativeDefault;
    }

    public String getUrl() {
        return url;
    }

    public Style.Cursor getAlternativeDefault() {
        return alternativeDefault;
    }

    public String getNoUrl() {
        return noUrl;
    }

    public Style.Cursor getNoAlternativeDefault() {
        return noAlternativeDefault;
    }

    public int getHotSpotX() {
        return hotSpotX;
    }

    public int getHotSpotY() {
        return hotSpotY;
    }

    public int getHotSpotNoX() {
        return hotSpotNoX;
    }

    public int getHotSpotNoY() {
        return hotSpotNoY;
    }
}
