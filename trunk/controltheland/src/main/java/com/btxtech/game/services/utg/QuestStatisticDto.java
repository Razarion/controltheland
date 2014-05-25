package com.btxtech.game.services.utg;

import java.util.List;

/**
 * Created by beat
 * on 13.05.2014.
 */
public class QuestStatisticDto {
    private int levelNumber;
    private List<QuestEntry> questEntries;

    public QuestStatisticDto(int levelNumber, List<QuestEntry> questEntries) {
        this.levelNumber = levelNumber;
        this.questEntries = questEntries;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public List<QuestEntry> getQuestEntries() {
        return questEntries;
    }

    public static class QuestEntry {
        private String questName;
        private int passed;

        public QuestEntry(String questName, int passed) {
            this.questName = questName;
            this.passed = passed;
        }

        public String getQuestName() {
            return questName;
        }

        public int getPassed() {
            return passed;
        }
    }
}
