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

package com.btxtech.game.services.utg;

import java.util.Date;

/**
 * User: beat
 * Date: 16.01.2010
 * Time: 12:24:41
 */
public class VisitorInfo {
    private Date date;
    private String sessionId;
    private int pageHits;
    private int enterSetupHits;
    private int enterGameHits;
    private int commands;
    private int missions;

    public VisitorInfo(Date date,
                       String sessionId,
                       int pageHits,
                       int enterSetupHits,
                       int enterGameHits,
                       int commands,
                       int missions) {
        this.date = date;
        this.sessionId = sessionId;
        this.pageHits = pageHits;
        this.enterSetupHits = enterSetupHits;
        this.enterGameHits = enterGameHits;
        this.commands = commands;
        this.missions = missions;
    }

    public Date getDate() {
        return date;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getPageHits() {
        return pageHits;
    }

    public int getEnterSetupHits() {
        return enterSetupHits;
    }

    public int getEnterGameHits() {
        return enterGameHits;
    }

    public int getCommands() {
        return commands;
    }

    public int getMissions() {
        return missions;
    }
}
