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

package com.btxtech.game.jsre.client.common;

/**
 * User: beat
 * Date: May 23, 2009
 * Time: 3:07:05 PM
 */
public class Constants {
    // Terrain
    public static final int SCROLL_DISTANCE = 500;
    public static final String TERRAIN_CONTROLLER_URL = "/spring/terrain";
    public static final String TERRAIN_IMG_TYPE = "tp";
    public static final String TERRAIN_IMG_TYPE_SURFACE = "sf";
    public static final String TERRAIN_IMG_TYPE_FOREGROUND = "fg";
    public static final String TERRAIN_IMG_TYPE_IMG_ID = "id";

    // Item Images
    public static final String ITEM_IMAGE_URL = "/spring/item";
    public static final String MUZZLE_ITEM_IMAGE_URL = "/spring/muzzle";
    public static final String TYPE = "type";
    public static final String TYPE_IMAGE = "img";
    public static final String TYPE_SOUND = "snd";
    public static final String ITEM_TYPE_ID = "id";
    public static final String ITEM_TYPE_SPRITE_MAP_ID = "idsm";
    public static final String CODEC = "cdc";

    // Codecs
    public static final String CODEC_TYPE_MP3 = "audio/mpeg";
    public static final String CODEC_TYPE_OGG = "audio/ogg";

    // Tutorial
    public static final String TUTORIAL_RESOURCE_URL = "/spring/tutorial";
    public static final String TUTORIAL_RESOURCE_ID = "id";

    // zIndex
    public static final int Z_INDEX_LEVEL_SPLASH = 101; // TODO Remove splash
    public static final int Z_INDEX_STARTUP_SCREEN = 100; // Fix coded in Game.html
    public static final int Z_INDEX_TIP = 11;
    public static final int Z_INDEX_DIALOG = 10;
    public static final int Z_INDEX_INFORMATION_COCKPIT = 9;
    public static final int Z_INDEX_GROUP_SELECTION_FRAME = 8;
    public static final int Z_INDEX_SPEECH_BUBBLE = 7;
    public static final int Z_INDEX_PLACEABLE_PREVIEW = 6;
    public static final int Z_INDEX_ITEM_COCKPIT = 5;
    public static final int Z_INDEX_CHAT_COCKPIT = 4;
    public static final int Z_INDEX_SIDE_COCKPIT = 3;
    public static final int Z_INDEX_TERRAIN = 2;
    public static final int Z_INDEX_HIDDEN = 1;

    // Error
    public static final String ERROR_KEY = "e";

    // Inventory Items
    public static final String INVENTORY_PATH = "/inventoryImg";
    public static final String INVENTORY_ID = "id";
    public static final String INVENTORY_TYPE = "type";
    public static final String INVENTORY_TYPE_ARTIFACT = "art";
    public static final String INVENTORY_TYPE_ITEM = "item";
}
