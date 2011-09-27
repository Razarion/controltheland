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

package com.btxtech.game.jsre.client.effects;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:16:31 PM
 */
public class ExplosionHandler {
    public static final int TIMER_DELAY = 60;
    private static final ExplosionHandler INSTANCE = new ExplosionHandler();
    final private ArrayList<ExplosionFrame> explosionFrames = new ArrayList<ExplosionFrame>();
    private Timer timer;

    public static ExplosionHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ExplosionHandler() {
        timer = new Timer() {
            @Override
            public void run() {
                try {
                    handlerFrames();
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                }
            }
        };
    }

    private void handlerFrames() {
        synchronized (explosionFrames) {
            Iterator<ExplosionFrame> iterator = explosionFrames.iterator();
            while (iterator.hasNext()) {
                ExplosionFrame explosionFrame = iterator.next();
                try {
                    if (explosionFrame.tick()) {
                        iterator.remove();
                    }
                } catch (Throwable t) {
                    GwtCommon.handleException(t);
                    iterator.remove();
                }
            }
            if (!explosionFrames.isEmpty()) {
                timer.schedule(TIMER_DELAY);
            }
        }
    }

    public void terminateWithExplosion(ClientSyncItemView clientSyncItemView) {
        ExplosionFrame explosionFrame = new ExplosionFrame(clientSyncItemView);

        synchronized (explosionFrames) {
            explosionFrames.add(explosionFrame);
            if (explosionFrames.size() < 2) {
                timer.schedule(TIMER_DELAY);
            }
        }
    }

}