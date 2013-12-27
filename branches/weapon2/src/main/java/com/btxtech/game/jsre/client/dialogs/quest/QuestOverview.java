package com.btxtech.game.jsre.client.dialogs.quest;

import java.io.Serializable;
import java.util.List;

/**
 * User: beat
 * Date: 29.06.12
 * Time: 16:02
 */
public class QuestOverview implements Serializable {
    private List<QuestInfo> questInfos;
    private int questsDone;
    private int totalQuests;
    private int missionsDone;
    private int totalMissions;

    public List<QuestInfo> getQuestInfos() {
        return questInfos;
    }

    public void setQuestInfos(List<QuestInfo> questInfos) {
        this.questInfos = questInfos;
    }

    public int getQuestsDone() {
        return questsDone;
    }

    public void setQuestsDone(int questsDone) {
        this.questsDone = questsDone;
    }

    public int getTotalQuests() {
        return totalQuests;
    }

    public void setTotalQuests(int totalQuests) {
        this.totalQuests = totalQuests;
    }

    public int getMissionsDone() {
        return missionsDone;
    }

    public void setMissionsDone(int missionsDone) {
        this.missionsDone = missionsDone;
    }

    public int getTotalMissions() {
        return totalMissions;
    }

    public void setTotalMissions(int totalMissions) {
        this.totalMissions = totalMissions;
    }
}
