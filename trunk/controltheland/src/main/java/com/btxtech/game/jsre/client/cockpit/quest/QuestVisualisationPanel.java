package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.quest.images.QuestVisualisationImageBundle;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestVisualisationPanel extends Composite {
    private static final int IMAGE_WIDTH = 30;
    private static final int IMAGE_HEIGHT = 30;
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
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questBasesKilled());
                break;
            case MONEY_INCREASED:
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questOilCollected());
                break;
            case SYNC_ITEM_BUILT:
                displayItemAmount(questProgressInfo.getItemIdAmounts(), ClientI18nHelper.CONSTANTS.questBuilt());
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questUnitStructuresBuilt());
                break;
            case SYNC_ITEM_KILLED:
                displayItemAmount(questProgressInfo.getItemIdAmounts(), ClientI18nHelper.CONSTANTS.questDestroyed());
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questUnitStructuresDestroyed());
                break;
            case SYNC_ITEM_POSITION:
                displayItemAmount(questProgressInfo.getItemIdAmounts(), null);
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questMinutesPast());
                break;
            case RAZARION_INCREASED:
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questRazarionIncreased());
                break;
            case ARTIFACT_ITEM_ADDED:
                displayArtifactItemAmount(questProgressInfo.getInventoryArtifactInfoAmount(), ClientI18nHelper.CONSTANTS.questArtifactItemAddedActionWord());
                displayAmount(questProgressInfo.getAmount(), ClientI18nHelper.CONSTANTS.questArtifactItemAdded());
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
                itemName = ClientI18nHelper.getLocalizedString(itemType.getI18Name());
                progressTable.setWidget(row, 3, ImageHandler.getItemTypeImage(itemType, IMAGE_WIDTH, IMAGE_HEIGHT));
            } catch (NoSuchItemTypeException e) {
                log.log(Level.WARNING, "QuestVisualisationPanel.displayItemAmount()", e);
            }
            progressTable.setWidget(row, 4, new HTML("<span style='font-weight:bold;'>" + itemName + "</span> " + (actionWord != null ? actionWord : "")));
        }
    }

    private void displayArtifactItemAmount(Map<InventoryArtifactInfo, QuestProgressInfo.Amount> inventoryArtifactInfoAmount, String actionWord) {
        if (inventoryArtifactInfoAmount == null) {
            return;
        }

        List<InventoryArtifactInfo> inventoryArtifactInfos = new ArrayList<InventoryArtifactInfo>(inventoryArtifactInfoAmount.keySet());
        Collections.sort(inventoryArtifactInfos, new Comparator<InventoryArtifactInfo>() {

            @Override
            public int compare(InventoryArtifactInfo o1, InventoryArtifactInfo o2) {
                return o1.getInventoryArtifactName().compareTo(o2.getInventoryArtifactName());
            }

        });
        for (InventoryArtifactInfo inventoryArtifactInfo : inventoryArtifactInfos) {
            QuestProgressInfo.Amount amount = inventoryArtifactInfoAmount.get(inventoryArtifactInfo);
            int row = progressTable.getRowCount();
            if (amount.isFulfilled()) {
                progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.tick()));
            } else {
                progressTable.setWidget(row, 1, new Image(QuestVisualisationImageBundle.INSTANCE.exclamation()));
            }
            SimplePanel simplePanel = new SimplePanel();
            simplePanel.setWidget(ImageHandler.getInventoryArtifactImage(inventoryArtifactInfo, IMAGE_WIDTH, IMAGE_HEIGHT));
            simplePanel.getElement().getStyle().setBackgroundColor(inventoryArtifactInfo.getHtmlRarenessColor());
            progressTable.setWidget(row, 3, simplePanel);
            progressTable.setText(row, 2, amount.getAmount() + "/" + amount.getTotalAmount());
            progressTable.setWidget(row, 4, new HTML("<span style='font-weight:bold;'>" + inventoryArtifactInfo.getInventoryArtifactName() + "</span> " + (actionWord != null ? actionWord : "")));
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
        if (actionWord != null) {
            progressTable.setText(row, 4, actionWord);
        }
    }
}
