package com.btxtech.game.mockservlet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.tutorial.ItemTypeAndPosition;
import com.btxtech.game.jsre.common.tutorial.TaskConfig;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 15.03.2012
 * Time: 23:55:55
 */
public class MockMovableServiceTestTransporter extends MockMovableService {
    protected void setupTutorialConfig(SimulationInfo simulationInfo) {
        List<ItemTypeAndPosition> ownItems = new ArrayList<ItemTypeAndPosition>();
        ownItems.add(new ItemTypeAndPosition(MOVABLE_CONTAINER_ITEM_TYPE.getId(), new Index(1450, 700), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(200, 100), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(200, 300), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(200, 500), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(200, 700), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(200, 900), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(400, 100), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(400, 300), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(400, 500), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(400, 700), MathHelper.WEST));
        ownItems.add(new ItemTypeAndPosition(MOVABLE_ITEM_TYPE.getId(), new Index(400, 900), MathHelper.WEST));
        Map<Integer, Integer> itemTypeLimitation = new HashMap<Integer, Integer>();
        List<TaskConfig> taskConfigs = new ArrayList<TaskConfig>();
        taskConfigs.add(new TaskConfig(ownItems, null, null, 10, 100, 1000, 0.5, "TestTask1", null, itemTypeLimitation, RadarMode.MAP));
        TutorialConfig tutorialConfig = new TutorialConfig(taskConfigs, "MyTestBase", 500, 500, false, "", false);
        simulationInfo.setTutorialConfig(tutorialConfig);
        simulationInfo.setLevelNumber(1);
    }

    protected void setupTerrain(GameInfo gameInfo) {
        gameInfo.setTerrainSettings(new TerrainSettings(50, 50, 100, 100));
        gameInfo.setTerrainImagePositions(new ArrayList<TerrainImagePosition>());
        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 14, 50), 0)); // Land
        surfaceRects.add(new SurfaceRect(new Rectangle(14, 0, 1, 50), 1)); // Land Coast
        surfaceRects.add(new SurfaceRect(new Rectangle(15, 0, 1, 50), 2)); // Water Coast
        surfaceRects.add(new SurfaceRect(new Rectangle(16, 0, 34, 50), 3)); // Water
        gameInfo.setSurfaceRects(surfaceRects);
        Collection<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
        surfaceImages.add(new SurfaceImage(SurfaceType.LAND, 0, ""));
        surfaceImages.add(new SurfaceImage(SurfaceType.LAND_COAST, 1, ""));
        surfaceImages.add(new SurfaceImage(SurfaceType.WATER_COAST, 2, ""));
        surfaceImages.add(new SurfaceImage(SurfaceType.WATER, 3, ""));
        gameInfo.setSurfaceImages(surfaceImages);
        gameInfo.setTerrainImages(new ArrayList<TerrainImage>());
    }
}
