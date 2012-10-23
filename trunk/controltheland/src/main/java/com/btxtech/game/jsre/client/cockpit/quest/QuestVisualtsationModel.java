package com.btxtech.game.jsre.client.cockpit.quest;

import java.util.logging.Logger;

import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.SplashManager;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class QuestVisualtsationModel {
    private static final QuestVisualtsationModel INSTANCE = new QuestVisualtsationModel();
    private static Logger log = Logger.getLogger(QuestVisualtsationModel.class.getName());
    private QuestInfo currentQuest;
    private QuestVisualisationCockpit listener;
    private boolean nextPlanet;

    public static QuestVisualtsationModel getInstance() {
        return INSTANCE;
    }

    public void setListener(QuestVisualisationCockpit listener) {
        this.listener = listener;
    }

    public void setLevelTask(LevelTaskPacket levelTaskPacket) {
        if (levelTaskPacket == null) {
            currentQuest = null;
        } else if (levelTaskPacket.isCompleted()) {
            SplashManager.getInstance().onLevelTaskCone();
            currentQuest = null;
        } else {
            if (levelTaskPacket.getQuestInfo() != null) {
                currentQuest = levelTaskPacket.getQuestInfo();
                if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE && currentQuest.getType() == QuestInfo.Type.MISSION) {
                    startMission();
                }
            }
        }
        updateType(levelTaskPacket);
    }

    private void updateType(LevelTaskPacket levelTaskPacket) {
        if (listener != null) {
            listener.updateType(currentQuest);
            if (levelTaskPacket != null && levelTaskPacket.getQuestProgressInfo() != null) {
                listener.updateQuestProgress(levelTaskPacket.getQuestProgressInfo());
            }
        }
    }

    public void startMission() {
        if (currentQuest == null || currentQuest.getType() != QuestInfo.Type.MISSION) {
            log.warning("ClientLevelHandler.startMission() currentQuest == null");
            return;
        }
        if (currentQuest.getType() != QuestInfo.Type.MISSION) {
            log.warning("ClientLevelHandler.startMission() currentQuest.getType() != QuestInfo.Type.MISSION");
            return;
        }
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            log.warning("Attempt to start a mission inside another mission.");
            return;
        }

        YesNoDialog yesNoDialog = new YesNoDialog("Start Mission", "Compete in a single player mission on a different planet. You can return to this base at any time.", "Start", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ClientLevelHandler.getInstance().setNextTaskId(currentQuest.getId());
                StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_SIMULATED);
            }
        }, "Cancel", null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
    }

    public boolean hasActiveQuest() {
        return currentQuest != null;
    }

    // Move to a user guidance class
    public void abortMission() {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.MASTER) {
            log.warning("Attempt to abort the real game");
            return;
        }
        StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_REAL);
    }

    // Move to a user guidance class
   public void moveToNextPlanet() {
        YesNoDialog yesNoDialog = new YesNoDialog("Next Planet", "Leave your base and move to the next planet?", "GO!", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Connection.getInstance().surrenderBase();
            }
        }, "Cancel", null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
    }

    public void onLevelChange(LevelScope levelScope) {
        if (levelScope.getPlanetId() != null && levelScope.getPlanetId() != ClientPlanetServices.getInstance().getPlanetInfo().getPlanetId()) {
            nextPlanet = true;
            moveToNextPlanet();
        } else {
            nextPlanet = false;
        }
        updateType(null);
    }

    public boolean isShowStartMission() {
        return currentQuest != null && currentQuest.getType() == QuestInfo.Type.MISSION && Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE;
    }

    public boolean isNextPlanet() {
        return nextPlanet;
    }

    public boolean isNoQuest() {
        return currentQuest == null;
    }

}
