package com.btxtech.game.jsre.client.dialogs.inventory;

import java.util.Collection;
import java.util.Map;

import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;

public class MainTabbedPanel extends Composite {
    private static final int INVENTORY_COLUMS = 3;
    private static final int WORKSHOP_COLUMS = 3;
    private static final int DEALER_ITEMS_COLUMS = 3;
    private static final int DEALER_ARTIFACT_COLUMS = 3;
    private static MainTabbedPanelUiBinder uiBinder = GWT.create(MainTabbedPanelUiBinder.class);
    @UiField
    FlexTable inventoryTable;
    @UiField
    TabPanel tabPanel;
    @UiField
    FlexTable workshopTable;
    @UiField
    FlexTable itemTable;
    @UiField
    FlexTable artefactTable;
    @UiField
    FlexTable fundsTable;
    @UiField
    TabPanel dealerPanel;
    @UiField Label razarionLabel;
    @UiField ListBox filterListBox;
    private InventoryDialog inventoryDialog;

    interface MainTabbedPanelUiBinder extends UiBinder<Widget, MainTabbedPanel> {
    }

    public MainTabbedPanel(InventoryDialog inventoryDialog) {
        initWidget(uiBinder.createAndBindUi(this));
        this.inventoryDialog = inventoryDialog;
        tabPanel.selectTab(0);
        dealerPanel.selectTab(0);
        filterListBox.addItem("Current Planet & Level", "value 1");
        filterListBox.addItem("Sirus", "value 2");
        filterListBox.addItem("Planet2", "value 3");
        filterListBox.addItem("Planet3", "value 4");
        filterListBox.addItem("All", "value 5");
        
    }

    public void setRazarionAmount(int razarion) {
        razarionLabel.setText("Razarion: " + razarion);

    }

    public void displayInventory(Map<InventoryItemInfo, Integer> ownInventoryItems) {
        inventoryTable.removeAllRows();
        int column = 0;
        int row = 0;
        for (Map.Entry<InventoryItemInfo, Integer> entry : ownInventoryItems.entrySet()) {
            if (column >= INVENTORY_COLUMS) {
                column = 0;
                row++;
            }
            inventoryTable.setWidget(row, column, new InventoryItem(entry.getKey(), entry.getValue(), inventoryDialog));
            column++;
        }
    }

    public void displayWorkshop(InventoryInfo inventoryInfo, Collection<InventoryItemInfo> allInventoryItemInfos) {
        workshopTable.removeAllRows();
        int column = 0;
        int row = 0;
        for (InventoryItemInfo inventoryItemInfo : allInventoryItemInfos) {
            if (inventoryItemInfo.hasArtifacts()) {
                if (column >= WORKSHOP_COLUMS) {
                    column = 0;
                    row++;
                }
                workshopTable.setWidget(row, column, new WorkshopPlate(inventoryItemInfo, inventoryInfo.getOwnInventoryArtifacts(), inventoryDialog));
                column++;
            }
        }
    }

    public void displayDealerItems(InventoryInfo inventoryInfo, Collection<InventoryItemInfo> allInventoryItemInfos) {
        itemTable.removeAllRows();
        int column = 0;
        int row = 0;
        for (InventoryItemInfo inventoryItemInfo : allInventoryItemInfos) {
            if (inventoryItemInfo.hasRazarionCoast() && inventoryItemInfo.hasBaseItemTypeId()) {
                if (column >= DEALER_ITEMS_COLUMS) {
                    column = 0;
                    row++;
                }
                itemTable.setWidget(row, column, new MarketItem(inventoryItemInfo, inventoryInfo.getRazarion(), inventoryDialog));
                column++;
            }
        }
    }

    public void displayDealerFunds(InventoryInfo inventoryInfo, Collection<InventoryItemInfo> allInventoryItemInfos) {
        fundsTable.removeAllRows();
        int column = 0;
        int row = 0;
        for (InventoryItemInfo inventoryItemInfo : allInventoryItemInfos) {
            if (inventoryItemInfo.hasRazarionCoast() && !inventoryItemInfo.hasBaseItemTypeId()) {
                if (column >= DEALER_ITEMS_COLUMS) {
                    column = 0;
                    row++;
                }
                fundsTable.setWidget(row, column, new MarketItem(inventoryItemInfo, inventoryInfo.getRazarion(), inventoryDialog));
                column++;
            }
        }
    }

    public void displayDealerArtifacts(InventoryInfo inventoryInfo, Collection<InventoryArtifactInfo> allInventoryArtifactInfos) {
        artefactTable.removeAllRows();
        int column = 0;
        int row = 0;

        for (InventoryArtifactInfo inventoryArtifactInfo : inventoryInfo.getAllInventoryArtifactInfos()) {
            if (inventoryArtifactInfo.hasRazarionCoast()) {
                if (column >= DEALER_ARTIFACT_COLUMS) {
                    column = 0;
                    row++;
                }
                artefactTable.setWidget(row, column, new MarketArtifact(inventoryArtifactInfo, inventoryInfo.getRazarion(), inventoryDialog));
                column++;
            }
        }
    }
    @UiHandler("filterListBox")
    void onFilterListBoxChange(ChangeEvent event) {
        System.out.println("filterListBox: " + filterListBox.getSelectedIndex());
    }
}
