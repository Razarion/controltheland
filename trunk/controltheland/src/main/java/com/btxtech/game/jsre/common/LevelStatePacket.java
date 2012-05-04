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

package com.btxtech.game.jsre.common;

import com.btxtech.game.jsre.client.common.LevelScope;

/**
 * User: beat Date: 12.05.2010 Time: 14:38:58
 */
public class LevelStatePacket extends Packet {
    private Integer xp;
    private Integer xp2LevelUp;
    private String activeQuestTitle;
    private String activeQuestProgress;
    private Integer activeQuestLevelTaskId;
    private boolean questDeactivated;
    private Integer questsDone;
    private Integer totalQuests;
    private Integer missionsDone;
    private Integer totalMissions;
    private boolean missionQuestCompleted;
    private LevelScope levelScope;

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getXp2LevelUp() {
        return xp2LevelUp;
    }

    public void setXp2LevelUp(Integer xp2LevelUp) {
        this.xp2LevelUp = xp2LevelUp;
    }

    public String getActiveQuestTitle() {
        return activeQuestTitle;
    }

    public void setActiveQuestTitle(String activeQuestTitle) {
        this.activeQuestTitle = activeQuestTitle;
    }

    public String getActiveQuestProgress() {
        return activeQuestProgress;
    }

    public void setActiveQuestProgress(String questProgress) {
        this.activeQuestProgress = questProgress;
    }

    public Integer getActiveQuestLevelTaskId() {
        return activeQuestLevelTaskId;
    }

    public void setActiveQuestLevelTaskId(Integer activeQuestLevelTaskId) {
        this.activeQuestLevelTaskId = activeQuestLevelTaskId;
    }

    public boolean isQuestDeactivated() {
        return questDeactivated;
    }

    public void setQuestDeactivated(boolean questDeactivated) {
        this.questDeactivated = questDeactivated;
    }

    public Integer getQuestsDone() {
        return questsDone;
    }

    public void setQuestsDone(Integer questsDone) {
        this.questsDone = questsDone;
    }

    public Integer getTotalQuests() {
        return totalQuests;
    }

    public void setTotalQuests(Integer totalQuests) {
        this.totalQuests = totalQuests;
    }

    public Integer getMissionsDone() {
        return missionsDone;
    }

    public void setMissionsDone(Integer missionsDone) {
        this.missionsDone = missionsDone;
    }

    public Integer getTotalMissions() {
        return totalMissions;
    }

    public void setTotalMissions(Integer totalMissions) {
        this.totalMissions = totalMissions;
    }

    public boolean isMissionQuestCompleted() {
        return missionQuestCompleted;
    }

    public void setMissionQuestCompleted(boolean missionQuestCompleted) {
        this.missionQuestCompleted = missionQuestCompleted;
    }

    public LevelScope getLevel() {
        return levelScope;
    }

    public void setLevel(LevelScope levelScope) {
        this.levelScope = levelScope;
    }

    public void merge(LevelStatePacket previous) {
        if (xp == null) {
            xp = previous.xp;
        }
        if (xp2LevelUp == null) {
            xp2LevelUp = previous.xp2LevelUp;
        }
        if (activeQuestTitle == null) {
            activeQuestTitle = previous.activeQuestTitle;
        }
        if (activeQuestProgress == null) {
            activeQuestProgress = previous.activeQuestProgress;
        }
        if (activeQuestLevelTaskId == null) {
            activeQuestLevelTaskId = previous.activeQuestLevelTaskId;
        }
        if (!questDeactivated) {
            questDeactivated = previous.questDeactivated;
        }
        if (questsDone == null) {
            questsDone = previous.questsDone;
        }
        if (totalQuests == null) {
            totalQuests = previous.totalQuests;
        }
        if (missionsDone == null) {
            missionsDone = previous.missionsDone;
        }
        if (totalMissions == null) {
            totalMissions = previous.totalMissions;
        }
        if (!missionQuestCompleted) {
            missionQuestCompleted = previous.missionQuestCompleted;
        }
        if (levelScope == null) {
            levelScope = previous.levelScope;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LevelStatePacket that = (LevelStatePacket) o;

        return missionQuestCompleted == that.missionQuestCompleted
                && questDeactivated == that.questDeactivated
                && !(activeQuestLevelTaskId != null ? !activeQuestLevelTaskId.equals(that.activeQuestLevelTaskId) : that.activeQuestLevelTaskId != null)
                && !(activeQuestProgress != null ? !activeQuestProgress.equals(that.activeQuestProgress) : that.activeQuestProgress != null)
                && !(activeQuestTitle != null ? !activeQuestTitle.equals(that.activeQuestTitle) : that.activeQuestTitle != null)
                && !(levelScope != null ? !levelScope.equals(that.levelScope) : that.levelScope != null)
                && !(missionsDone != null ? !missionsDone.equals(that.missionsDone) : that.missionsDone != null)
                && !(questsDone != null ? !questsDone.equals(that.questsDone) : that.questsDone != null)
                && !(totalMissions != null ? !totalMissions.equals(that.totalMissions) : that.totalMissions != null)
                && !(totalQuests != null ? !totalQuests.equals(that.totalQuests) : that.totalQuests != null)
                && !(xp != null ? !xp.equals(that.xp) : that.xp != null)
                && !(xp2LevelUp != null ? !xp2LevelUp.equals(that.xp2LevelUp) : that.xp2LevelUp != null);
    }

    @Override
    public int hashCode() {
        int result = xp != null ? xp.hashCode() : 0;
        result = 31 * result + (xp2LevelUp != null ? xp2LevelUp.hashCode() : 0);
        result = 31 * result + (activeQuestTitle != null ? activeQuestTitle.hashCode() : 0);
        result = 31 * result + (activeQuestProgress != null ? activeQuestProgress.hashCode() : 0);
        result = 31 * result + (activeQuestLevelTaskId != null ? activeQuestLevelTaskId.hashCode() : 0);
        result = 31 * result + (questDeactivated ? 1 : 0);
        result = 31 * result + (questsDone != null ? questsDone.hashCode() : 0);
        result = 31 * result + (totalQuests != null ? totalQuests.hashCode() : 0);
        result = 31 * result + (missionsDone != null ? missionsDone.hashCode() : 0);
        result = 31 * result + (totalMissions != null ? totalMissions.hashCode() : 0);
        result = 31 * result + (missionQuestCompleted ? 1 : 0);
        result = 31 * result + (levelScope != null ? levelScope.hashCode() : 0);
        return result;
    }
}
