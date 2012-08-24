package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:27
 */
public class TipTaskFactory {
    public static List<AbstractTipTask> create(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        switch (gameTipConfig.getTip()) {
            case BUILD:
                return createBuiltFactory(gameTipManager, gameTipConfig.getActor(), gameTipConfig.getToBeBuiltId(), gameTipConfig.getTerrainPositionHint());
            case FABRICATE:
                return createFactorizeUnit(gameTipManager, gameTipConfig.getActor(), gameTipConfig.getToBeBuiltId());
            case GET_RESOURCE:
                return createGetResource(gameTipManager, gameTipConfig.getActor(), gameTipConfig.getResourceId());
            default:
                throw new IllegalArgumentException("TipTaskFactory: unknown tip: " + gameTipConfig.getTip());
        }
    }

    private static List<AbstractTipTask> createBuiltFactory(GameTipManager gameTipManager, int builderTypeId, int toBeBuiltId, Index terrainPositionHint) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, builderTypeId));
        tasks.add(new ToBeBuildPlacerTipTask(gameTipManager, toBeBuiltId));
        tasks.add(new SendBuildCommandTipTask(gameTipManager, toBeBuiltId, terrainPositionHint));
        return tasks;
    }

    private static List<AbstractTipTask> createFactorizeUnit(GameTipManager gameTipManager, int factoryTypeId, int toBeBuiltId) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, factoryTypeId));
        tasks.add(new SendFactorizeCommandTipTask(gameTipManager, toBeBuiltId));
        return tasks;
    }

    private static List<AbstractTipTask> createGetResource(GameTipManager gameTipManager, int actor, int resourceId) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, actor));
        tasks.add(new SendMoneyCollectCommandTipTask(gameTipManager, resourceId));
        return tasks;
    }
}
