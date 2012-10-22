package com.btxtech.game.jsre.client.cockpit.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.quest.images.QuestVisualisationImageBundle;
import com.btxtech.game.jsre.client.item.ItemTypeContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class QuestVisualtisationPanel extends Composite {

    private static QuestVisualtisationPanelUiBinder uiBinder = GWT.create(QuestVisualtisationPanelUiBinder.class);
    @UiField
    Label descriptionLabel;
    @UiField
    FlexTable progressTable;
    private static Logger log = Logger.getLogger(QuestVisualtisationPanel.class.getName());

    interface QuestVisualtisationPanelUiBinder extends UiBinder<Widget, QuestVisualtisationPanel> {
    }

    public QuestVisualtisationPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void update(QuestProgressInfo questProgressInfo) {
        progressTable.removeAllRows();

        switch (questProgressInfo.getConditionTrigger()) {
        case BASE_KILLED:
            displayAmount(questProgressInfo.getAmount(), "Bases destroyed");
            break;
        case MONEY_INCREASED:
            displayAmount(questProgressInfo.getAmount(), "Oli collected");
            break;
        case SYNC_ITEM_BUILT:
            displayItems(questProgressInfo.getItemIdAmounts(), "built");
            break;
        case SYNC_ITEM_KILLED:
            displayItems(questProgressInfo.getItemIdAmounts(), "destroyed");
            break;
        case SYNC_ITEM_POSITION:
            displayItems(questProgressInfo.getItemIdAmounts(), "available");
            displayAmount(questProgressInfo.getAmount(), "Bases destroyed");
            break;
        default:
            log.severe("QuestVisualtisationPanel.update() unknwon ConditionTrigger: " + questProgressInfo.getConditionTrigger());
        }
    }

    private void displayItems(Map<Integer, QuestProgressInfo.Amount> itemIdAmount, String actionWord) {
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
            String itemName = "";
            try {
                ItemType itemType = ItemTypeContainer.getInstance().getItemType(itemId);
                itemName = itemType.getName();
                progressTable.setWidget(row, 2, ImageHandler.getItemTypeImage(itemType, 10, 10));
            } catch (NoSuchItemTypeException e) {
                log.log(Level.WARNING, "QuestVisualtisationPanel.fillItemTypeTable()", e);
            }
            progressTable.setText(row, 3, amount.getAmount() + " / " + amount.getTotalAmount() + " of " + itemName + " " + actionWord);

        }
    }

    private void displayAmount(QuestProgressInfo.Amount amount, String actionWord) {
        if(actionWord == null) {
            return;
        }
        int row = progressTable.getRowCount();
        if (amount.isFulfilled()) {
            progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.tick()));
        } else {
            progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.exclamation()));
        }
        progressTable.setText(row, 3, amount.getAmount() + " / " + amount.getTotalAmount() + " " + actionWord);
    }
}
