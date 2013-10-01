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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.renderer.ImageLoaderContainer;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Timer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 12.11.2012
 * Time: 21:23:04
 */
public class AttackVisualisation extends MiniMap {
    private final static String RADAR_ATTACK = "radarAttack.png";
    private final static int MAX_BLINK_COUNT = 4;
    private final static int MAX_BLINK_DELAY = 200;
    private final static int RANGE = 1500;
    private Logger log = Logger.getLogger(AttackVisualisation.class.getName());
    private Timer timer;
    private Map<SyncBaseItem, AttackVisualisationItem> queue = new HashMap<SyncBaseItem, AttackVisualisationItem>();
    private Set<SyncBaseItem> blockedItems = new HashSet<SyncBaseItem>();
    private ImageLoaderContainer<String> imageContainer = new ImageLoaderContainer<String>() {
        @Override
        protected String getUrl(String s) {
            return ImageHandler.getCockpitImageUrl(s);
        }
    };
    private boolean active = true;

    public AttackVisualisation(int width, int height) {
        super(width, height);
    }

    @Override
    protected void render() {
        clear();
        ImageElement imageElement = imageContainer.getImage(RADAR_ATTACK);
        if (imageElement == null) {
            imageContainer.startLoad();
            return;
        }
        for (Iterator<Map.Entry<SyncBaseItem, AttackVisualisationItem>> iterator = queue.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<SyncBaseItem, AttackVisualisationItem> entry = iterator.next();
            entry.getValue().blink();
            if (entry.getValue().isShowing()) {
                Index position = entry.getKey().getSyncItemArea().getPosition();
                int x = absolute2RadarPositionX(position) - imageElement.getWidth() / 2;
                int y = absolute2RadarPositionY(position) - imageElement.getHeight() / 2;
                getContext2d().drawImage(imageElement, x, y);
            }
            if (entry.getValue().isExpired()) {
                entry.getValue().freeBlockedItems(blockedItems);
                iterator.remove();
            }
        }
        if (queue.isEmpty()) {
            stopTimer();
        }
    }

    @Override
    public void cleanup() {
        stopTimer();
        queue.clear();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void startTimer() {
        if (timer == null) {
            timer = new TimerPerfmon(PerfmonEnum.RADAR_ATTACK_VISUALISATION) {

                @Override
                public void runPerfmon() {
                    try {
                        draw();
                    } catch (Throwable t) {
                        log.log(Level.SEVERE, "Exception in AttackVisualisation Timer", t);
                    }
                }
            };
            timer.scheduleRepeating(MAX_BLINK_DELAY);
        }
    }

    public void onwItemUnderAttack(SyncBaseItem target) {
        if (!active) {
            return;
        }
        startTimer();
        if (blockedItems.contains(target)) {
            return;
        }
        if (!queue.containsKey(target)) {
            AttackVisualisationItem nearest = findAttackVisualisationItem(target);
            if (nearest != null) {
                addBlockedItems(nearest, target);
            } else {
                queue.put(target, new AttackVisualisationItem(target));
            }
        }
    }

    private void addBlockedItems(AttackVisualisationItem attackVisualisationItem, SyncBaseItem toBeBlocked) {
        blockedItems.add(toBeBlocked);
        attackVisualisationItem.addBlockedItem(toBeBlocked);
    }

    private AttackVisualisationItem findAttackVisualisationItem(SyncBaseItem target) {
        for (AttackVisualisationItem attackVisualisationItem : queue.values()) {
            if (attackVisualisationItem.isInRange(target)) {
                return attackVisualisationItem;
            }
        }
        return null;
    }

    public void activate(boolean active) {
        this.active = active;
        if (!active) {
            cleanup();
        }
    }

    private class AttackVisualisationItem {
        private boolean showing;
        private int count;
        private Rectangle scope;
        private Set<SyncBaseItem> blockedItems = new HashSet<SyncBaseItem>();

        public AttackVisualisationItem(SyncBaseItem target) {
            scope = Rectangle.generateRectangleFromMiddlePoint(target.getSyncItemArea().getPosition(), RANGE, RANGE);
        }

        public void blink() {
            showing = !showing;
            if (showing) {
                count++;
            }
        }

        public boolean isShowing() {
            return showing && !isExpired();
        }

        private boolean isExpired() {
            return count > MAX_BLINK_COUNT;
        }

        public boolean isInRange(SyncBaseItem target) {
            return scope.contains(target.getSyncItemArea().getPosition());
        }

        public void addBlockedItem(SyncBaseItem toBeBlocked) {
            blockedItems.add(toBeBlocked);
        }

        public void freeBlockedItems(Set<SyncBaseItem> blockedItems) {
            blockedItems.removeAll(this.blockedItems);
        }
    }
}
