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
    private int distance;
    private double angel;
    private long time;

    public MuzzleFlash(ClientSyncBaseItemView clientSyncBaseItemView) throws ItemDoesNotExistException {
        time = System.currentTimeMillis();
        this.clientSyncBaseItemView = clientSyncBaseItemView;
        SoundHandler.playMuzzleFlashSound(clientSyncBaseItemView.getSyncBaseItem().getBaseItemType());
        Index center = getAbsoluteStartPoint(clientSyncBaseItemView);
        WeaponType weaponType = clientSyncBaseItemView.getSyncBaseItem().getSyncWaepon().getWeaponType();
        Rectangle rectangle;
        if (weaponType.stretchMuzzleFlashToTarget()) {
            SyncItem target = ItemContainer.getInstance().getItem(clientSyncBaseItemView.getSyncBaseItem().getSyncWaepon().getTarget());
            distance = target.getPosition().getDistance(center);
            rectangle = new Rectangle((int) (center.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0)),
                    center.getY() - distance,
                    weaponType.getMuzzleFlashWidth(),
                    distance);
        } else {
            rectangle = new Rectangle((int) (center.getX() - Math.round(weaponType.getMuzzleFlashWidth() / 2.0)),
                    center.getY() - weaponType.getMuzzleFlashLength(),
                    weaponType.getMuzzleFlashWidth(),
                    weaponType.getMuzzleFlashLength());
        }
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
            //displayOval(x,y,a,b, tmpAngel);
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
        MapWindow.getAbsolutePanel().remove(canvas);
    }

   /*    private void displayOval(int middleX, int intMiddleY, int a, int b, double angel) {
        final int CROSS_HALF = 3;
        GWTCanvas oval = new GWTCanvas(2 * a, 2 * b);
        int rSmall = Math.min(a, b);
        int rBig = Math.max(a, b);
        MapWindow.getAbsolutePanel().add(oval, middleX - a - TerrainView.getInstance().getViewOriginLeft(), intMiddleY - b- TerrainView.getInstance().getViewOriginTop());
        //oval.getElement().getStyle().setZIndex(Constants.Z_INDEX_MUZZLE_FLASH + 1);
        //oval.setLineWidth(1);
        //oval.setStrokeStyle(Color.RED);
        oval.beginPath();
        // Cross
        //makeCorss(a, b, CROSS_HALF, oval);
        // Border
        //oval.moveTo(0, 0);
        //oval.lineTo(2 * a - 1, 0);
        //oval.lineTo(2 * a - 1, 2 * b - 1);
        //oval.lineTo(0, 2 * b - 1);
        //oval.lineTo(0, 0);
        // Oval
        oval.scale((double) a / (double) rBig, (double) b / (double) rBig);
        oval.arc((double) a * rBig / (double) a, (double) b * rBig / (double) b, rBig, 0, Math.PI * 2, true);
        int x = a + (int) (a * Math.cos(-angel));
        int y = b + (int) (b * Math.sin(-angel));
        System.out.println("angel: " + angel + " x:" + x + " y:" + y + " Math.cos(angel): " + Math.cos(angel) + " Math.sin(angel): " + Math.sin(angel));
        makeCorss(x, y, CROSS_HALF, oval);


        oval.stroke();
    }

    private void makeCorss(int a, int b, int CROSS_HALF, GWTCanvas oval) {
        oval.moveTo(a - CROSS_HALF, b);
        oval.lineTo(a + CROSS_HALF, b);
        oval.moveTo(a, b - CROSS_HALF);
        oval.lineTo(a, b + CROSS_HALF);
    }*/
    
    public boolean isTimeUp() {
        return System.currentTimeMillis() >= time + MILIS_SHOW_TIME;
    }
}
