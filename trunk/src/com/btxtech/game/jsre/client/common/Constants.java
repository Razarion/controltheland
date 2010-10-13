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
    public static final String ITEM_IMAGE_ID = "id";
    public static final String ITEM_IMAGE_INDEX = "ix";

    // Tutorial
    public static final String TUTORIAL_RESOURCE_URL = "/spring/tutorial";
    public static final String TUTORIAL_RESOURCE_ID = "id";

    // zIndex
    public static final int Z_INDEX_STARTUP_SCREEN = 100; // Fix coded in Game.html
    public static final int Z_INDEX_DIALOG = 16;
    public static final int Z_INDEX_GROUP_SELECTION_FRAME = 15;
    public static final int Z_INDEX_SPEECH_BUBBLE = 14;
    public static final int Z_INDEX_PLACEABLE_PREVIEW = 13;
    public static final int Z_INDEX_TOP_MAP_PANEL = 12;
    public static final int Z_INDEX_PROJECTILE = 11;
    public static final int Z_INDEX_EXPLOSION = 10;
    public static final int Z_INDEX_MUZZLE_FLASH = 9;
    public static final int Z_INDEX_MACHINE_GUN_ATTACK = 8;
    public static final int Z_INDEX_ABOVE_MOVABLE = 7;
    public static final int Z_INDEX_MOVABLE = 6;
    public static final int Z_INDEX_MONEY = 5;
    public static final int Z_INDEX_BUILDING = 4;
    public static final int Z_INDEX_BELOW_BUILDING = 3;
    public static final int Z_INDEX_TERRAIN = 2;
    public static final int Z_INDEX_HIDDEN = 1;

    // Financial
    public static final int MONEY_STACK_COUNT = 8;

    // Distances
    public static final int MIN_FREE_MONEY_DISTANCE = 400;

    // Item names
    public static final String CONSTRUCTION_VEHICLE = "Construction Vehicle";
    public static final String FACTORY = "Factory";
    public static final String JEEP = "Jeep";
    public static final String HARVESTER = "Harvester";
    public static final String MONEY = "Money";

    // Missions
    public static final int TARGET_MAX_RANGE = 300;
    public static final int TARGET_MIN_RANGE = 150;
    public static final double MISSION_MONEY = 50.0;

    // Error
    public static final String ERROR_KEY = "e";

}
