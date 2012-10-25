package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.SplashManager;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class QuestVisualtsationModel {
    private static final String COLOR_ATTACK = "#FF0000";
    private static final String COLOR_COLLECT = "#FFFF00";
    private static final String COLOR_PLACE = "#00FF00";
    private static final QuestVisualtsationModel INSTANCE = new QuestVisualtsationModel();
    private static Logger log = Logger.getLogger(QuestVisualtsationModel.class.getName());
    private QuestInfo currentQuest;
    private QuestProgressInfo currentQuestProgressInfo;
    private QuestVisualisationCockpit listener;
    private boolean nextPlanet;
    private boolean showInGameVisualisation;

    public static QuestVisualtsationModel getInstance() {
        return INSTANCE;
    }

    public void setListener(QuestVisualisationCockpit listener) {
        this.listener = listener;
    }

    public void setLevelTask(LevelTaskPacket levelTaskPacket) {
        if (levelTaskPacket == null) {
            currentQuest = null;
            currentQuestProgressInfo = null;
            updateType();
        } else if (levelTaskPacket.isCompleted()) {
            SplashManager.getInstance().onLevelTaskCone();
            currentQuest = null;
            currentQuestProgressInfo = null;
            updateType();
        } else {
            currentQuestProgressInfo = levelTaskPacket.getQuestProgressInfo();
            if (levelTaskPacket.getQuestInfo() != null) {
                currentQuest = levelTaskPacket.getQuestInfo();
                updateType();
                if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE && currentQuest.getType() == QuestInfo.Type.MISSION) {
                    startMission();
                }
            }
            updateQuestProgress();
        }
    }

    private void updateType() {
        if (listener != null) {
            listener.updateType(currentQuest);
        }
    }

    private void updateQuestProgress() {
        if (listener != null) {
            if (currentQuestProgressInfo != null) {
                listener.updateQuestProgress(currentQuestProgressInfo);
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
        currentQuest = null;
        currentQuestProgressInfo = null;
        updateType();
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

    public void setShowInGameVisualisation(boolean showInGameVisualisation) {
        this.showInGameVisualisation = showInGameVisualisation;
    }

    public Collection<InGameQuestItemVisualisation> getInGameItemQuestVisualisation(Collection<SyncItem> itemsInView, Rectangle viewRect) {
        if (!showInGameVisualisation) {
            return null;
        }

        if (currentQuestProgressInfo == null) {
            return null;
        }
        String color;
        switch (currentQuestProgressInfo.getConditionTrigger()) {
            case SYNC_ITEM_KILLED:
                if (itemsInView.isEmpty()) {
                    return null;
                }
                color = COLOR_ATTACK;
                break;
            case MONEY_INCREASED:
                if (itemsInView.isEmpty()) {
                    return null;
                }
                color = COLOR_COLLECT;
                break;
            case SYNC_ITEM_POSITION:
                if (currentQuest.getRadarPosition() != null && viewRect.contains(currentQuest.getRadarPosition())) {
                    Collection<InGameQuestItemVisualisation> itemVisualisations = new ArrayList<InGameQuestItemVisualisation>();
                    itemVisualisations.add(new InGameQuestItemVisualisation(COLOR_PLACE, currentQuest.getRadarPosition().sub(viewRect.getStart())));
                    return itemVisualisations;
                } else {
                    return null;
                }
            default:
                return null;
        }

        Collection<InGameQuestItemVisualisation> itemVisualisations = new ArrayList<InGameQuestItemVisualisation>();
        for (SyncItem syncItem : itemsInView) {
            if (color.equals(COLOR_ATTACK)) {
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!syncBaseItem.isEnemy(ClientBase.getInstance().getSimpleBase())) {
                        continue;
                    }
                    if (currentQuestProgressInfo.getAmount() != null) {
                        if (!currentQuestProgressInfo.getAmount().isFulfilled()) {
                            itemVisualisations.add(new InGameQuestItemVisualisation(color, syncItem.getSyncItemArea().getPosition().sub(viewRect.getStart())));
                        }
                    } else if (currentQuestProgressInfo.getItemIdAmounts() != null) {
                        QuestProgressInfo.Amount amount = currentQuestProgressInfo.getItemIdAmounts().get(syncBaseItem.getItemType().getId());
                        if (amount != null && !amount.isFulfilled()) {
                            itemVisualisations.add(new InGameQuestItemVisualisation(color, syncItem.getSyncItemArea().getPosition().sub(viewRect.getStart())));
                        }
                    }
                }
            } else if (color.equals(COLOR_COLLECT)) {
                if (syncItem instanceof SyncResourceItem) {
                    if (currentQuestProgressInfo.getAmount() != null) {
                        if (!currentQuestProgressInfo.getAmount().isFulfilled()) {
                            itemVisualisations.add(new InGameQuestItemVisualisation(color, syncItem.getSyncItemArea().getPosition().sub(viewRect.getStart())));
                        }
                    }
                }
            }
        }

        return itemVisualisations;
    }

    public InGameQuestDirectionVisualisation getInGameQuestDirectionVisualisationAngel(Rectangle viewRect) {
        if (!showInGameVisualisation) {
            return null;
        }

        if (currentQuestProgressInfo == null) {
            return null;
        }
        switch (currentQuestProgressInfo.getConditionTrigger()) {
            case SYNC_ITEM_KILLED: {
                Set<Integer> filter = null;
                if (currentQuestProgressInfo.getItemIdAmounts() != null) {
                    filter = new HashSet<Integer>();
                    for (Map.Entry<Integer, QuestProgressInfo.Amount> entry : currentQuestProgressInfo.getItemIdAmounts().entrySet()) {
                        if (!entry.getValue().isFulfilled()) {
                            filter.add(entry.getKey());
                        }
                    }
                }
                SyncBaseItem syncBaseItem = ItemContainer.getInstance().getNearestEnemyItem(viewRect.getCenter(), filter, ClientBase.getInstance().getSimpleBase());
                if (syncBaseItem == null) {
                    return null;
                }
                return new InGameQuestDirectionVisualisation(syncBaseItem, viewRect);
            }
            case MONEY_INCREASED: {
                SyncResourceItem resourceItem = ItemContainer.getInstance().getNearestResourceItem(viewRect.getCenter());
                if (resourceItem == null) {
                    return null;
                }
                return new InGameQuestDirectionVisualisation(resourceItem, viewRect);
            }
            case SYNC_ITEM_POSITION:
                if (currentQuest.getRadarPosition() != null) {
                    return new InGameQuestDirectionVisualisation(currentQuest.getRadarPosition(), viewRect);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }
}
