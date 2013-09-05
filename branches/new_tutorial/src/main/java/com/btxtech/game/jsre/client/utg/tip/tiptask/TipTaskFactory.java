package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.utg.tip.GameTipConfig;
import com.btxtech.game.jsre.client.utg.tip.GameTipManager;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:27
 */
public class TipTaskFactory {
    public static TipTaskContainer create(GameTipManager gameTipManager, GameTipConfig gameTipConfig) {
        TipTaskContainer tipTaskContainer = new TipTaskContainer(gameTipManager);
        switch (gameTipConfig.getTip()) {
            case BUILD: {
                createBuiltFactory(tipTaskContainer, gameTipConfig);
                break;
            }
            case FABRICATE: {
                createFactorizeUnit(tipTaskContainer, gameTipConfig);
                break;
            }
            case GET_RESOURCE: {
                createGetResource(tipTaskContainer, gameTipConfig);
                break;
            }
            case MOVE: {
                createMove(tipTaskContainer, gameTipConfig);
                break;
            }
            case ATTACK: {
                createAttack(tipTaskContainer, gameTipConfig);
                break;
            }
            case SCROLL: {
                createScroll(tipTaskContainer, gameTipConfig);
                break;
            }
            default:
                throw new IllegalArgumentException("TipTaskFactory: unknown tip: " + gameTipConfig.getTip());
        }
        return tipTaskContainer;
    }

    private static void createBuiltFactory(TipTaskContainer tipTaskContainer, GameTipConfig gameTipConfig) {
        tipTaskContainer.add(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.add(new ToBeBuildPlacerTipTask(gameTipConfig.getToBeBuiltId()));
        tipTaskContainer.add(new SendBuildCommandTipTask(gameTipConfig.getToBeBuiltId(), gameTipConfig.getTerrainPositionHint()));
        tipTaskContainer.addFallback(new IdleItemTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new ToBeBuildPlacerTipTask(gameTipConfig.getToBeBuiltId()));
        tipTaskContainer.addFallback(new SendBuildCommandTipTask(gameTipConfig.getToBeBuiltId(), gameTipConfig.getTerrainPositionHint()));
    }

    private static void createFactorizeUnit(TipTaskContainer tipTaskContainer, GameTipConfig gameTipConfig) {
        tipTaskContainer.add(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.add(new SendFactorizeCommandTipTask(gameTipConfig.getToBeBuiltId()));
        tipTaskContainer.addFallback(new IdleItemTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SendFactorizeCommandTipTask(gameTipConfig.getToBeBuiltId()));
    }

    private static void createGetResource(TipTaskContainer tipTaskContainer, GameTipConfig gameTipConfig) {
        tipTaskContainer.add(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.add(new SendMoneyCollectCommandTipTask(gameTipConfig.getResourceId()));
        tipTaskContainer.addFallback(new IdleItemTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SendMoneyCollectCommandTipTask(gameTipConfig.getResourceId()));
    }

    private static void createMove(TipTaskContainer tipTaskContainer, GameTipConfig gameTipConfig) {
        tipTaskContainer.add(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.add(new SendMoveCommandTipTask(gameTipConfig.getTerrainPositionHint()));
        tipTaskContainer.addFallback(new IdleItemTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SendMoveCommandTipTask(gameTipConfig.getTerrainPositionHint()));
    }

    private static void createAttack(TipTaskContainer tipTaskContainer, GameTipConfig gameTipConfig) {
        tipTaskContainer.add(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.add(new SendAttackCommandTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new IdleItemTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SelectTipTask(gameTipConfig.getActor()));
        tipTaskContainer.addFallback(new SendAttackCommandTipTask(gameTipConfig.getActor()));
    }

    private static void createScroll(TipTaskContainer tipTaskContainer, GameTipConfig gameTipConfig) {
        tipTaskContainer.add(new ScrollTipTask(gameTipConfig.getTerrainRectHint(), gameTipConfig.getTipSplashPopupInfo()));
    }

}
