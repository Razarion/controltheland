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
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 * User: beat
 * Date: Jul 1, 2009
 * Time: 2:28:42 PM
 */
public class ExplosionFrame {
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
    private Canvas canvas;
    private Context2d context2d;

    public ExplosionFrame(ClientSyncItem clientSyncItem) {
        this.clientSyncItemView = clientSyncItem;
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("MuzzleFlash: Canvas not supported.");
        }
        frame = 1;
        if (clientSyncItem.isSyncProjectileItem()) {
            width = clientSyncItem.getSyncProjectileItem().getProjectileItemType().getExplosionRadius() * 2;
            //noinspection SuspiciousNameCombination
            height = width;
        } else {
            width = clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageWidth();
            height = clientSyncItem.getSyncItem().getItemType().getBoundingBox().getImageHeight();
        }
        SoundController soundController = new SoundController();
        Sound sound = soundController.createSound(Sound.MIME_TYPE_AUDIO_MPEG, "/sounds/explosion.mp3");
        sound.play();
        Index relativeMiddle = TerrainView.getInstance().toRelativeIndex(clientSyncItem.getSyncItem().getSyncItemArea().getPosition());
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        context2d = canvas.getContext2d();
        MapWindow.getAbsolutePanel().add(canvas, relativeMiddle.getX() - width / 2, relativeMiddle.getY() - height / 2);
        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_EXPLOSION);
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
            context2d.clearRect(0, 0, width, height);

            double frameFactor = (double) frame / (double) COUNT;

            try {
                context2d.drawImage(imageElement,
                        0, 0, // Source pos
                        80, 80, // Source size
                        middleX - middleX * frameFactor, middleY - middleY * frameFactor,// Canvas pos
                        width * frameFactor, height * frameFactor // Canvas size
                );
            } catch (Throwable throwable) {
                GwtCommon.handleException(throwable);
            }

            if ((double) frame / COUNT >= START_ALPHA) {
                double alpha = (COUNT - (double) frame) / (COUNT * START_ALPHA);
                context2d.setGlobalAlpha(alpha);
            }

            //stroke();
            if (frame == REMOVE_FRAME) {
                clientSyncItemView.dispose();
            }
            return false;
        } else {
            MapWindow.getAbsolutePanel().remove(canvas);
            return true;
        }

    }

}
