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

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.NoSuchClipException;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.CommonClipInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:28:42 PM
 */
public class Explosion {
    private static final int REMOVE_ITEM = 10;
    private int frame = -1;
    private long startTime;
    private Rectangle explosionViewRect;
    private Index relativeImageStart;
    private boolean insideViewRect;
    private boolean playing;
    private SyncItem syncItem;
    private ClipInfo clipInfo;
    private Index spriteMapOffset;

    public Explosion(SyncItem syncItem) throws NoSuchClipException {
        this.syncItem = syncItem;
        startTime = System.currentTimeMillis();
        clipInfo = ClientClipHandler.getInstance().getClipInfo(CommonClipInfo.Type.EXPLOSION);
        SoundHandler.getInstance().playClipSound(clipInfo);
        explosionViewRect = Rectangle.generateRectangleFromMiddlePoint(syncItem.getSyncItemArea().getPosition(), clipInfo.getFrameWidth(), clipInfo.getFrameHeight());
    }

    public void prepareRender(long timeStamp, Rectangle viewRect) {
        insideViewRect = viewRect.adjoins(this.explosionViewRect);
        playing = false;
        if (!insideViewRect) {
            return;
        }

        int newFrame = clipInfo.getFrame(timeStamp - startTime);
        playing = newFrame >= 0;
        if (!playing) {
            return;
        }
        if (newFrame == frame) {
            return;
        }
        frame = newFrame;
        relativeImageStart = explosionViewRect.getStart().sub(viewRect.getStart());
        spriteMapOffset = clipInfo.getSpriteMapOffset(frame);
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isInViewRect() {
        return insideViewRect;
    }

    public SyncItem getSyncItem() {
        return syncItem;
    }

    public boolean isItemVisible() {
        return frame <= REMOVE_ITEM;
    }

    public ClipInfo getClipInfo() {
        return clipInfo;
    }

    public int getSpriteMapXOffset() {
        return spriteMapOffset.getX();
    }

    public int getSpriteMapYOffset() {
        return spriteMapOffset.getY();
    }

    public int getRelativeImageStartX() {
        return relativeImageStart.getX();
    }

    public int getRelativeImageStartY() {
        return relativeImageStart.getY();
    }
}
