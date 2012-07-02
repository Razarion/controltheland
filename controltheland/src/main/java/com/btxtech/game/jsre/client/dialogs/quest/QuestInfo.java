package com.btxtech.game.jsre.client.dialogs.quest;

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

    /**
     * Used by GWT
     */
    QuestInfo() {
    }

    public QuestInfo(String title, String description, int xp, int gold, int id, Type type) {
        this.title = title;
        this.description = description;
        this.gold = gold;
        this.xp = xp;
        this.id = id;
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestInfo questInfo = (QuestInfo) o;

        return gold == questInfo.gold
                && id == questInfo.id
                && xp == questInfo.xp
                && description.equals(questInfo.description)
                && title.equals(questInfo.title)
                && type == questInfo.type;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + xp;
        result = 31 * result + gold;
        result = 31 * result + type.hashCode();
        result = 31 * result + id;
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
                '}';
    }
}
