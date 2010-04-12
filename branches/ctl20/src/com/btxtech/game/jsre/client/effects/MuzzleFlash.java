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

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;

/**
 * User: beat
 * Date: 30.12.2009
 * Time: 12:48:08
 */
public class MuzzleFlash {
    public static final long MILIS_SHOW_TIME = 100;
    private Index normCenter;
    private GWTCanvas canvas;
    private ClientSyncBaseItemView clientSyncBaseItemView;
    private double angel;
    private long time;

    public MuzzleFlash(ClientSyncBaseItemView clientSyncBaseItemView) throws ItemDoesNotExistException {
        time = System.currentTimeMillis();
        this.clientSyncBaseItemView = clientSyncBaseItemView;
        SoundHandler.playMuzzleFlashSound(clientSyncBaseItemView.getSyncBaseItem().getBaseItemType());
        Index center = getAbsoluteStartPoint(clientSyncBaseItemView);
        WeaponType weaponType = clientSyncBaseItemView.getSyncBaseItem().getSyncWaepon().getWeaponType();
        int x;
        int y;
        int width;
        int height;

        if (weaponType.stretchMuzzleFlashToTarget()) {
            SyncItem target = ItemContainer.getInstance().getItem(clientSyncBaseItemView.getSyncBaseItem().getSyncWaepon().getTarget());
            int distance = target.getPosition().getDistance(center);
            x = (int) (center.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0));
            y = center.getY() - distance;
            width = weaponType.getMuzzleFlashWidth();
            height = distance;
        } else {
            x = (int) (center.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0));
            y = center.getY() - weaponType.getMuzzleFlashLength();
            width = weaponType.getMuzzleFlashWidth();
            height = weaponType.getMuzzleFlashLength();
        }
        if (x < 0 || y < 0 || width < 0 || height < 0) {
            return;
        }

        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle = rectangle.getSurroundedRectangle(center, angel);
        canvas = new GWTCanvas(rectangle.getWidth(), rectangle.getHeight());
        MapWindow.getAbsolutePanel().add(canvas,
                rectangle.getStart().getX() - TerrainView.getInstance().getViewOriginLeft(),
                rectangle.getStart().getY() - TerrainView.getInstance().getViewOriginTop());
        canvas.getElement().getStyle().setZIndex(Constants.Z_INDEX_MUZZLE_FLASH);
        normCenter = center.sub(rectangle.getStart());
        handlerImage(clientSyncBaseItemView.getSyncBaseItem().getBaseItemType());
    }

    private void handlerImage(BaseItemType baseItemType) {
        ImageLoader.loadImages(new String[]{ImageHandler.getMuzzleFlashImageUrl(baseItemType)}, new ImageLoader.CallBack() {
            @Override
            public void onImagesLoaded(ImageElement[] imageElements) {
                try {
                    if (imageElements.length != 1) {
                        throw new IllegalArgumentException("MuzzleFlash: Wrong image count received: " + imageElements.length);
                    }
                    WeaponType weaponType = clientSyncBaseItemView.getSyncBaseItem().getSyncWaepon().getWeaponType();
                    canvas.translate(normCenter.getX(), normCenter.getY());
                    canvas.rotate(-angel);
                    canvas.drawImage(imageElements[0], -Math.round(weaponType.getMuzzleFlashWidth() / 2.0), -weaponType.getMuzzleFlashLength());
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    private Index getAbsoluteStartPoint(ClientSyncBaseItemView clientSyncBaseItemView) throws ItemDoesNotExistException {
        SyncItem target = ItemContainer.getInstance().getItem(clientSyncBaseItemView.getSyncBaseItem().getSyncWaepon().getTarget());
        BaseItemType baseItemType = clientSyncBaseItemView.getSyncBaseItem().getBaseItemType();
        if (clientSyncBaseItemView.getSyncBaseItem().hasSyncTurnable()) {
            // Make angel start on the Y axsi
            double tmpAngel = clientSyncBaseItemView.getSyncBaseItem().getSyncTurnable().getAngel();
            tmpAngel += ImageHandler.QUARTER_RADIANT;
            tmpAngel = ImageHandler.normalizeAngel(tmpAngel);

            // Calculate ellipse coordinates
            Index ellipseMiddle = new Index(baseItemType.getWeaponType().getMuzzlePointX_0(), baseItemType.getWeaponType().getMuzzlePointY_90());
            int a = ellipseMiddle.getX() - baseItemType.getWeaponType().getMuzzlePointX_90();
            int b = ellipseMiddle.getY() - baseItemType.getWeaponType().getMuzzlePointY_0();

            // Calculate point
            int x = (int) (a * Math.cos(-tmpAngel));
            int y = (int) (b * Math.sin(-tmpAngel));

            // convert to MapWindow
            x = x + ellipseMiddle.getX() - baseItemType.getWidth() / 2 + clientSyncBaseItemView.getSyncItem().getPosition().getX();
            y = y + ellipseMiddle.getY() - baseItemType.getHeight() / 2 + clientSyncBaseItemView.getSyncItem().getPosition().getY();
            Index index = new Index(x, y);
            angel = index.getAngleToNord(target.getPosition());
            return index;
        } else {
            int x = baseItemType.getWeaponType().getMuzzlePointX_0() - baseItemType.getWidth() / 2 + clientSyncBaseItemView.getSyncItem().getPosition().getX();
            int y = baseItemType.getWeaponType().getMuzzlePointY_0() - baseItemType.getHeight() / 2 + clientSyncBaseItemView.getSyncItem().getPosition().getY();
            Index index = new Index(x, y);
            angel = index.getAngleToNord(target.getPosition());
            return index;
        }

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
