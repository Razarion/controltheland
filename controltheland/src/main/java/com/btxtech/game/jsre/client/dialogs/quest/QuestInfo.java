package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;

public class QuestInfo implements Serializable {
    public enum Type {
        QUEST, MISSION
    }

    private String title;
    private String description;
    private QuestTypeEnum questTypeEnum;
    private int xp;
    private String additionDescription;
    private int gold;
    private Type type;
    private int id;
    private Index radarPosition;
    private boolean hideQuestProgress;
    private Integer unlockCrystals;

    /**
     * Used by GWT
     */
    QuestInfo() {
    }

    public QuestInfo(String title, String description, String additionDescription, QuestTypeEnum questTypeEnum, int xp, int gold, int id, Type type, Index radarPosition, boolean hideQuestProgress, Integer unlockCrystals) {
        this.title = title;
        this.description = description;
        this.additionDescription = additionDescription;
        this.questTypeEnum = questTypeEnum;
        this.gold = gold;
        this.xp = xp;
        this.id = id;
        this.type = type;
        this.radarPosition = radarPosition;
        this.hideQuestProgress = hideQuestProgress;
        this.unlockCrystals = unlockCrystals;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getXp() {
        return xp;
    }

    public int getGold() {
        return gold;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Index getRadarPosition() {
        return radarPosition;
    }

    public String getAdditionDescription() {
        return additionDescription;
    }

    public boolean isHideQuestProgress() {
        return hideQuestProgress;
    }

    public QuestTypeEnum getQuestTypeEnum() {
        return questTypeEnum;
    }

    public boolean isUnlockNeeded() {
        return unlockCrystals != null;
    }

    public Integer getUnlockCrystals() {
        return unlockCrystals;
    }

    public void setUnlockCrystals(Integer unlockCrystals) {
        this.unlockCrystals = unlockCrystals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestInfo questInfo = (QuestInfo) o;

        return gold == questInfo.gold
                && hideQuestProgress == questInfo.hideQuestProgress
                && id == questInfo.id
                && xp == questInfo.xp
                && !(questTypeEnum != null ? !questTypeEnum.equals(questInfo.questTypeEnum) : questInfo.questTypeEnum != null)
                && !(additionDescription != null ? !additionDescription.equals(questInfo.additionDescription) : questInfo.additionDescription != null)
                && !(description != null ? !description.equals(questInfo.description) : questInfo.description != null)
                && !(radarPosition != null ? !radarPosition.equals(questInfo.radarPosition) : questInfo.radarPosition != null)
                && !(title != null ? !title.equals(questInfo.title) : questInfo.title != null)
                && !(unlockCrystals != null ? !unlockCrystals.equals(questInfo.unlockCrystals) : questInfo.unlockCrystals != null)
                && type == questInfo.type;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + xp;
        result = 31 * result + (questTypeEnum != null ? questTypeEnum.hashCode() : 0);
        result = 31 * result + (additionDescription != null ? additionDescription.hashCode() : 0);
        result = 31 * result + gold;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (radarPosition != null ? radarPosition.hashCode() : 0);
        result = 31 * result + (unlockCrystals != null ? unlockCrystals.hashCode() : 0);
        result = 31 * result + (hideQuestProgress ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestInfo{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", additionDescription='" + additionDescription + '\'' +
                ", questTypeEnum='" + questTypeEnum + '\'' +
                ", xp=" + xp +
                ", gold=" + gold +
                ", type=" + type +
                ", id=" + id +
                ", radarPosition=" + radarPosition +
                ", hideQuestProgress=" + hideQuestProgress +
                '}';
    }
}
