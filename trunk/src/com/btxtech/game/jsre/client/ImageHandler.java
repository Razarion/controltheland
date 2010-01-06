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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncTurnable;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import java.util.HashSet;

/**
 * User: beat
 * Date: Jun 14, 2009
 * Time: 4:35:18 PM
 */
public class ImageHandler {
    public static final int EXPLOSION_EDGE_LENGTH = 200;
    public static final int DEFAULT_IMAGE_NUMBER = 9;

    public static final String PNG_SUFFIX = ".png";
    public static final String IMAGES = "images";
    public static final String EXPLOSION = "effects";
    public static double ONE_RADIANT = 2.0 * Math.PI;
    public static double QUARTER_RADIANT = Math.PI / 2.0;

    private static HashSet<String> loadedUrls = new HashSet<String>();

    /**
     * Singleton
     */
    private ImageHandler() {

    }

    public static Image getItemTypeImage(SyncItem syncItem) {
        int imgIndex;
        if (syncItem.hasSyncTurnable()) {
            SyncTurnable syncTurnable = syncItem.getSyncTurnable();
            imgIndex = angleToNumber(syncTurnable.getAngel(), syncTurnable.getTurnableType().getImageCount());
            imgIndex++;// First image start with 1
        } else {
            imgIndex = 1;
        }
        ItemType itemType = syncItem.getItemType();
        return getItemTypeImage(imgIndex, itemType);
    }

    public static Image getItemTypeImage(ItemType itemType) {
        return getItemTypeImage(1, itemType);
    }

    private static Image getItemTypeImage(int imgIndex, ItemType itemType) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.ITEM_IMAGE_ID);
        url.append("=");
        url.append(itemType.getId());
        url.append("&");
        url.append(Constants.ITEM_IMAGE_INDEX);
        url.append("=");
        url.append(imgIndex);
        String urlStr = url.toString();
        loadImage(urlStr);
        return createImageIE6TransparencyProblem(urlStr, itemType.getWidth(), itemType.getHeight());
    }

    /*
    * Always starts with 0
    */
    public static int angleToNumber(double angle, int resolution) {
        angle = normalizeAngel(angle);

        double slice = getAngelSilce(resolution);
        double halveSlice = slice / 2.0;
        if (angle < halveSlice) {
            return 0; //Always starts with 1
        }
        if (angle > ONE_RADIANT - halveSlice) {
            return 0; //Always starts with 1
        }
        int slicePos = (int) Math.round(angle / slice);

        if (slicePos > resolution - 1) {
            throw new IllegalArgumentException();
        }

        return slicePos;
    }

    public static double getAngelSilce(int resolution) {
        return ONE_RADIANT / (double) resolution;
    }

    public static double normalizeAngel(double angle) {
        if (angle < 0) {
            angle = ONE_RADIANT + angle;
        }

        if (angle < 0 || angle > ONE_RADIANT) {
            throw new IllegalArgumentException("Angel not in range: " + angle);
        }
        return angle;
    }

    public static String getTerrainImageUrl(int terrainTileId) {
        return Constants.TERRAIN + terrainTileId;
    }

    public static String getMuzzleFlashImageUrl(BaseItemType baseItemType) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.MUZZLE_ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.ITEM_IMAGE_ID);
        url.append("=");
        url.append(baseItemType.getId());
        url.append("&");
        url.append(Constants.TYPE);
        url.append("=");
        url.append(Constants.TYPE_IMAGE);
        return url.toString();
    }

    public static Image getTerrainImage(int terrainTileId) {
        return createImageIE6TransparencyProblem(getTerrainImageUrl(terrainTileId), Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    public static String getExplosion() {
        return "/" + IMAGES + "/" + EXPLOSION + "/" + "ex4" + PNG_SUFFIX;
    }


    private static class ImageForIe6 extends Image {
        public ImageForIe6(Element element) {
            super(element);
        }
    }

    public static Image createImageIE6TransparencyProblem(String url, int width, int height) {
        if (GwtCommon.isIe6() && (url.endsWith(".png") || url.endsWith(".PNG"))) {
            return new ImageForIe6(getIE6TransparencyProblemElement(url, width, height));
        } else {
            return new Image(url);
        }
    }

    public static Element getIE6TransparencyProblemElement(String url, int width, int height) {
        Element div = DOM.createDiv();
        DOM.setInnerHTML(div, "<span style=\"display:inline-block;width:" + width + "px;height:" + height + "px;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url + "', sizingMethod='scale')\"></span>");
        return DOM.getFirstChild(div);
    }

    /**
     * If an image is loaded in IE7 & IE8, but remove before complete loading than this image can never be loaded again.
     * To solve this, put every image in a separate instance.
     *
     * @param url the image url
     */
    private static void loadImage(String url) {
        if (!loadedUrls.contains(url)) {
            loadedUrls.add(url);
            new Image(url);
        }
    }

}
