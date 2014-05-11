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

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.syncObjects.ActiveProjectile;
import com.btxtech.game.jsre.common.gameengine.syncObjects.ActiveProjectileGroup;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * User: beat
 * Date: Jun 20, 2009
 * Time: 1:29:15 PM
 */
public class AttackVisualization {
    public final static int ATTACK_EFFECT_TIMER_DELAY = 50;
    private static final AttackVisualization INSTANCE = new AttackVisualization();
    private Collection<MuzzleFlash> muzzleFlashes = new ArrayList<MuzzleFlash>();
    private Collection<ProjectileVisualization> projectileVisualizations = new ArrayList<ProjectileVisualization>();
    private Collection<Detonation> detonations = new ArrayList<Detonation>();

    public static AttackVisualization getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private AttackVisualization() {
    }

    public void onFiring(SyncBaseItem syncBaseItem, ActiveProjectileGroup projectileGroup) {
        try {
            long timeStamp = System.currentTimeMillis();
            if (syncBaseItem.getSyncItemArea().contains(TerrainView.getInstance().getViewRect())) {
                for (ActiveProjectile activeProjectile : projectileGroup.getProjectiles()) {
                    muzzleFlashes.add(new MuzzleFlash(timeStamp, syncBaseItem, activeProjectile.getMuzzleNr()));
                    projectileVisualizations.add(new ProjectileVisualization(timeStamp, syncBaseItem, activeProjectile));
                }
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleExceptionOnlyOnce(e);
        }
    }

    public void onProjectileDetonation(SyncBaseItem attacker) {
        try {
            if (TerrainView.getInstance().getViewRect().contains(attacker.getSyncWeapon().getProjectileTarget())) {
                detonations.add(new Detonation(System.currentTimeMillis(), attacker));
            }
        } catch (Exception e) {
            ClientExceptionHandler.handleExceptionOnlyOnce(e);
        }
    }

    public Collection<MuzzleFlash> getMuzzleFlashInRegion(long timeStamp, Rectangle viewRect) {
        Collection<MuzzleFlash> muzzleFlashes = new ArrayList<MuzzleFlash>();
        for (Iterator<MuzzleFlash> iterator = this.muzzleFlashes.iterator(); iterator.hasNext(); ) {
            MuzzleFlash attack = iterator.next();
            attack.prepareRender(timeStamp, viewRect);
            if (attack.isPlaying() && attack.isInViewRect()) {
                muzzleFlashes.add(attack);
            } else {
                iterator.remove();
            }
        }
        return muzzleFlashes;
    }

    public Collection<ProjectileVisualization> getProjectilesInRegion(long timeStamp, Rectangle viewRect) {
        Collection<ProjectileVisualization> projectileVisualizations = new ArrayList<ProjectileVisualization>();
        for (Iterator<ProjectileVisualization> iterator = this.projectileVisualizations.iterator(); iterator.hasNext(); ) {
            ProjectileVisualization projectileVisualization = iterator.next();
            projectileVisualization.prepareRender(timeStamp, viewRect);
            if (projectileVisualization.isPlaying() && projectileVisualization.isInViewRect()) {
                projectileVisualizations.add(projectileVisualization);
            } else {
                iterator.remove();
            }
        }
        return projectileVisualizations;
    }

    public Collection<Detonation> getDetonationsInRegion(long timeStamp, Rectangle viewRect) {
        Collection<Detonation> detonations = new ArrayList<Detonation>();
        for (Iterator<Detonation> iterator = this.detonations.iterator(); iterator.hasNext(); ) {
            Detonation detonation = iterator.next();
            detonation.prepareRender(timeStamp, viewRect);
            if (detonation.isPlaying() && detonation.isInViewRect()) {
                detonations.add(detonation);
            } else {
                iterator.remove();
            }
        }
        return detonations;
    }
}
