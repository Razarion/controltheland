package com.btxtech.game.jsre.client.cockpit.quest;

import java.io.Serializable;
import java.util.Map;

import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;

public class QuestProgressInfo implements Serializable {
    private ConditionTrigger conditionTrigger;
    private Map<Integer, Amount> itemIdAmounts;
    private Amount amount;

    /**
     * Used by GWT
     */
    QuestProgressInfo() {
    }

    public QuestProgressInfo(ConditionTrigger conditionTrigger) {
        this.conditionTrigger = conditionTrigger;
    }

    public ConditionTrigger getConditionTrigger() {
        return conditionTrigger;
    }

    public Map<Integer, Amount> getItemIdAmounts() {
        return itemIdAmounts;
    }

    public void setItemIdAmounts(Map<Integer, Amount> itemIdAmount) {
        this.itemIdAmounts = itemIdAmount;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public static class Amount implements Serializable {
        private int amount;
        private int totalAmount;

        /**
         * Used by GWT
         */
        Amount() {
        }

        public Amount(int amount, int totalAmount) {
            this.amount = amount;
            this.totalAmount = totalAmount;
        }

        public int getAmount() {
            return amount;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public boolean isFulfilled() {
            return amount >= totalAmount;
        }
    }
}
