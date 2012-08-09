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

import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:28:42 PM
 */
public class Explosion {
    private static final int DISPLAY_TIME = 50;
    private static final int COUNT = 20;
    private static final int FADEOUT_FRAME = 16;
    private static final int REMOVE_ITEM = 10;
    private int frame = -1;
    private int width;
    private int height;
    private double alpha;
    private long startTime;
    private Rectangle viewRect;
    private Index absoluteMiddle;
    private Index absoluteImageStart;
    private int frameWidth;
    private int frameHeight;
    private SyncItem syncItem;

    public Explosion(SyncItem syncItem) {
        this.syncItem = syncItem;
        startTime = System.currentTimeMillis();
        if (syncItem instanceof SyncProjectileItem) {
            width = ((SyncProjectileItem) syncItem).getProjectileItemType().getExplosionRadius() * 2;
            //noinspection SuspiciousNameCombination
            height = width;
        } else {
            width = (int) (syncItem.getItemType().getItemTypeSpriteMap().getImageWidth() * 1.5);
            height = (int) (syncItem.getItemType().getItemTypeSpriteMap().getImageHeight() * 1.5);
        }
        SoundHandler.getInstance().playItemExplode();
        absoluteMiddle = syncItem.getSyncItemArea().getPosition();
        viewRect = Rectangle.generateRectangleFromMiddlePoint(absoluteMiddle, width * 2, height * 2);
    }

    public void setTimeStamp(long timeStamp) {
        int oldFrame = (int) ((timeStamp - startTime) / DISPLAY_TIME);
        if (frame < 0) {
            frame = 0;
        } else if (frame > COUNT) {
            frame = COUNT;
        }
        if (oldFrame == frame) {
            return;
        }
        frame = oldFrame;

        if (frame >= FADEOUT_FRAME) {
            alpha = (double) (COUNT - frame) / (double) (COUNT - FADEOUT_FRAME);
        } else {
            alpha = 1.0;
        }

        double factor = (double) frame / (double) COUNT;
        frameWidth = (int) (factor * width);
        frameHeight = (int) (factor * height);
        absoluteImageStart = absoluteMiddle.sub(frameWidth / 2, frameHeight / 2);
    }

    public double getAlpha() {
        return alpha;
    }

    public boolean isInTime() {
        return frame < COUNT;
    }

    public boolean isInViewRect(Rectangle viewRect) {
        return viewRect.adjoins(this.viewRect);
    }

    public Index getAbsoluteImageStart() {
        return absoluteImageStart;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }

    public boolean isItemVisible() {
        return frame <= REMOVE_ITEM;
    }
}
