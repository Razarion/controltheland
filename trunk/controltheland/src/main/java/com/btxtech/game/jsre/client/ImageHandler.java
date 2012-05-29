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
import com.btxtech.game.jsre.client.utg.ImageSizeCallback;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BuildupStep;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

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
    public static final String ICONS = "icons";
    public static final String TIPS = "tips";
    public static final String COCKPIT = "/" + IMAGES + "/cockpit/";
    public static final String BTN_IMAGE_PATH = COCKPIT;
    public static final String BTN_UP_IMAGE = "-up.png";
    public static final String BTN_DOWN_IMAGE = "-down.png";
    public static final String SPLASH_IMAGE_PREFIX = "/images/splash/";

    private static HashSet<String> loadedUrls = new HashSet<String>();

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

    public static String getItemTypeImageBackgroundUrl(SyncItem syncItem) {
        int xOffset = syncItem.getSyncItemArea().getBoundingBox().angelToImageOffset(syncItem.getSyncItemArea().getAngel());
        StringBuilder builder = new StringBuilder();
        builder.append("url(");
        builder.append(getItemTypeSpriteMapUrl(syncItem.getItemType().getId()));
        builder.append(") no-repeat -");
        builder.append(xOffset);
        builder.append("px 0px");
        return builder.toString();
    }

    public static Image getItemTypeImage(ItemType itemType, Integer width, Integer height) {
        String url = getItemTypeSpriteMapUrl(itemType.getId());
        Image image;
        if (width != null && height != null) {
            int xOffset = itemType.getBoundingBox().getCosmeticImageIndex() * width;
            image = new Image(url, xOffset, 0, width, height);
            image.setPixelSize(width, height);
            String spriteWidth = Integer.toString(width * itemType.getBoundingBox().getAngelCount());
            image.getElement().getStyle().setProperty("backgroundSize", spriteWidth + "px " + Integer.toString(height) + "px");
        } else {
            int xOffset = itemType.getBoundingBox().getCosmeticImageIndex() * itemType.getBoundingBox().getImageWidth();
            image = new Image(url, xOffset, 0, itemType.getBoundingBox().getImageWidth(), itemType.getBoundingBox().getImageHeight());
        }
        return image;
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

    public static String getBuildupStepImageUrl(BaseItemType baseItemType, BuildupStep buildupStep) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.TYPE);
        url.append("=");
        url.append(Constants.TYPE_BUILDUP_STEP);
        url.append("&");
        url.append(Constants.ITEM_TYPE_ID);
        url.append("=");
        url.append(baseItemType.getId());
        url.append("&");
        url.append(Constants.ITEM_IMAGE_BUILDUP_STEP);
        url.append("=");
        url.append(buildupStep.getImageId());
        return url.toString();
    }

    public static String getMuzzleFlashImageUrl(BaseItemType baseItemType) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.MUZZLE_ITEM_IMAGE_URL);
        url.append("?");
        url.append(Constants.ITEM_TYPE_ID);
        url.append("=");
        url.append(baseItemType.getId());
        url.append("&");
        url.append(Constants.TYPE);
        url.append("=");
        url.append(Constants.TYPE_IMAGE);
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
        url.append(Constants.INVENTORY_PATH);
        url.append("?");
        url.append(Constants.INVENTORY_TYPE);
        url.append("=");
        url.append(Constants.INVENTORY_TYPE_ITEM);
        url.append("&");
        url.append(Constants.INVENTORY_ID);
        url.append("=");
        url.append(Integer.toString(id));
        return url.toString();
    }

    public static String getInventoryArtifactUrl(int id) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.INVENTORY_PATH);
        url.append("?");
        url.append(Constants.INVENTORY_TYPE);
        url.append("=");
        url.append(Constants.INVENTORY_TYPE_ARTIFACT);
        url.append("&");
        url.append(Constants.INVENTORY_ID);
        url.append("=");
        url.append(Integer.toString(id));
        return url.toString();
    }

    public static Image getButtonUpImage(String name) {
        return new Image(BTN_IMAGE_PATH + name + BTN_UP_IMAGE);
    }

    public static Image getButtonDownImage(String name) {
        return new Image(BTN_IMAGE_PATH + name + BTN_DOWN_IMAGE);
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

    public static Image getIcon16(String icon) {
        return createImageIE6TransparencyProblem("/" + IMAGES + "/" + ICONS + "/" + icon + PNG_SUFFIX, 16, 16);
    }

    public static Widget getTipImage(String tip) {
        return new Image("/" + IMAGES + "/" + TIPS + "/" + tip);
    }

    /**
     * @param id                image id
     * @param imageSizeCallback called when the image was loaded. If the image is already loaded is is not called
     * @return Image
     */
    public static Image getTutorialImage(int id, final ImageSizeCallback imageSizeCallback) {
        StringBuilder url = new StringBuilder();
        url.append(Constants.TUTORIAL_RESOURCE_URL);
        url.append("?");
        url.append(Constants.TUTORIAL_RESOURCE_ID);
        url.append("=");
        url.append(id);
        String urlStr = url.toString();
        final Image image = new Image();
        if (imageSizeCallback != null) {
            image.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {
                    imageSizeCallback.onImageSize(image, image.getWidth(), image.getHeight());
                }
            });
        }
        image.setUrl(urlStr);
        loadImage(urlStr);
        return image;
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

    public static String getCockpitImageUrl(String image) {
        return COCKPIT + image;
    }

    public static Image getCockpitImage(String image) {
        return new Image(getCockpitImageUrl(image));
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
