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
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
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
    private long time;
    private Canvas canvas;
    private Context2d context2d;
    private double muzzleRotationAngel;

    public MuzzleFlash(ClientSyncItem clientSyncItem, int muzzleFlashNr) throws ItemDoesNotExistException {
        time = System.currentTimeMillis();
        this.clientSyncItem = clientSyncItem;
        BaseItemType baseItemType = clientSyncItem.getSyncBaseItem().getBaseItemType();
        WeaponType weaponType = baseItemType.getWeaponType();

        double angel = clientSyncItem.getSyncBaseItem().getSyncItemArea().getAngel();
        angel = baseItemType.getBoundingBox().getAllowedAngel(angel);
        int imageNr = baseItemType.getBoundingBox().angelToImageNr(angel);
        Index muzzleStart = baseItemType.getWeaponType().getMuzzleFlashPosition(muzzleFlashNr, imageNr);
        Index absoluteMuzzleStart = clientSyncItem.getSyncBaseItem().getSyncItemArea().getPosition().add(muzzleStart);
        int x = (int) (absoluteMuzzleStart.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0));
        int y;
        int width = weaponType.getMuzzleFlashWidth();
        int height;
        if (weaponType.stretchMuzzleFlashToTarget()) {
            SyncItem target = ItemContainer.getInstance().getItem(clientSyncItem.getSyncBaseItem().getSyncWeapon().getTarget());
            int distance = target.getSyncItemArea().getPosition().getDistance(absoluteMuzzleStart);
            y = absoluteMuzzleStart.getY() - distance;
            height = distance;
            muzzleRotationAngel = absoluteMuzzleStart.getAngleToNord(target.getSyncItemArea().getPosition());
        } else {
            y = absoluteMuzzleStart.getY() - weaponType.getMuzzleFlashLength();
            height = weaponType.getMuzzleFlashLength();
            muzzleRotationAngel = angel;
        }
        if (x < 0 || y < 0 || width < 0 || height < 0) {
            return;
        }

        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle = rectangle.getSurroundedRectangle(absoluteMuzzleStart, muzzleRotationAngel);

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
                    context2d.rotate(-muzzleRotationAngel);
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
