package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.quest.images.QuestVisualisationImageBundle;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestVisualisationPanel extends Composite {

    private static QuestVisualisationPanelUiBinder uiBinder = GWT.create(QuestVisualisationPanelUiBinder.class);
    @UiField
    HTML descriptionHtml;
    @UiField
    FlexTable progressTable;
    private static Logger log = Logger.getLogger(QuestVisualisationPanel.class.getName());
    private boolean hideQuestProgress;

    interface QuestVisualisationPanelUiBinder extends UiBinder<Widget, QuestVisualisationPanel> {
    }

    public QuestVisualisationPanel(QuestInfo questInfo) {
        initWidget(uiBinder.createAndBindUi(this));
        descriptionHtml.setHTML(questInfo.getAdditionDescription());
        hideQuestProgress = questInfo.isHideQuestProgress();
    }

    public void update(QuestProgressInfo questProgressInfo) {
        progressTable.removeAllRows();

        if (hideQuestProgress) {
            return;
        }

        switch (questProgressInfo.getConditionTrigger()) {
            case BASE_KILLED:
                displayAmount(questProgressInfo.getAmount(), "bases destroyed");
                break;
            case MONEY_INCREASED:
                displayAmount(questProgressInfo.getAmount(), "oli collected");
                break;
            case SYNC_ITEM_BUILT:
                displayItemAmount(questProgressInfo.getItemIdAmounts(), "built");
                displayAmount(questProgressInfo.getAmount(), "units/structures built");
                break;
            case SYNC_ITEM_KILLED:
                displayItemAmount(questProgressInfo.getItemIdAmounts(), "destroyed");
                displayAmount(questProgressInfo.getAmount(), "units/structures destroyed");
                break;
            case SYNC_ITEM_POSITION:
                displayItemAmount(questProgressInfo.getItemIdAmounts(), "available");
                displayAmount(questProgressInfo.getAmount(), "minutes left");
                break;
            default:
                log.severe("QuestVisualisationPanel.update() unknown ConditionTrigger: " + questProgressInfo.getConditionTrigger());
        }
    }

    private void displayItemAmount(Map<Integer, QuestProgressInfo.Amount> itemIdAmount, String actionWord) {
        if (itemIdAmount == null) {
            return;
        }
        List<Integer> itemIds = new ArrayList<Integer>(itemIdAmount.keySet());
        Collections.sort(itemIds, new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }

        });
        for (Integer itemId : itemIds) {
            QuestProgressInfo.Amount amount = itemIdAmount.get(itemId);
            int row = progressTable.getRowCount();
            if (amount.isFulfilled()) {
                progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.tick()));
            } else {
                progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.exclamation()));
            }
            progressTable.setText(row, 2, amount.getAmount() + "/" + amount.getTotalAmount());
            String itemName = "";
            try {
                ItemType itemType = ItemTypeContainer.getInstance().getItemType(itemId);
                itemName = itemType.getName();
                progressTable.setWidget(row, 3, ImageHandler.getItemTypeImage(itemType, 30, 30));
            } catch (NoSuchItemTypeException e) {
                log.log(Level.WARNING, "QuestVisualisationPanel.displayItemAmount()", e);
            }
            progressTable.setWidget(row, 4, new HTML("<span style='font-weight:bold;'>" + itemName + "</span> " + actionWord));

        }
    }

    private void displayAmount(QuestProgressInfo.Amount amount, String actionWord) {
        if (amount == null) {
            return;
        }
        int row = progressTable.getRowCount();
        if (amount.isFulfilled()) {
            progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.tick()));
        } else {
            progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.exclamation()));
        }
        progressTable.setText(row, 2, amount.getAmount() + "/" + amount.getTotalAmount());
        progressTable.setText(row, 4, actionWord);
    }
}
