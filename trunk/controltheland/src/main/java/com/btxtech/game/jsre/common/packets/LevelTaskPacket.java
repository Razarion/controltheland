package com.btxtech.game.jsre.common.packets;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 16:23
 */
public class LevelTaskPacket extends Packet{
    private QuestInfo questInfo;
    private QuestProgressInfo questProgressInfo;
    private boolean completed;

    public QuestInfo getQuestInfo() {
        return questInfo;
    }

    public void setQuestInfo(QuestInfo questInfo) {
        this.questInfo = questInfo;
    }


    public QuestProgressInfo getQuestProgressInfo() {
        return questProgressInfo;
    }

    public void setQuestProgressInfo(QuestProgressInfo questProgressInfo) {
        this.questProgressInfo = questProgressInfo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        completed = true;
    }

    @Override
    public String toString() {
        return "LevelTaskPacket{" +
                "questInfo=" + questInfo +
                ", questProgressInfo=" + questProgressInfo +
                ", completed=" + completed +
                '}';
    }
}
