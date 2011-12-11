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

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 * User: beat
 * Date: 30.12.2009
 * Time: 12:48:08
 */
public class MuzzleFlash {
    public static final long MILIS_SHOW_TIME = 100;
    private Index normCenter;
    private ClientSyncItem clientSyncItem;
    private double angel;
    private long time;
    private Canvas canvas;
    private Context2d context2d;

    public MuzzleFlash(ClientSyncItem clientSyncItem) throws ItemDoesNotExistException {
        time = System.currentTimeMillis();
        this.clientSyncItem = clientSyncItem;
        BaseItemType baseItemType = clientSyncItem.getSyncBaseItem().getBaseItemType();
        WeaponType weaponType = baseItemType.getWeaponType();

        SoundHandler.playMuzzleFlashSound(baseItemType);

        angel = clientSyncItem.getSyncBaseItem().getSyncItemArea().getAngel();
        angel = baseItemType.getBoundingBox().getAllowedAngel(angel);
        int imageNr = baseItemType.getBoundingBox().angelToImageNr(angel);
        //tmpAngel += ImageHandler.QUARTER_RADIANT;
        //tmpAngel = MathHelper.normaliseAngel(tmpAngel);
        Index muzzleStart = baseItemType.getWeaponType().getMuzzleFiresPosition(imageNr);
        Index absoluteMuzzleStart = clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition().add(muzzleStart);
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;
        if (weaponType.stretchMuzzleFlashToTarget()) {
            /*   TODO  SyncItem target = ItemContainer.getInstance().getItem(clientSyncItem.getSyncBaseItem().getSyncWeapon().getTarget());
        int distance = target.getSyncItemArea().getPosition().getDistance(center);
        x = (int) (center.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0));
        y = center.getY() - distance;
        width = weaponType.getMuzzleFlashWidth();
        height = distance;  */
        } else {
            x = (int) (absoluteMuzzleStart.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0));
            y = absoluteMuzzleStart.getY() - weaponType.getMuzzleFlashLength();
            width = weaponType.getMuzzleFlashWidth();
            height = weaponType.getMuzzleFlashLength();
        }
        if (x < 0 || y < 0 || width < 0 || height < 0) {
            return;
        }

        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle = rectangle.getSurroundedRectangle(absoluteMuzzleStart, angel);

        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("MuzzleFlash: Canvas not supported.");
        }
        canvas.setCoordinateSpaceWidth(rectangle.getWidth());
        canvas.setCoordinateSpaceHeight(rectangle.getHeight());
        context2d = canvas.getContext2d();

        MapWindow.getAbsolutePanel().add(canvas,
                rectangle.getStart().getX() - TerrainView.getInstance().getViewOriginLeft(),
                rectangle.getStart().getY() - TerrainView.getInstance().getViewOriginTop());

        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_MUZZLE_FLASH);
        normCenter = absoluteMuzzleStart.sub(rectangle.getStart());
        handlerImage(clientSyncItem.getSyncBaseItem().getBaseItemType());
    }

    private void handlerImage(BaseItemType baseItemType) {
        ImageLoader.loadImages(new String[]{ImageHandler.getMuzzleFlashImageUrl(baseItemType)}, new ImageLoader.CallBack() {
            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                if (imageElements.length != 1) {
                    throw new IllegalArgumentException("MuzzleFlash: Wrong image count received: " + imageElements.length);
                }
                WeaponType weaponType = clientSyncItem.getSyncBaseItem().getSyncWeapon().getWeaponType();
                try {
                    context2d.translate(normCenter.getX(), normCenter.getY());
                    context2d.rotate(-angel);
                    context2d.drawImage(imageElements[0], -Math.round(weaponType.getMuzzleFlashWidth() / 2.0), -weaponType.getMuzzleFlashLength());
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    public void dispose() {
        if (canvas != null) {
            MapWindow.getAbsolutePanel().remove(canvas);
        }
    }

    public boolean isTimeUp() {
        return System.currentTimeMillis() >= time + MILIS_SHOW_TIME;
    }
}
