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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: Jun 14, 2009
 * Time: 4:35:18 PM
 */
public class ImageHandler {
    public static final int EXPLOSION_EDGE_LENGTH = 200;
    public static final int DEFAULT_IMAGE_NUMBER = 9;
    private static final int QUEST_PROGRESS_IMAGES_HEIGHT = 20;

    public static final String PNG_SUFFIX = ".png";
    public static final String IMAGES = "images";
    public static final String EXPLOSION = "effects";
    public static final String ICONS = "icons";
    public static final String TIPS = "tips";
    public static final String COCKPIT = "/" + IMAGES + "/cockpit/";
    public static final String BTN_IMAGE_PATH = COCKPIT;
    public static final String BTN_UP_IMAGE = "-up.png";
    public static final String BTN_DOWN_IMAGE = "-down.png";
    public static final String BTN_DISABLED_IMAGE = "-disabled.png";
    public static final String SPLASH_IMAGE_PREFIX = "/images/splash/";
    public static final String EXTERNAL_USED = "external_used";

    /**
     * Singleton
     */
    private ImageHandler() {

    }

    public static String getImageBackgroundUrl(String bgImageUrl) {
        StringBuilder builder = new StringBuilder();
        builder.append("url(");
        builder.append(bgImageUrl);
        builder.append(") no-repeat 0px 0px");
        return builder.toString();
    }

    public static Image getItemTypeImage(ItemType itemType, int width, int height) {
        String url = getItemTypeSpriteMapUrl(itemType.getId());
        ItemTypeSpriteMap itemTypeSpriteMap = itemType.getItemTypeSpriteMap();
        double scale = (double) width / (double) itemTypeSpriteMap.getImageWidth();
        Index offset = itemTypeSpriteMap.getCosmeticImageOffset();
        Image image = new Image(url, (int) ((double) offset.getX() * scale), (int) ((double) offset.getY() * scale), width, height);
        image.setPixelSize(width, height);
        image.getElement().getStyle().setProperty("backgroundSize",
                (int) ((double) itemTypeSpriteMap.getSpriteWidth() * scale) + "px " + Integer.toString(height) + "px");
        return image;
    }

    public static String getQuestProgressItemTypeImageString(ItemType itemType) {
        ItemTypeSpriteMap itemTypeSpriteMap = itemType.getItemTypeSpriteMap();
        double scale = (double) QUEST_PROGRESS_IMAGES_HEIGHT / (double) itemTypeSpriteMap.getImageHeight();
        Index offset = itemTypeSpriteMap.getCosmeticImageOffset();
        StringBuilder builder = new StringBuilder();
        builder.append("<img border='0' src='/game/clear.cache.gif' style='width:");
        builder.append((int) (itemTypeSpriteMap.getImageWidth() * scale));
        builder.append("px; height:");
        builder.append(QUEST_PROGRESS_IMAGES_HEIGHT);
        builder.append("px; background-image: url(");
        builder.append(getItemTypeSpriteMapUrl(itemType.getId()));
        builder.append("); background-repeat: no-repeat; background-position: -");
        builder.append((int) (offset.getX() * scale));
        builder.append("px ");
        builder.append(offset.getY());
        builder.append("px; background-size: ");
        builder.append((int) (itemTypeSpriteMap.getSpriteWidth() * scale));
        builder.append("px ");
        builder.append(QUEST_PROGRESS_IMAGES_HEIGHT);
        builder.append("px;");
        builder.append("'></img>");
        return builder.toString();
    }

