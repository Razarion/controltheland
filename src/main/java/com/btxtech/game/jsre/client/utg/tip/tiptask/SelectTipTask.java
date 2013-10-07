package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.tip.visualization.GameTipVisualization;
import com.btxtech.game.jsre.client.utg.tip.visualization.ItemInGameTipVisualization;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 12:53
 */
public class SelectTipTask extends AbstractTipTask implements SelectionListener {
    private int itemTypeId;

    public SelectTipTask(int itemTypeId) {
        this.itemTypeId = itemTypeId;
        activateConversionOnMouseMove();
    }

    @Override
    public void internalStart() {
        SelectionHandler.getInstance().addSelectionListener(this);
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(false);
    }

    @Override
    public boolean isFulfilled() {
        Group selectedGroup = SelectionHandler.getInstance().getOwnSelection();
        if (selectedGroup == null) {
            return false;
        }
        Map<BaseItemType, Collection<SyncBaseItem>> selectedItems = selectedGroup.getGroupedItems();
        return selectedItems.size() == 1 && CommonJava.getFirst(selectedItems.keySet()).getId() == itemTypeId;
    }

    @Override
    public void internalCleanup() {
        SelectionHandler.getInstance().removeSelectionListener(this);
        QuestVisualisationModel.getInstance().setShowInGameVisualisation(true);
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        onFailed();
    }

    @Override
    public void onSelectionCleared() {
        onFailed();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        Map<BaseItemType, Collection<SyncBaseItem>> selectedItems = selectedGroup.getGroupedItems();
        if (selectedItems.size() == 1 && CommonJava.getFirst(selectedItems.keySet()).getId() == itemTypeId) {
            onSucceed();
        } else {
            onFailed();
        }
    }

    @Override
    public String getTaskText() {
        // TODO use i18n
        // TODO dynamic create item name
        // TODO German flexion
        return "Selektiere deine(n) " + getItemTypeName(itemTypeId) + " mit der Maus";
    }

    public GameTipVisualization createInGameTip() {
        SyncBaseItem syncBaseItem = CommonJava.getFirst(ItemContainer.getInstance().getItems4BaseAndType(ClientBase.getInstance().getSimpleBase(), itemTypeId));
        return new ItemInGameTipVisualization(syncBaseItem);
    }
}
