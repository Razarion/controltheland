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

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:16:31 PM
 */
public class ExplosionHandler {
    private static final ExplosionHandler INSTANCE = new ExplosionHandler();
    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private Logger log = Logger.getLogger(ExplosionHandler.class.getName());

    public static ExplosionHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ExplosionHandler() {
    }

    public void onExplosion(SyncItem syncItem) {
        if (syncItem.getSyncItemArea().contains(TerrainView.getInstance().getViewRect())) {
            try {
                explosions.add(new Explosion(System.currentTimeMillis(), syncItem));
            } catch (Exception e) {
                log.log(Level.SEVERE, "ExplosionHandler.onExplosion", e);
            }
        }
    }

    public Collection<Explosion> getExplosions(long timeStamp, Rectangle viewRect) {
        Collection<Explosion> explosions = new ArrayList<Explosion>();
        for (Iterator<Explosion> iterator = this.explosions.iterator(); iterator.hasNext(); ) {
            Explosion explosion = iterator.next();
            explosion.prepareRender(timeStamp, viewRect);
            if (explosion.isPlaying() && explosion.isInViewRect()) {
                explosions.add(explosion);
            } else {
                iterator.remove();
            }
        }
        return explosions;
    }

}
