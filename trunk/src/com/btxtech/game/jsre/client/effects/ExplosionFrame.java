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

import com.allen_sauer.gwt.voices.client.Sound;
import com.allen_sauer.gwt.voices.client.SoundController;
import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.ExtendedCanvas;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Event;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:28:42 PM
 */
public class ExplosionFrame extends ExtendedCanvas {
    public static final int COUNT = 10;
    public static final double START_ALPHA = 0.80;
    public static final double REMOVE = 0.70;
    public static final int REMOVE_FRAME = (int) (REMOVE * COUNT);
    private int frame = 0;
    private ClientSyncItem clientSyncItemView;
    private int middleX;
    private int middleY;
    private int width;
    private int height;
    private ImageElement imageElement;

    public ExplosionFrame(ClientSyncItem clientSyncItem) {
        this.clientSyncItemView = clientSyncItem;
        frame = 1;
        width = clientSyncItem.getSyncItem().getItemType().getWidth();
        height = clientSyncItem.getSyncItem().getItemType().getHeight();
        SoundController soundController = new SoundController();
        Sound sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG, "/sounds/explosion.mp3");
        sound.play();
        Index relativeMiddle = TerrainView.getInstance().toRelativeIndex(clientSyncItem.getSyncItem().getPosition());
        MapWindow.getAbsolutePanel().add(this, relativeMiddle.getX() - width / 2, relativeMiddle.getY() - height / 2);
        setPixelSize(width, height);
        sinkEvents(Event.ONMOUSEMOVE);
        resize(width, height);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_EXPLOSION);
        middleX = width / 2;
        middleY = height / 2;

        ImageLoader.loadImages(new String[]{ImageHandler.getExplosion()}, new ImageLoader.CallBack() {

            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    imageElement = imageElements[0];
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });

    }

    public boolean tick() {
        if (imageElement == null) {
            return false;
        }
        frame++;
        if (frame <= COUNT) {
            clear();

            double frameFactor = (double) frame / (double) COUNT;

            drawImage(imageElement,
                    0, 0, // Source pos
                    80, 80, // Source size
                    middleX - middleX * frameFactor, middleY - middleY * frameFactor,// Canvas pos
                    width * frameFactor, height * frameFactor // Canvas size
            );

            if ((double) frame / COUNT >= START_ALPHA) {
                double alpha = (COUNT - (double) frame) / (COUNT * START_ALPHA);
                setGlobalAlpha(alpha);
            }

            //stroke();
            if (frame == REMOVE_FRAME) {
                clientSyncItemView.dispose();
            }
            return false;
        } else {
            MapWindow.getAbsolutePanel().remove(this);
            return true;
        }

    }

}
