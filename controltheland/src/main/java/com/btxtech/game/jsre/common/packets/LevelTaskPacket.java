package com.btxtech.game.jsre.common.packets;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 16:23
 */
public class LevelTaskPacket extends Packet{
    private QuestInfo questInfo;
    private String activeQuestProgress;
    private boolean completed;

    public QuestInfo getQuestInfo() {
        return questInfo;
    }

    public void setQuestInfo(QuestInfo questInfo) {
        this.questInfo = questInfo;
    }

    public String getActiveQuestProgress() {
        return activeQuestProgress;
    }

    public void setActiveQuestProgress(String activeQuestProgress) {
        this.activeQuestProgress = activeQuestProgress;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        completed = true;
    }
}
