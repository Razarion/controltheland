package com.btxtech.game.jsre.client.utg.tip.tiptask;

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
                return createBuiltFactory(gameTipManager, gameTipConfig);
            case FABRICATE:
                return createFactorizeUnit(gameTipManager, gameTipConfig);
            case GET_RESOURCE:
                return createGetResource(gameTipManager, gameTipConfig);
            case MOVE:
                return createMove(gameTipManager, gameTipConfig);
            case ATTACK:
                return createAttack(gameTipManager, gameTipConfig);
            default:
                throw new IllegalArgumentException("TipTaskFactory: unknown tip: " + gameTipConfig.getTip());
        }
    }

    private static List<AbstractTipTask> createBuiltFactory(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, gameTipConfig.getActor()));
        tasks.add(new ToBeBuildPlacerTipTask(gameTipManager, gameTipConfig.getToBeBuiltId()));
        tasks.add(new SendBuildCommandTipTask(gameTipManager, gameTipConfig.getToBeBuiltId(), gameTipConfig.getTerrainPositionHint()));
        addHighlightQuestVisualisationCockpit(gameTipManager, gameTipConfig, tasks);
        return tasks;
    }

    private static List<AbstractTipTask> createFactorizeUnit(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, gameTipConfig.getActor()));
        tasks.add(new SendFactorizeCommandTipTask(gameTipManager, gameTipConfig.getToBeBuiltId()));
        addHighlightQuestVisualisationCockpit(gameTipManager, gameTipConfig, tasks);
        return tasks;
    }

    private static List<AbstractTipTask> createGetResource(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, gameTipConfig.getActor()));
        tasks.add(new SendMoneyCollectCommandTipTask(gameTipManager, gameTipConfig.getResourceId()));
        addHighlightQuestVisualisationCockpit(gameTipManager, gameTipConfig, tasks);
        return tasks;
    }

    private static List<AbstractTipTask> createMove(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, gameTipConfig.getActor()));
        tasks.add(new SendMoveCommandTipTask(gameTipManager, gameTipConfig.getTerrainPositionHint()));
        addHighlightQuestVisualisationCockpit(gameTipManager, gameTipConfig, tasks);
        return tasks;
    }

    private static List<AbstractTipTask> createAttack(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        List<AbstractTipTask> tasks = new ArrayList<AbstractTipTask>();
        tasks.add(new SelectTipTask(gameTipManager, gameTipConfig.getActor()));
        tasks.add(new SendAttackCommandTipTask(gameTipManager, gameTipConfig.getActor()));
        addHighlightQuestVisualisationCockpit(gameTipManager, gameTipConfig, tasks);
        return tasks;
    }

    private static void addHighlightQuestVisualisationCockpit(GameTipManager gameTipManager, GameTipConfig gameTipConfig, List<AbstractTipTask> tasks) {
        if (gameTipConfig.isHighlightQuestVisualisationCockpit()) {
            tasks.add(new WatchQuestVisualisationCockpitTipTask(gameTipManager));
        }
    }
}