    public static String getItemTypeSpriteMapUrl(int itemId) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.ITEM_TYPE_SPRITE_MAP_ID);
        url.append("=");
        url.append(itemId);
        return url.toString();
    }


    public static String getImageSpriteMapUrl(int imageSpriteMapInfoId) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.IMAGE_SPRITE_MAP_PATH);
        url.append("?");
        url.append(Constants.IMAGE_SPRITE_MAP_ID);
        url.append("=");
        url.append(imageSpriteMapInfoId);
        return url.toString();
    }

    public static String getSurfaceImagesUrl(int id) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.TERRAIN_CONTROLLER_URL);
        url.append("?");
        url.append(Constants.TERRAIN_IMG_TYPE);
        url.append("=");
        url.append(Constants.TERRAIN_IMG_TYPE_SURFACE);
        url.append("&");
        url.append(Constants.TERRAIN_IMG_TYPE_IMG_ID);
        url.append("=");
        url.append(Integer.toString(id));
        return url.toString();
    }

    public static String getTerrainImageUrl(int id) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.TERRAIN_CONTROLLER_URL);
        url.append("?");
        url.append(Constants.TERRAIN_IMG_TYPE);
        url.append("=");
        url.append(Constants.TERRAIN_IMG_TYPE_FOREGROUND);
        url.append("&");
        url.append(Constants.TERRAIN_IMG_TYPE_IMG_ID);
        url.append("=");
        url.append(Integer.toString(id));
        return url.toString();
    }

    public static String getInventoryItemUrl(int id) {
        StringBuilder url = new StringBuilder();
        url.append("/");
        url.append(CmsUtil.MOUNT_INVENTORY_IMAGES);
        url.append("/");
        url.append(Constants.INVENTORY_TYPE);
        url.append("/");
        url.append(Constants.INVENTORY_TYPE_ITEM);
        url.append("/");
        url.append(Constants.INVENTORY_ID);
        url.append("/");
        url.append(Integer.toString(id));
        return url.toString();
    }

    public static String getInventoryArtifactUrl(int id) {
        StringBuilder url = new StringBuilder();
        url.append("/");
        url.append(CmsUtil.MOUNT_INVENTORY_IMAGES);
        url.append("/");
        url.append(Constants.INVENTORY_TYPE);
        url.append("/");
        url.append(Constants.INVENTORY_TYPE_ARTIFACT);
        url.append("/");
        url.append(Constants.INVENTORY_ID);
        url.append("/");
        url.append(Integer.toString(id));
        return url.toString();
    }

    public static String getStarMapPlanetImageUrl(int planetId) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.IMAGE_URL);
        url.append("?");
        url.append(Constants.IMAGE_TYPE_KEY);
        url.append("=");
        url.append(Constants.IMAGE_TYPE_VALUE_STAR_MAP);
        url.append("&");
        url.append(Constants.IMAGE_ID);
        url.append("=");
        url.append(Integer.toString(planetId));
        return url.toString();
    }

    public static Image getStarMapPlanetImage(int planetId) {
        return new Image(getStarMapPlanetImageUrl(planetId));
    }

    public static Image getButtonUpImage(String name) {
        return new Image(BTN_IMAGE_PATH + name + BTN_UP_IMAGE);
    }

    public static Image getButtonDownImage(String name) {
        return new Image(BTN_IMAGE_PATH + name + BTN_DOWN_IMAGE);
    }

    public static Image getButtonDisabledImage(String name) {
        return new Image(BTN_IMAGE_PATH + name + BTN_DISABLED_IMAGE);
    }

    public static String getSplashImageUrl(String name) {
        return SPLASH_IMAGE_PREFIX + name;
    }

    public static Image getTerrainImage(int id) {
        return new Image(getTerrainImageUrl(id));
    }

    public static Image getSurfaceImage(int id) {
        return new Image(getSurfaceImagesUrl(id));
    }

    public static String getExplosion() {
        return "/" + IMAGES + "/" + EXPLOSION + "/" + "ex4" + PNG_SUFFIX;
    }

    public static Widget getTipImage(String tip) {
        return new Image("/" + IMAGES + "/" + TIPS + "/" + tip);
    }

    public static String getCockpitImageUrl(String image) {
        return COCKPIT + image;
    }

    public static Image getCockpitImage(String image) {
        return new Image(getCockpitImageUrl(image));
    }

    public static String getFacebookFeedImageUrl() {
        return CmsUtil.RAZARION_URL + "/" + IMAGES + "/" + EXTERNAL_USED + "/RazarionMain4.jpg";
    }
}
