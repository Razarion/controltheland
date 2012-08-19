package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.AbstractControlPanel;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 08.11.2011
 * Time: 00:29:45
 */
public class ItemCockpit extends AbstractControlPanel implements SelectionListener {
    private static final ItemCockpit INSTANCE = new ItemCockpit();
    private boolean isActive = false;
    private VerticalPanel verticalPanel;

    public static ItemCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemCockpit() {
        setup();
    }

    public void addToParentAndRegister(AbsolutePanel parentPanel) {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.PLAYBACK) {
            SelectionHandler.getInstance().addSelectionListener(this);
            parentPanel.add(this, 10, 0);
            setVisible(false);
            getElement().getStyle().setZIndex(Constants.Z_INDEX_ITEM_COCKPIT);
            getElement().getStyle().clearTop();
            getElement().getStyle().setBottom(5, Style.Unit.PX);
        }
    }

    private void activeOwnSingle(SyncBaseItem syncBaseItem) {
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(new OwnInfoPanel(syncBaseItem));
        horizontalPanel.add(new SpecialFunctionPanel(syncBaseItem));
        verticalPanel.add(horizontalPanel);
        if(syncBaseItem.hasSyncFactory() || syncBaseItem.hasSyncBuilder()) {
            BuildupItemPanel buildupItemPanel = new BuildupItemPanel();
            buildupItemPanel.display(syncBaseItem);
            verticalPanel.add(buildupItemPanel);
        }
        setVisible(true);
        ClientUserTracker.getInstance().onDialogAppears(this, "ItemCockpit");
        isActive = true;
    }

    private void activeOwnMultiDifferentType(Map<BaseItemType, Collection<SyncBaseItem>> itemTypes) {
       // TODO
    }

    private void activeOwnMultiSameType(BaseItemType first, Collection<SyncBaseItem> first1) {
       // TODO
    }

    private void activateOther(SyncItem syncItem) {
        verticalPanel.clear();
        verticalPanel.add(new OtherInfoPanel(syncItem));
        setVisible(true);
        ClientUserTracker.getInstance().onDialogAppears(this, "ItemCockpit");
        isActive = true;
    }

    private void deActivate() {
        if (isActive) {
            ClientUserTracker.getInstance().onDialogDisappears(this);
            isActive = false;
            verticalPanel.clear();
            setVisible(false);
        }
    }

    @Override
    protected Widget createBody() {
        verticalPanel = new VerticalPanel();
        verticalPanel.getElement().getStyle().setColor("#C2D7EC");
        return verticalPanel;
    }

    public boolean isActive() {
        return isActive;
    }

    public void onMoneyChanged(double accountBalance) {
        // TODO buildupItemPanel.onMoneyChanged(accountBalance);
    }

    public void onStateChanged() {
        // TODO buildupItemPanel.onStateChanged();
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        activateOther(selection);
    }

    @Override
    public void onSelectionCleared() {
        deActivate();
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        if(selectedGroup.getCount() == 1) {
            activeOwnSingle(selectedGroup.getFirst());
        } else {
            Map<BaseItemType, Collection<SyncBaseItem>> itemTypes = selectedGroup.getGroupedItems();
            if(itemTypes.size() == 1) {
                activeOwnMultiSameType(CommonJava.getFirst(itemTypes.keySet()), CommonJava.getFirst(itemTypes.values()));
            } else {
                activeOwnMultiDifferentType(itemTypes);
            }
        }
    }
}
