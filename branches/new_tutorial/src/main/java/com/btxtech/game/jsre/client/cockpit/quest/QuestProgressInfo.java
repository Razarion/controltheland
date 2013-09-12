package com.btxtech.game.jsre.client.cockpit.quest;

import java.io.Serializable;
import java.util.Map;

import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;

public class QuestProgressInfo implements Serializable {
    private ConditionTrigger conditionTrigger;
    private Map<Integer, Amount> itemIdAmounts;
    private Map<InventoryArtifactInfo, QuestProgressInfo.Amount> inventoryArtifactInfoAmount;
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

    public Map<InventoryArtifactInfo, Amount> getInventoryArtifactInfoAmount() {
        return inventoryArtifactInfoAmount;
    }

    public void setInventoryArtifactInfoAmount(Map<InventoryArtifactInfo, Amount> inventoryArtifactInfoAmount) {
        this.inventoryArtifactInfoAmount = inventoryArtifactInfoAmount;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "QuestProgressInfo{" +
                "conditionTrigger=" + conditionTrigger +
                ", itemIdAmounts=" + itemIdAmounts +
                ", inventoryArtifactInfoAmount=" + inventoryArtifactInfoAmount +
                ", amount=" + amount +
                '}';
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Amount amount1 = (Amount) o;

            return amount == amount1.amount && totalAmount == amount1.totalAmount;
        }

        @Override
        public int hashCode() {
            int result = amount;
            result = 31 * result + totalAmount;
            return result;
        }

        @Override
        public String toString() {
            return "Amount{" +
                    "amount=" + amount +
                    ", totalAmount=" + totalAmount +
                    '}';
        }
    }
}
