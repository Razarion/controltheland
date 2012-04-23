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
 */
public enum CursorType {
    GO("/images/cursors/go.cur", Style.Cursor.CROSSHAIR, "/images/cursors/nogo.cur", Style.Cursor.POINTER),
    ATTACK("/images/cursors/attack.cur", Style.Cursor.CROSSHAIR, "/images/cursors/noattack.cur", Style.Cursor.POINTER),
    COLLECT("/images/cursors/collect.cur", Style.Cursor.CROSSHAIR, "/images/cursors/nocollect.cur", Style.Cursor.POINTER),
    LOAD("/images/cursors/load.cur", Style.Cursor.S_RESIZE, "/images/cursors/noload.cur", Style.Cursor.POINTER),
    UNLOAD("/images/cursors/unload.cur", Style.Cursor.N_RESIZE, "/images/cursors/nounload.cur", Style.Cursor.POINTER),
    SELL("/images/cursors/sell.cur", Style.Cursor.POINTER, "/images/cursors/nosell.cur", Style.Cursor.DEFAULT),
    FINALIZE_BUILD("/images/cursors/finalizebuild.cur", Style.Cursor.POINTER, "/images/cursors/nofinalizebuild.cur", Style.Cursor.DEFAULT),
    ALLIANCE("/images/cursors/alliance.cur", Style.Cursor.POINTER, null, null);
    private String url;
    private Style.Cursor alternativeDefault;
    private String noUrl;
    private Style.Cursor noAlternativeDefault;

    CursorType(String url, Style.Cursor alternativeDefault, String noUrl, Style.Cursor noAlternativeDefault) {
        this.url = url;
        this.alternativeDefault = alternativeDefault;
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
}
