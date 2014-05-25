package com.btxtech.game.services.utg;

import java.util.List;

/**
 * Created by beat on 13.05.2014.
 */
public class TutorialStatisticDto {
    private String tutorialName;
    private int tutorialStarted;
    private List<TutorialQuestEntry> tutorialQuestEntries;

    public TutorialStatisticDto(String tutorialName, int tutorialStarted, List<TutorialQuestEntry> tutorialQuestEntries) {
        this.tutorialName = tutorialName;
        this.tutorialStarted = tutorialStarted;
        this.tutorialQuestEntries = tutorialQuestEntries;
    }

    public String getTutorialName() {
        return tutorialName;
    }

    public int getTutorialStarted() {
        return tutorialStarted;
    }

    public List<TutorialQuestEntry> getTutorialQuestEntries() {
        return tutorialQuestEntries;
    }

    public static class TutorialQuestEntry {
        private String questName;
        private int passed;
        private String percentage;

        public TutorialQuestEntry(String questName, int passed, String percentage) {
            this.questName = questName;
            this.passed = passed;
            this.percentage = percentage;
        }

        public String getQuestName() {
            return questName;
        }

        public int getPassed() {
            return passed;
        }

        public String getPercentage() {
            return percentage;
        }
    }
}
