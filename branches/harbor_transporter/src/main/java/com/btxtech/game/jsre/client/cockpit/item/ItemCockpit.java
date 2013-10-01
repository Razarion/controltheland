package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.MinimizeButton;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat Date: 08.11.2011 Time: 00:29:45
 */
public class ItemCockpit extends Composite implements SelectionListener {
    private static ItemCockpitUiBinder uiBinder = GWT.create(ItemCockpitUiBinder.class);
    @UiField
    SimplePanel buildupPanel;
    @UiField
    SimplePanel infoPanel;
    @UiField
    SimplePanel specialFunctionPanel;
    private static final ItemCockpit INSTANCE = new ItemCockpit();
    private boolean isActive = false;
    private BuildupItemPanel buildupItemPanel;
    private MinimizeButton minimizeButton = new MinimizeButton(false);

    interface ItemCockpitUiBinder extends UiBinder<Widget, ItemCockpit> {
    }

    public static ItemCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ItemCockpit() {
        initWidget(uiBinder.createAndBindUi(this));
        minimizeButton.addWidgetToHide(this);
    }

    public void addToParentAndRegister(AbsolutePanel parentPanel) {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.PLAYBACK) {
            SelectionHandler.getInstance().addSelectionListener(this);
            parentPanel.add(this, 0, 0);
            getElement().getStyle().setZIndex(Constants.Z_INDEX_ITEM_COCKPIT);
            getElement().getStyle().clearTop();
            getElement().getStyle().setBottom(0, Style.Unit.PX);
            parentPanel.add(minimizeButton, 0, 0);
            minimizeButton.setZIndex(Constants.Z_INDEX_ITEM_COCKPIT_MINIMIZE_BUTTON);
            minimizeButton.getElement().getStyle().clearTop();
            minimizeButton.getElement().getStyle().setBottom(0, Style.Unit.PX);
            showPanel(false);
        }
    }

    private void activeOwnSingle(SyncBaseItem syncBaseItem) {
        String debugInfo = null;
        if (Game.isDebug()) {
            debugInfo = "{" + syncBaseItem.getId() + "}";
        }
        infoPanel.setWidget(new OwnInfoPanel(syncBaseItem.getBaseItemType(), 1, debugInfo));
        if (SpecialFunctionPanel.hasSpecialFuntion(syncBaseItem)) {
            specialFunctionPanel.setWidget(new SpecialFunctionPanel(syncBaseItem));
            specialFunctionPanel.setVisible(true);
        }
        if (syncBaseItem.hasSyncFactory() || syncBaseItem.hasSyncBuilder()) {
            buildupItemPanel = new BuildupItemPanel();
            buildupItemPanel.display(syncBaseItem);
            buildupPanel.setWidget(buildupItemPanel);
            buildupPanel.setVisible(true);
        }
    }

    private void activeOwnMultiSameType(BaseItemType baseItemType, Group group) {
        infoPanel.setWidget(new OwnInfoPanel(baseItemType, group.getCount(), null));
        if (baseItemType.getFactoryType() != null || baseItemType.getBuilderType() != null) {
            buildupItemPanel = new BuildupItemPanel();
            buildupItemPanel.display(group);
            buildupPanel.setWidget(buildupItemPanel);
            buildupPanel.setVisible(true);
        }
    }

    private void cleanPanels() {
        buildupPanel.clear();
        buildupPanel.setVisible(false);
        infoPanel.clear();
        specialFunctionPanel.clear();
        specialFunctionPanel.setVisible(false);
        buildupItemPanel = null;
    }

    public boolean isActive() {
        return isActive;
    }

    public void onMoneyChanged(double accountBalance) {
        if (buildupItemPanel != null) {
            buildupItemPanel.onMoneyChanged(accountBalance);
        }
    }

    public void onStateChanged() {
        if (buildupItemPanel != null) {
            buildupItemPanel.onStateChanged();
        }
    }

    @Override
    public void onTargetSelectionChanged(SyncItem selection) {
        minimizeButton.maximize();
        cleanPanels();
        infoPanel.setWidget(new OtherInfoPanel(selection));
        isActive = true;
        showPanel(true);
        ClientUserTracker.getInstance().onDialogAppears(this, "ItemCockpit");
    }

    @Override
    public void onSelectionCleared() {
        cleanPanels();
        if (isActive) {
            ClientUserTracker.getInstance().onDialogDisappears(this);
            isActive = false;
            showPanel(false);
        }
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        minimizeButton.maximize();
        cleanPanels();
        if (selectedGroup.getCount() == 1) {
            activeOwnSingle(selectedGroup.getFirst());
        } else {
            Map<BaseItemType, Collection<SyncBaseItem>> itemTypes = selectedGroup.getGroupedItems();
            if (itemTypes.size() == 1) {
                activeOwnMultiSameType(CommonJava.getFirst(itemTypes.keySet()), selectedGroup);
            } else {
                infoPanel.setWidget(new OwnMultiDifferentItemPanel(itemTypes));
            }
        }
        isActive = true;
        showPanel(true);
        ClientUserTracker.getInstance().onDialogAppears(this, "ItemCockpit");
    }

    public Index getAbsoluteMiddleTopPositionFromBuildupPanel(int buildupItemTypeId) {
        if (buildupItemPanel == null) {
            throw new IllegalArgumentException("ItemCockpit.getAbsoluteMiddleTopPositionFromBuildupPanel() buildupItemPanel is null. buildupItemTypeId: " + buildupItemTypeId);
        }
        return buildupItemPanel.getAbsoluteMiddleTopPosition(buildupItemTypeId);
    }

    public boolean isInside(int x, int y) {
        return new Rectangle(getAbsoluteLeft(), getAbsoluteTop(), getOffsetWidth(), getOffsetHeight()).contains(new Index(x, y));
    }

    private void showPanel(boolean visible) {
        setVisible(visible);
        minimizeButton.setVisible(visible);
    }
}
