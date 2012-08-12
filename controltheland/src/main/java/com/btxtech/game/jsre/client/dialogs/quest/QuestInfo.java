package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.common.Index;

import java.io.Serializable;

public class QuestInfo implements Serializable {
    public enum Type {
        QUEST, MISSION
    }

    private String title;
    private String description;
    private int xp;
    private int gold;
    private Type type;
    private int id;
    private Index radarPosition;

    /**
     * Used by GWT
     */
    QuestInfo() {
    }

    public QuestInfo(String title, String description, int xp, int gold, int id, Type type, Index radarPosition) {
        this.title = title;
        this.description = description;
        this.gold = gold;
        this.xp = xp;
        this.id = id;
        this.type = type;
        this.radarPosition = radarPosition;
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

    public void setRadarPosition(Index radarPosition) {
        this.radarPosition = radarPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestInfo questInfo = (QuestInfo) o;

        return gold == questInfo.gold
                && id == questInfo.id
                && xp == questInfo.xp
                && !(description != null ? !description.equals(questInfo.description) : questInfo.description != null)
                && !(radarPosition != null ? !radarPosition.equals(questInfo.radarPosition) : questInfo.radarPosition != null)
                && !(title != null ? !title.equals(questInfo.title) : questInfo.title != null)
                && type == questInfo.type;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + xp;
        result = 31 * result + gold;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (radarPosition != null ? radarPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestInfo{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", xp=" + xp +
                ", gold=" + gold +
                ", type=" + type +
                ", id=" + id +
                ", radarPosition=" + radarPosition +
                '}';
    }
}
